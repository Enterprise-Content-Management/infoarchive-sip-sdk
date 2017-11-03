/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.sdk.support.io.DomainObjectTooBigException;
import com.opentext.ia.sdk.support.io.RuntimeIoException;
import com.opentext.ia.test.TestCase;


@SuppressWarnings("unchecked")
public class WhenAssemblingSipsInBatches extends TestCase {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private SipAssembler<String> sipAssembler;
  private SipSegmentationStrategy<String> segmentationStrategy;
  private final Consumer<FileGenerationMetrics> callback = mock(Consumer.class);

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
    Iterator<FileGenerationMetrics> sips = batcher.getSipsMetrics()
      .iterator();

    verify(segmentationStrategy).shouldStartNewSip(eq(object1), any(SipMetrics.class));
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
    assertEquals("SIP dir #" + n, folder.getRoot(), sip.getFile()
      .getParentFile());
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

  @Test
  public void shouldInvokeCallback() throws IOException {
    BatchSipAssemblerWithCallback<String> batcher =
        new BatchSipAssemblerWithCallback<>(sipAssembler, segmentationStrategy, () -> newFile(), callback);

    batcher.add(randomString());

    verify(callback, never()).accept(any(FileGenerationMetrics.class));
    batcher.end();
    verify(callback).accept(notNull(FileGenerationMetrics.class));
  }

  @Test(expected = DomainObjectTooBigException.class)
  public void shouldRejectDomainObjectThatIsTooBig() throws IOException {
    File dir = folder.newFolder();
    int maxSize = 2;
    SipSegmentationStrategy<String> strategy = SipSegmentationStrategy.byMaxProspectiveSipSize(maxSize,
        new StringToDigitalObjects());
    BatchSipAssembler<String> batcher = new BatchSipAssembler<>(sipAssembler, strategy, dir);

    batcher.add(randomString(maxSize + 1));
  }

  @Test
  // #39
  public void shouldBeAbleToDeleteFileFromCallback() throws IOException {
    AtomicReference<File> sip = new AtomicReference<File>();
    Consumer<FileGenerationMetrics> deletingCallback = fgm -> {
      File file = fgm.getFile();
      sip.set(file);
      file.delete();
    };
    BatchSipAssemblerWithCallback<String> batcher = new BatchSipAssemblerWithCallback<>(sipAssembler,
        segmentationStrategy, () -> newFile(), deletingCallback);

    batcher.add(randomString());

    batcher.end();
    assertNotNull("Callback not invoked", sip.get());
    assertFalse("File not deleted", sip.get().isFile());
  }


  private final class StringToDigitalObjects implements DigitalObjectsExtraction<String> {

    @Override
    public Iterator<? extends DigitalObject> apply(String testDomainObject) {
      Collection<DigitalObject> result = new ArrayList<>();
      for (int i = 0; i < testDomainObject.length(); i++) {
        result.add(
            DigitalObject.fromString(randomString(), testDomainObject.substring(i, i + 1), StandardCharsets.UTF_8));
      }
      return result.iterator();
    }

  }

}
