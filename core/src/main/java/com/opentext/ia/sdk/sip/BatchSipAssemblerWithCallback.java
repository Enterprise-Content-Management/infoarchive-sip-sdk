/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.opentext.ia.sdk.support.io.FileSupplier;

/**
 * {@linkplain BatchSipAssembler Assemble a batch of SIPs} with a callback when SIP is closed.
 * <p>
 * 
 * @param <D> The type of domain objects to assemble
 */
public class BatchSipAssemblerWithCallback<D> extends BatchSipAssembler<D> {

  private Consumer<FileGenerationMetrics> callback;
  private final Object lock = new Object();

  public BatchSipAssemblerWithCallback(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy,
      Consumer<FileGenerationMetrics> callback) {
    this(assembler, segmentationStrategy, FileSupplier.fromTemporaryDirectory(), callback);
  }

  public BatchSipAssemblerWithCallback(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy,
      File dir, Consumer<FileGenerationMetrics> callback) {
    this(assembler, segmentationStrategy, FileSupplier.fromDirectory(dir), callback);
  }

  public BatchSipAssemblerWithCallback(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy,
      Supplier<File> fileSupplier, Consumer<FileGenerationMetrics> callback) {
    super(assembler, segmentationStrategy, fileSupplier);
    this.callback = callback;
    setFinalSipInDss(true);
  }

  @Override
  protected void sipEnded(FileGenerationMetrics metrics) {
    synchronized (getLock()) {
      super.sipEnded(metrics);
    }
    getCallback().accept(metrics);
  }

  public Object getLock() {
    return lock;
  }

  public Consumer<FileGenerationMetrics> getCallback() {
    return callback;
  }

}
