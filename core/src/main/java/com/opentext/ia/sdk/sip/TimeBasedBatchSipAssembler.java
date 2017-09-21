/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import com.opentext.ia.sdk.support.datetime.Timer;
import com.opentext.ia.sdk.support.io.FileSupplier;
import com.opentext.ia.sdk.support.io.RuntimeIoException;

/**
 * {@linkplain BatchSipAssembler Assemble a batch of SIPs} on a timed interval.
 * <p>
 * @param <D> The type of domain objects to assemble
 */
public class TimeBasedBatchSipAssembler<D> extends BatchSipAssemblerWithCallback<D> {

  private final Timer timer;

  public TimeBasedBatchSipAssembler(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy,
      SipAssemblyTimer sipAssemblyTimer) {
    this(assembler, segmentationStrategy, FileSupplier.fromTemporaryDirectory(), sipAssemblyTimer);
  }

  public TimeBasedBatchSipAssembler(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy,
      File dir, SipAssemblyTimer sipAssemblyTimer) {
    this(assembler, segmentationStrategy, FileSupplier.fromDirectory(dir), sipAssemblyTimer);
  }

  public TimeBasedBatchSipAssembler(SipAssembler<D> assembler, SipSegmentationStrategy<D> segmentationStrategy,
      Supplier<File> fileSupplier, SipAssemblyTimer sipAssemblyTimer) {
    super(assembler, segmentationStrategy, fileSupplier, sipAssemblyTimer.getCallback());
    setFinalSipInDss(true);
    this.timer = new Timer(sipAssemblyTimer.getMillis(), this::closeSip, sipAssemblyTimer.getClock());
  }

  private void closeSip() {
    try {
      synchronized (getLock()) {
        closeCurrentSip();
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void add(D domainObject) throws IOException {
    synchronized (getLock()) {
      super.add(domainObject);
    }
    timer.reset();
  }

  @Override
  protected void sipEnded(FileGenerationMetrics metrics) {
    synchronized (getLock()) {
      super.sipEnded(metrics);
    }
  }

  @Override
  public void end() throws IOException {
    timer.stop();
    synchronized (getLock()) {
      super.end();
    }
  }

}
