/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.test.TestCase;


@SuppressWarnings("unchecked")
public class WhenAssemblingSipsInBatches extends TestCase {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private SipAssembler<String> sipAssembler;
  private SipSegmentationStrategy<String> segmentationStrategy;

  @Before
  public void init() {
    sipAssembler = SipAssembler.forPdi(somePackagingInformation(),
        (Assembler<HashedContents<String>>)mock(Assembler.class));
    segmentationStrategy = mock(SipSegmentationStrategy.class);
  }

  private PackagingInformation somePackagingInformation() {
    return PackagingInformation.builder()
        .dss()
            .holding(randomString(64))
            .schema(randomString(64))
            .entity(randomString(64))
            .producer(randomString(64))
        .end()
        .build();
  }

  @Test
  public void shouldStartNewSipsbasedOnSegmentationStrategy() throws IOException {
    String object1 = randomString();
    String object2 = randomString();
    String object3 = randomString();
    when(segmentationStrategy.shouldStartNewSip(anyString(), any(SipMetrics.class))).thenAnswer(invocation -> {
      return object2 == invocation.getArgumentAt(0, String.class);
    });
    BatchSipAssembler<String> batcher = new BatchSipAssembler<>(sipAssembler, segmentationStrategy, () -> newFile());

    batcher.add(object1);
    batcher.add(object2);
    batcher.add(object3);
    batcher.end();
    Iterator<FileGenerationMetrics> sips = batcher.getSipsMetrics().iterator();

    verify(segmentationStrategy, never()).shouldStartNewSip(eq(object1), any(SipMetrics.class));
    verify(segmentationStrategy).shouldStartNewSip(eq(object2), any(SipMetrics.class));
    verify(segmentationStrategy).shouldStartNewSip(eq(object3), any(SipMetrics.class));

    assertSip(1, sips);
    assertSip(2, sips);
    assertFalse("Extra SIPs", sips.hasNext());
  }

  private File newFile() {
    try {
      return folder.newFile();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void assertSip(int n, Iterator<FileGenerationMetrics> sips) {
    assertTrue("Missing SIP #" + n, sips.hasNext());

    FileGenerationMetrics sip = sips.next();
    assertEquals("SIP dir #" + n, folder.getRoot(), sip.getFile().getParentFile());
    assertEquals("# objects in SIP #" + n, n, ((SipMetrics)sip.getMetrics()).numAius());
  }

  @Test
  public void shouldCreateFilesInGivenDirectory() throws IOException {
    File dir = folder.newFolder();
    BatchSipAssembler<String> batcher = new BatchSipAssembler<>(sipAssembler, segmentationStrategy, dir);

    batcher.add(randomString());
    batcher.end();
    Collection<FileGenerationMetrics> sips = batcher.getSipsMetrics();

    sips.forEach(sip -> assertEquals("SIP directory", dir, sip.getFile().getParentFile()));
  }

}
