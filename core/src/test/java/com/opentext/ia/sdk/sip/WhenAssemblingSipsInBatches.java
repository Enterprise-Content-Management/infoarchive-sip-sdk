/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
    sipAssembler =
        SipAssembler.forPdi(somePackagingInformation(), (Assembler<HashedContents<String>>)mock(Assembler.class));
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

    sips.forEach(sip -> assertEquals("SIP directory", dir, sip.getFile()
      .getParentFile()));
  }

  @Test
  public void shouldInvokeCallback() throws IOException {
    BatchSipAssemblerWithCallback<String> batcher =
        new BatchSipAssemblerWithCallback<>(sipAssembler, segmentationStrategy, () -> newFile(), callback);

    batcher.add(randomString());

    verify(callback, never()).accept(any(FileGenerationMetrics.class));
    batcher.end();
    verify(callback).accept(isNotNull(FileGenerationMetrics.class));
  }

  @Test
  public void shouldRejectDomainObjectThatIsTooBig() throws IOException {
    long sipSizeLimit = 2;
    File dir = folder.newFolder();
    // DigitalObjectsExtraction needs to be created and given to the segmentation strategy
    class StringsDomainObjectToDigitalObjects implements DigitalObjectsExtraction<String> {

      @Override
      public Iterator<? extends DigitalObject> apply(String testDomainObject) {
        final ArrayList<DigitalObject> digiObjs = new ArrayList<>();
        ArrayList<String> myStrings = new ArrayList<>();
        for (int i = 0; i < testDomainObject.length(); i++) {
          myStrings.add(testDomainObject.charAt(i) + "");
        }
        myStrings.forEach(eachWord -> digiObjs.add(DigitalObject.fromBytes(randomString(), eachWord.getBytes())));
        return digiObjs.iterator();
      }
    }

    SipSegmentationStrategy<String> localStrategy =
        SipSegmentationStrategy.byMaxProspectiveSipSize(sipSizeLimit, new StringsDomainObjectToDigitalObjects());
    BatchSipAssembler<String> batcher = new BatchSipAssembler<>(sipAssembler, localStrategy, dir);

    batcher.add("9_1234567");
    batcher.add("10_1234567");
    batcher.add("10_1234567");
    batcher.end();
    System.out.println("Batcher closed");
    Iterator<FileGenerationMetrics> sips = batcher.getSipsMetrics().iterator();
    SipMetrics sMet = (SipMetrics)sips.next().getMetrics();
    System.out.println("Print Metrics: " + sMet);

    batcher.add("3 - 0123456789");
    batcher.add("4 - 0123456789");
    batcher.add("5 - 0123456789");
    batcher.add("6 - 0123456789");
    batcher.add("7 - 0123456789");
    batcher.end();
    Iterator<FileGenerationMetrics> sips2 = batcher.getSipsMetrics().iterator();
    SipMetrics sMet2 = (SipMetrics)sips2.next().getMetrics();
    System.out.println("Print Metrics: " + sMet2);
    sMet2.sipFileSize();
  }

}
