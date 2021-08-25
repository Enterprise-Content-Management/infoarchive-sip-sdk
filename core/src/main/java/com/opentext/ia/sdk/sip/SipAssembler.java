/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.opentext.ia.sdk.support.io.DataBuffer;
import com.opentext.ia.sdk.support.io.DataBufferSupplier;
import com.opentext.ia.sdk.support.io.DefaultZipAssembler;
import com.opentext.ia.sdk.support.io.EncodedHash;
import com.opentext.ia.sdk.support.io.FileBuffer;
import com.opentext.ia.sdk.support.io.HashAssembler;
import com.opentext.ia.sdk.support.io.IOStreams;
import com.opentext.ia.sdk.support.io.MemoryBuffer;
import com.opentext.ia.sdk.support.io.NoHashAssembler;
import com.opentext.ia.sdk.support.io.RuntimeIoException;
import com.opentext.ia.sdk.support.io.ZipAssembler;

/**
 * Assembles a <a href="http://public.ccsds.org/publications/archive/650x0m2.pdf">Submission Information Package</a>
 * (SIP) from several domain objects of the same type. Each domain object is typically a Plain Old Java Object (POJO)
 * that you create in an application-specific manner.
 * <p>
 * A SIP is a ZIP file that contains:
 * <ul>
 * <li>One {@linkplain com.opentext.ia.sdk.sip.PackagingInformation Packaging Information} that describes the
 * content of the SIP</li>
 * <li>One Preservation Description Information (PDI) that contains structured data to be archived</li>
 * <li>Zero or more Content Data Objects that contain unstructured data to be archived. These are referenced from the
 * PDI</li>
 * </ul>
 * <p>
 * Packaging Information is created by a {@linkplain PackagingInformationFactory factory}. If you want only one SIP in a
 * {@linkplain DataSubmissionSession DSS}, then you can use a {@linkplain DefaultPackagingInformationFactory} to create
 * the Packaging Information based on a prototype which contains application-specific fields.
 * <p>
 * The PDI will be assembled from the domain objects by an {@linkplain Assembler} and added to the ZIP by a
 * {@linkplain ZipAssembler}. Each domain object may also contain zero or more {@linkplain DigitalObject}s, which are
 * extracted from the domain object using a {@linkplain DigitalObjectsExtraction} and added to the ZIP. The PDI is
 * written to a {@linkplain DataBuffer} until it is complete. For small PDIs, you can use a {@linkplain MemoryBuffer} to
 * hold this data, but for larger PDIs you should use a {@linkplain FileBuffer} to prevent running out of memory.
 * <p>
 * Use the following steps to assemble a SIP:
 * <ol>
 * <li>Start the process by calling the {@linkplain #start(DataBuffer)} method</li>
 * <li>Add zero or more domain objects by calling the {@linkplain #add(Object)} method multiple times</li>
 * <li>Finish the process by calling the {@linkplain #end()} method</li>
 * </ol>
 * You can optionally get metrics about the SIP assembly process by calling {@linkplain #getMetrics()} at any time.
 * <p>
 * If the number of domain objects is small and each individual domain object is also small, you can wrap a
 * {@linkplain SipAssembler} in a {@linkplain Generator} to reduce the above code to a single call.
 * <p>
 * To assemble a number of SIPs in a batch, use {@linkplain BatchSipAssembler}.
 *
 * <dl><dt>Warning:</dt><dd>This object is not thread-safe. If you want to use multiple threads to assemble SIPs, let
 * each use their own instance.</dd></dl>
 * @param <D> The type of domain objects to assemble the SIP from
 */
public class SipAssembler<D> implements Assembler<D> {

  private static final String PACKAGING_INFORMATION_ENTRY = "eas_sip.xml";
  private static final String PDI_ENTRY = "eas_pdi.xml";

