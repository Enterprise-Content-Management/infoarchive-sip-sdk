/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.test.TestCase;

@SuppressWarnings("unchecked")
public class WhenAssemblingSipsInTimeWindows extends TestCase {

  @TempDir
  public Path temporaryFolder;
  private final SipAssembler<String> assembler = mock(SipAssembler.class);
  private final SipSegmentationStrategy<String> segmentationStrategy = mock(SipSegmentationStrategy.class);
  private final Clock clock = mock(Clock.class);
  private final Consumer<FileGenerationMetrics> callback = mock(Consumer.class);
  private long maxTime;
  private File sipDir;
  private TimeBasedBatchSipAssembler<String> batchAssembler;
  private String taskName;
  private Runnable alarm;

  @BeforeEach
  public void init() throws IOException {
    PackagingInformationFactory factory = mock(PackagingInformationFactory.class);
    when(assembler.getPackagingInformationFactory()).thenReturn(factory);
    sipDir = newFolder(temporaryFolder);
    maxTime = randomInt(37, 313);
    batchAssembler = new TimeBasedBatchSipAssembler<>(assembler, segmentationStrategy, sipDir,
        new SipAssemblyTimer(maxTime, clock, callback));
    ArgumentCaptor<String> taskNameCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(clock).schedule(taskNameCaptor.capture(), eq(maxTime), eq(TimeUnit.MILLISECONDS), taskCaptor.capture());
    taskName = taskNameCaptor.getValue();
    alarm = taskCaptor.getValue();
  }

  @Test
  public void shouldResetTimerWhenObjectAdded() throws IOException {
    batchAssembler.add(randomString());

    verify(clock).cancel(taskName);
    verify(clock, times(2)).schedule(taskName, maxTime, TimeUnit.MILLISECONDS, alarm);
  }

  @Test
  public void shouldCallBackWhenSipIsFullAfterAddingObject() throws IOException {
    String domainObject1 = randomString();
    String domainObject2 = randomString();
    when(segmentationStrategy.shouldStartNewSip(eq(domainObject2), any(SipMetrics.class))).thenReturn(Boolean.TRUE);
    SipMetrics sipMetrics = new SipMetrics(null);
    when(assembler.getMetrics()).thenReturn(sipMetrics);

    batchAssembler.add(domainObject1);
    batchAssembler.add(domainObject2);

    verify(assembler).add(domainObject1);
    verify(assembler).add(domainObject2);
    ArgumentCaptor<FileGenerationMetrics> metricsCaptor = ArgumentCaptor.forClass(FileGenerationMetrics.class);
    verify(callback).accept(metricsCaptor.capture());
    FileGenerationMetrics metrics = metricsCaptor.getValue();
    assertEquals(sipDir, metrics.getFile().getParentFile(), "SIP directory");
    assertSame(sipMetrics, metrics.getMetrics(), "SIP metrics");
  }

  @Test
  public void shouldCallBackAfterTimePassedWhenSipIsNonEmpty() throws IOException {
    batchAssembler.add(randomString());

    alarm.run();

    verify(callback).accept(notNull());
  }

  @Test
  public void shouldNotCallBackAfterTimePassedWhenSipIsEmpty() throws IOException {
    alarm.run();

    verify(callback, never()).accept(any(FileGenerationMetrics.class));
  }

  @Test
  public void shouldStopTimerWhenClosed() throws IOException {
    batchAssembler.end();

    verify(clock).cancel(taskName);
  }

}
