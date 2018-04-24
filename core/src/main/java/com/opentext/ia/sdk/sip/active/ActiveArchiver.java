/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.active;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.function.BiConsumer;

import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.sip.BatchSipAssembler;
import com.opentext.ia.sdk.sip.BatchSipAssemblerWithCallback;
import com.opentext.ia.sdk.sip.ContentAssembler;
import com.opentext.ia.sdk.sip.ContentAssemblerDefault;
import com.opentext.ia.sdk.sip.DefaultPackagingInformationFactory;
import com.opentext.ia.sdk.sip.DigitalObjectsExtraction;
import com.opentext.ia.sdk.sip.FileGenerationMetrics;
import com.opentext.ia.sdk.sip.OneSipPerDssPackagingInformationFactory;
import com.opentext.ia.sdk.sip.PackagingInformation;
import com.opentext.ia.sdk.sip.PackagingInformationFactory;
import com.opentext.ia.sdk.sip.PdiAssembler;
import com.opentext.ia.sdk.sip.SequentialDssIdSupplier;
import com.opentext.ia.sdk.sip.SipAssembler;
import com.opentext.ia.sdk.sip.SipSegmentationStrategy;
import com.opentext.ia.sdk.support.io.NoHashAssembler;


/**
 * {@linkplain BatchSipAssembler Assemble SIPs} and ingest them into an Archive as soon as they are done.
 * <dl><dt>Warning:</dt><dd>This object is not thread-safe. If you want to use multiple threads to assemble SIPs, let
 * each use their own instance.</dd></dl>
 * @param <D> The type of domain objects to assemble
 */
public class ActiveArchiver<D> {

  private final ArchiveClient archiveClient;
  private final BatchSipAssemblerWithCallback<D> assembler;
  private final BiConsumer<File, IOException> failedSipHandler;

  public ActiveArchiver(SipSegmentationStrategy<D> segmentationStrategy, PackagingInformation packagingInformation,
      String dssPrefix, PdiAssembler<D> pdiAssembler, ArchiveClient archiveClient,
      BiConsumer<File, IOException> failedSipHandler) {
    this(segmentationStrategy, packagingInformation, dssPrefix, pdiAssembler, ignored -> Collections.emptyIterator(),
        archiveClient, failedSipHandler);
  }

  public ActiveArchiver(SipSegmentationStrategy<D> segmentationStrategy, PackagingInformation packagingInformation,
      String dssPrefix, PdiAssembler<D> pdiAssembler, DigitalObjectsExtraction<D> contentsExtraction,
      ArchiveClient archiveClient, BiConsumer<File, IOException> failedSipHandler) {
    this(segmentationStrategy, newSipAssembler(packagingInformation, dssPrefix, pdiAssembler,
        contentsExtraction), archiveClient, failedSipHandler);
  }

  private static <D> SipAssembler<D> newSipAssembler(PackagingInformation prototype, String dssPrefix,
      PdiAssembler<D> pdiAssembler, DigitalObjectsExtraction<D> contentsExtraction) {
    PackagingInformationFactory factory = new OneSipPerDssPackagingInformationFactory(
        new DefaultPackagingInformationFactory(prototype),
        new SequentialDssIdSupplier(dssPrefix, 1));
    ContentAssembler<D> contentsAssembler = new ContentAssemblerDefault<>(contentsExtraction, new NoHashAssembler());
    return SipAssembler.forPdiAndContent(factory, pdiAssembler, contentsAssembler);
  }

  /**
   * Create a new instance.
   * @param segmentationStrategy The strategy to use for determining when to start a new SIP
   * @param sipAssembler The SIP assembler to use. This object must not be used anywhere else but here, especially
   * not in another thread
   * @param archiveClient The client to use for ingesting the assembled SIPs
   * @param failedSipHandler The error handler to call when ingesting a SIP fails
   */
  public ActiveArchiver(SipSegmentationStrategy<D> segmentationStrategy, SipAssembler<D> sipAssembler,
      ArchiveClient archiveClient, BiConsumer<File, IOException> failedSipHandler) {
    this.archiveClient = archiveClient;
    this.failedSipHandler = failedSipHandler;
    this.assembler = new BatchSipAssemblerWithCallback<>(sipAssembler, segmentationStrategy, this::sipAssembled);
  }

  private void sipAssembled(FileGenerationMetrics metrics) {
    File file = metrics.getFile();
    try {
      try (InputStream sip = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
        archiveClient.ingestDirect(sip);
      }
      Files.delete(file.toPath());
    } catch (IOException e) {
      failedSipHandler.accept(file, e);
    }
  }

  /**
   * Add a domain object to the current SIP.
   * @param domainObject The domain object to add
   * @throws IOException when an I/O error occurs
   */
  public void add(D domainObject) throws IOException {
    assembler.add(domainObject);
  }

  /**
   * Stop assembling SIPs.
   * @throws IOException when an I/O error occurs
   */
  public void end() throws IOException {
    assembler.end();
  }

}