  private final ZipAssembler zip;
  private final Assembler<PackagingInformation> packagingInformationAssembler;
  private final Assembler<HashedContents<D>> pdiAssembler;
  private final HashAssembler pdiHashAssembler;
  private final Supplier<? extends DataBuffer> pdiBufferSupplier;
  private final PackagingInformationFactory packagingInformationFactory;
  private final Counters metrics = new Counters();
  private final ContentAssembler<D> contentAssembler;
  private DataBuffer pdiBuffer;
  private DataBuffer sipFileBuffer;
  private Optional<EncodedHash> pdiHash;

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param prototype Prototype for the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdi(PackagingInformation prototype, Assembler<HashedContents<D>> pdiAssembler) {
    return forPdiWithHashing(prototype, pdiAssembler, new NoHashAssembler());
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param factory Factory for creating the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdi(PackagingInformationFactory factory,
      Assembler<HashedContents<D>> pdiAssembler) {
    return forPdiWithHashing(factory, pdiAssembler, new NoHashAssembler());
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param prototype Prototype for the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param pdiHashAssembler Assembler that builds up an encoded hash for the PDI
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiWithHashing(PackagingInformation prototype,
      Assembler<HashedContents<D>> pdiAssembler, HashAssembler pdiHashAssembler) {
    return forPdiAndContentWithHashing(prototype, pdiAssembler, pdiHashAssembler, ContentAssembler.ignoreContent());
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param factory Factory for creating the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param pdiHashAssembler Assembler that builds up an encoded hash for the PDI
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiWithHashing(PackagingInformationFactory factory,
      Assembler<HashedContents<D>> pdiAssembler, HashAssembler pdiHashAssembler) {
    return forPdiAndContentWithHashing(factory, pdiAssembler, pdiHashAssembler, ContentAssembler.ignoreContent());
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param prototype Prototype for the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContent(PackagingInformation prototype,
      Assembler<HashedContents<D>> pdiAssembler, DigitalObjectsExtraction<D> contentsExtraction) {
    HashAssembler noHashAssembler = new NoHashAssembler();
    return forPdiAndContentWithHashing(prototype, pdiAssembler, noHashAssembler, contentsExtraction, noHashAssembler);
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param prototype Prototype for the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param contentAssembler ContentAssembler that adds the digital objects to the SIP
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContent(PackagingInformation prototype,
      Assembler<HashedContents<D>> pdiAssembler, ContentAssembler<D> contentAssembler) {
    HashAssembler noHashAssembler = new NoHashAssembler();
    return forPdiAndContentWithHashing(prototype, pdiAssembler, noHashAssembler, contentAssembler);
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param factory Factory for creating the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param contentAssembler ContentAssembler that adds the digital objects to the SIP
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContent(PackagingInformationFactory factory,
      Assembler<HashedContents<D>> pdiAssembler, ContentAssembler<D> contentAssembler) {
    HashAssembler noHashAssembler = new NoHashAssembler();
    return forPdiAndContentWithHashing(factory, pdiAssembler, noHashAssembler, contentAssembler);
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param factory Factory for creating the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContent(PackagingInformationFactory factory,
      Assembler<HashedContents<D>> pdiAssembler, DigitalObjectsExtraction<D> contentsExtraction) {
    HashAssembler noHashAssembler = new NoHashAssembler();
    return forPdiAndContentWithHashing(factory, pdiAssembler, noHashAssembler,
        ContentAssembler.noDedup(contentsExtraction, noHashAssembler));
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param prototype Prototype for the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContentWithContentHashing(PackagingInformation prototype,
      Assembler<HashedContents<D>> pdiAssembler, DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler) {
    return forPdiAndContentWithHashing(prototype, pdiAssembler, new NoHashAssembler(), contentsExtraction,
        contentHashAssembler);
  }

  /**
   * Assemble a SIP that contains only structured data and is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param factory Factory for creating the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContentWithContentHashing(PackagingInformationFactory factory,
      Assembler<HashedContents<D>> pdiAssembler, DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler) {
    return forPdiAndContentWithHashing(factory, pdiAssembler, new NoHashAssembler(),
        new ContentAssemblerDefault<D>(contentsExtraction, contentHashAssembler));
  }

  /**
   * Assemble a SIP that is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param prototype Prototype for the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param pdiHashAssembler Assembler that builds up an encoded hash for the PDI
   * @param contentsExtraction Extraction of content from domain objects added to the SIP
   * @param contentHashAssembler Assembler that builds up an encoded hash for the extracted content
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContentWithHashing(PackagingInformation prototype,
      Assembler<HashedContents<D>> pdiAssembler, HashAssembler pdiHashAssembler,
      DigitalObjectsExtraction<D> contentsExtraction, HashAssembler contentHashAssembler) {
    return new SipAssembler<>(new DefaultPackagingInformationFactory(prototype), pdiAssembler, pdiHashAssembler,
        new DataBufferSupplier<>(MemoryBuffer.class),
        new ContentAssemblerDefault<D>(contentsExtraction, contentHashAssembler));
  }

  /**
   * Assemble a SIP that is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param prototype Prototype for the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param pdiHashAssembler Assembler that builds up an encoded hash for the PDI
   * @param contentAssembler ContentAssembler that adds the digital objects to the SIP
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContentWithHashing(PackagingInformation prototype,
      Assembler<HashedContents<D>> pdiAssembler, HashAssembler pdiHashAssembler, ContentAssembler<D> contentAssembler) {
    return new SipAssembler<>(new DefaultPackagingInformationFactory(prototype), pdiAssembler, pdiHashAssembler,
        new DataBufferSupplier<>(MemoryBuffer.class), contentAssembler);
  }

  /**
   * Assemble a SIP that is the only SIP in its DSS.
   * @param <D> The type of domain objects to assemble the SIP from
   * @param factory Factory for creating the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param pdiHashAssembler Assembler that builds up an encoded hash for the PDI
   * @param contentAssembler ContentAssembler that adds the digital objects to the SIP
   * @return The newly created SIP assembler
   */
  public static <D> SipAssembler<D> forPdiAndContentWithHashing(PackagingInformationFactory factory,
      Assembler<HashedContents<D>> pdiAssembler, HashAssembler pdiHashAssembler, ContentAssembler<D> contentAssembler) {
    return new SipAssembler<>(factory, pdiAssembler, pdiHashAssembler, new DataBufferSupplier<>(MemoryBuffer.class),
        contentAssembler);
  }

  /**
   * Create a new instance.
   * @param packagingInformationFactory Factory for creating the Packaging Information
   * @param pdiAssembler Assembler that builds up the PDI
   * @param pdiHashAssembler Assembler that builds up an encoded hash for the PDI and the unstructured data
   * @param pdiBufferSupplier Supplier for a data buffer to store the PDI
   * @param contentAssembler ContentAssembler that adds the digital objects to the SIP
   */
  public SipAssembler(PackagingInformationFactory packagingInformationFactory,
      Assembler<HashedContents<D>> pdiAssembler, HashAssembler pdiHashAssembler,
      Supplier<? extends DataBuffer> pdiBufferSupplier, ContentAssembler<D> contentAssembler) {
    this(packagingInformationFactory, new InfoArchivePackagingInformationAssembler(), pdiAssembler, pdiHashAssembler,
        pdiBufferSupplier, new DefaultZipAssembler(), contentAssembler);
  }

  SipAssembler(PackagingInformationFactory packagingInformationFactory,
      Assembler<PackagingInformation> packagingInformationAssembler, Assembler<HashedContents<D>> pdiAssembler,
      HashAssembler pdiHashAssembler, Supplier<? extends DataBuffer> pdiBufferSupplier, ZipAssembler zipAssembler,
      ContentAssembler<D> contentAssembler) {
    this.packagingInformationFactory = packagingInformationFactory;
    this.packagingInformationAssembler = packagingInformationAssembler;
    this.pdiAssembler = pdiAssembler;
    this.pdiHashAssembler = pdiHashAssembler;
    this.pdiBufferSupplier = pdiBufferSupplier;
    this.contentAssembler = contentAssembler;
    this.zip = zipAssembler;
  }

  @Override
  public synchronized void start(DataBuffer buffer) throws IOException {
    this.sipFileBuffer = buffer;
    pdiHash = Optional.empty();
    metrics.reset();
    metrics.set(SipMetrics.ASSEMBLY_TIME, System.currentTimeMillis());
    zip.begin(sipFileBuffer.openForWriting());
    contentAssembler.begin(zip, metrics);
    startPdi();
  }

  private synchronized void startPdi() throws IOException {
    pdiBuffer = pdiBufferSupplier.get();
    pdiAssembler.start(pdiBuffer);
  }

  @Override
  public synchronized void add(D domainObject) {
    try {
      Map<String, ContentInfo> contentInfo;
      contentInfo = contentAssembler.addContentsOf(domainObject);
      pdiAssembler.add(new HashedContents<>(domainObject, contentInfo));
      metrics.inc(SipMetrics.NUM_AIUS);
      setPdiSize(pdiBuffer.length()); // Approximate PDI size until the end, when we know for sure
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void setPdiSize(long pdiSize) {
    metrics.set(SipMetrics.SIZE_PDI, pdiSize);
    metrics.set(SipMetrics.SIZE_SIP, metrics.get(SipMetrics.SIZE_DIGITAL_OBJECTS) + metrics.get(SipMetrics.SIZE_PDI));
  }

  @Override
  public synchronized void end() throws IOException {
    try { // NOPMD zip is closed
      endPdi();
      addPackagingInformation();
    } finally {
      IOStreams.close(zip);
      metrics.set(SipMetrics.ASSEMBLY_TIME, System.currentTimeMillis() - metrics.get(SipMetrics.ASSEMBLY_TIME));
      metrics.set(SipMetrics.SIZE_SIP_FILE, sipFileBuffer.length());
    }
  }

  private synchronized void endPdi() throws IOException {
    try {
      pdiAssembler.end();
      addPdiToZip();
    } finally {
      pdiBuffer = null;
    }
  }

  void addPdiToZip() throws IOException {
    try (InputStream in = pdiBuffer.openForReading()) {
      pdiHash = zip.addEntry(PDI_ENTRY, in, pdiHashAssembler)
        .stream()
        .limit(1)
        .findAny();
    }
    setPdiSize(pdiHashAssembler.numBytesHashed());
  }

  private void addPackagingInformation() throws IOException {
    DataBuffer buffer = new MemoryBuffer();
    packagingInformationAssembler.start(buffer);
    packagingInformationAssembler.add(packagingInformation());
    packagingInformationAssembler.end();
    long packagingInformationSize = buffer.length();
    try (InputStream stream = buffer.openForReading()) {
      zip.addEntry(PACKAGING_INFORMATION_ENTRY, stream, new NoHashAssembler());
    }
    metrics.set(SipMetrics.SIZE_SIP,
        metrics.get(SipMetrics.SIZE_DIGITAL_OBJECTS) + metrics.get(SipMetrics.SIZE_PDI) + packagingInformationSize);
  }

  private PackagingInformation packagingInformation() {
    return packagingInformationFactory.newInstance(metrics.get(SipMetrics.NUM_AIUS), pdiHash);
  }

  @Override
  public SipMetrics getMetrics() {
    return new SipMetrics(metrics.forReading());
  }

  public PackagingInformationFactory getPackagingInformationFactory() {
    return packagingInformationFactory;
  }

}
