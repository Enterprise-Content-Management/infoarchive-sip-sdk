/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import com.opentext.ia.sdk.support.io.FileBuffer;
import com.opentext.ia.sdk.support.io.FileSupplier;

/**
 * Assemble a batch of SIPs, based on some sort of {@linkplain SipSegmentationStrategy segmentation strategy} that
 * determines which domain objects go into which SIPs.
 * <p>
 * To create a batch of SIPs, simply {@linkplain #add(Object) add} domain objects and {@linkplain #end() end} the
 * assembly process. Then access the generated files and metrics about them using {@linkplain #getSipsMetrics()}.
 * <p>
 * There are several {@linkplain SipSegmentationStrategy factory methods} available to create common segmentation
 * strategies and you can also {@linkplain SipSegmentationStrategy#combining(SipSegmentationStrategy...) combine} them.
 * <p>
 * @param <D> The type of domain object to assemble SIPs from
 */
public class BatchSipAssembler<D> {

  private final SipAssembler<D> assembler;
  private final SipSegmentationStrategy<D> segmentationStrategy;
  private final Supplier<File> fileSupplier;
  private final Collection<FileGenerationMetrics> sipsMetrics = new ArrayList<>();
  private File current;

  /**
   * Create an instance that assembles SIPs in a temporary directory.
   * @param assembler An assembler that builds up the SIPs in the batch
   * @param segmentationStrategy A strategy that determines when to start new SIPs
   */
  public BatchSipAssembler(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy) {
    this(assembler, segmentationStrategy, FileSupplier.fromTemporaryDirectory());
  }

  /**
   * Create an instance that assembles SIPs in the given directory.
   * @param assembler An assembler that builds up the SIPs in the batch
   * @param segmentationStrategy A strategy that determines when to start new SIPs
   * @param dir Directory in which to generate SIP files
   */
  public BatchSipAssembler(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy, File dir) {
    this(assembler, segmentationStrategy, FileSupplier.fromDirectory(dir));
  }

  /**
   * Create an instance that assembles SIPs using the given supplier of SIP files.
   * @param assembler An assembler that builds up the SIPs in the batch
   * @param segmentationStrategy A strategy that determines when to start new SIPs
   * @param fileSupplier A supplier of files in which to store the SIPs
   */
  public BatchSipAssembler(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy,
      Supplier<File> fileSupplier) {
    this.assembler = assembler;
    this.segmentationStrategy = segmentationStrategy;
    this.fileSupplier = fileSupplier;
    setFinalSipInDss(false);
  }

  protected final void setFinalSipInDss(boolean finalSipInDss) {
    assembler.getPackagingInformationFactory()
      .setFinalSipInDss(finalSipInDss);
  }

  /**
   * Add a domain object to the batch of SIPs.
   * @param domainObject The domain object to add
   * @throws IOException When an I/O error occurs
   */
  public void add(D domainObject) throws IOException {
    if (shouldStartNewSip(domainObject)) {
      startSip();
    }
    assembler.add(domainObject);
  }

  private boolean shouldStartNewSip(D component) {
    return current == null || segmentationStrategy.shouldStartNewSip(component, assembler.getMetrics());
  }

  private void startSip() throws IOException {
    closeCurrentSip();
    startNewSip();
  }

  protected final void closeCurrentSip() throws IOException {
    if (current != null) {
      assembler.end();
      FileGenerationMetrics metrics = new FileGenerationMetrics(current, assembler.getMetrics());
      current = null;
      sipEnded(metrics);
    }
  }

  protected void sipEnded(FileGenerationMetrics metrics) {
    sipsMetrics.add(metrics);
  }

  private void startNewSip() throws IOException {
    File file = fileSupplier.get();
    assembler.start(new FileBuffer(file));
    // NOTE: Set *after* [assembler] has started, since we check [current] to determine whether
    // [assembler] has started.
    current = file;
  }

  /**
   * End the batch assembly process.
   * @throws IOException When an I/O error occurs
   */
  public void end() throws IOException {
    setFinalSipInDss(true);
    closeCurrentSip();
  }

  /**
   * Returns the files that were generated as part of this batch and metrics about the SIPs stored in them.
   * @return The files that were generated as part of this batch and metrics about the SIPs stored in them
   */
  public Collection<FileGenerationMetrics> getSipsMetrics() {
    return Collections.unmodifiableCollection(sipsMetrics);
  }

}
