/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.emc.ia.sdk.support.datetime.Timer;
import com.emc.ia.sdk.support.io.FileSupplier;
import com.emc.ia.sdk.support.io.RuntimeIoException;

public class TimeBasedBatchSipAssembler<D> extends BatchSipAssembler<D> {

  private final Object lock = new Object();
  private final Timer timer;
  private final Consumer<FileGenerationMetrics> callback;

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
    super(assembler, segmentationStrategy, fileSupplier);
    this.callback = sipAssemblyTimer.getCallback();
    setFinalSipInDss(true);
    this.timer = new Timer(sipAssemblyTimer.getMillis(), this::closeSip, sipAssemblyTimer.getClock());
  }

  private void closeSip() {
    try {
      synchronized (lock) {
        closeCurrentSip();
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void add(D domainObject) throws IOException {
    synchronized (lock) {
      super.add(domainObject);
    }
    timer.reset();
  }

  @Override
  protected void sipEnded(FileGenerationMetrics metrics) {
    synchronized (lock) {
      super.sipEnded(metrics);
    }
    callback.accept(metrics);
  }

  @Override
  public void end() throws IOException {
    timer.stop();
    synchronized (lock) {
      super.end();
    }
  }

}
