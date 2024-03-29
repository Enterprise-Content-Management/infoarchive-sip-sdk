/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.sdk.support.io.DomainObjectTooBigException;
import com.opentext.ia.sdk.support.io.RuntimeIoException;

@SuppressWarnings("unchecked")
class WhenAssemblingSipsInBatches extends SipAssemblingTestCase {

  @TempDir
  Path folder;
  private SipAssembler<String> sipAssembler;
  private SipSegmentationStrategy<String> segmentationStrategy;
  private final Consumer<FileGenerationMetrics> callback = mock(Consumer.class);

  @BeforeEach
  public void init() {
    sipAssembler =
        SipAssembler.forPdi(somePackagingInformation(), (Assembler<HashedContents<String>>)mock(Assembler.class));
    segmentationStrategy = mock(SipSegmentationStrategy.class);
  }

  private PackagingInformation somePackagingInformation() {
    return PackagingInformation.builder().dss().holding(randomString(64)).schema(randomString(64))
        .entity(randomString(64)).producer(randomString(64)).end().build();
  }

  @Test
  void shouldStartNewSipsbasedOnSegmentationStrategy() throws IOException {
    String object1 = randomString();
    String object2 = randomString();
    String object3 = randomString();
    when(segmentationStrategy.shouldStartNewSip(anyString(), any(SipMetrics.class))).thenAnswer(invocation -> {
      return object2.equals(invocation.getArgument(0));
    });
    BatchSipAssembler<String> batcher = new BatchSipAssembler<>(sipAssembler, segmentationStrategy, () -> newFile());

    batcher.add(object1);
    batcher.add(object2);
    batcher.add(object3);
    batcher.end();
    Iterator<FileGenerationMetrics> sips = batcher.getSipsMetrics().iterator();

    verify(segmentationStrategy).shouldStartNewSip(eq(object1), any(SipMetrics.class));
    verify(segmentationStrategy).shouldStartNewSip(eq(object2), any(SipMetrics.class));
    verify(segmentationStrategy).shouldStartNewSip(eq(object3), any(SipMetrics.class));

    assertSip(1, sips);
    assertSip(2, sips);
    assertFalse(sips.hasNext(), "Extra SIPs");
  }

  private File newFile() {
    try {
      return newFile(folder);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void assertSip(int n, Iterator<FileGenerationMetrics> sips) {
    assertTrue(sips.hasNext(), "Missing SIP #" + n);

    FileGenerationMetrics sip = sips.next();
    assertEquals(folder.toAbsolutePath().toString(),
        sip.getFile().getParentFile().getAbsolutePath(), "SIP dir #" + n);
    assertEquals(n, ((SipMetrics)sip.getMetrics()).numAius(), "# objects in SIP #" + n);
  }

  @Test
  void shouldCreateFilesInGivenDirectory() throws IOException {
    File dir = newFolder(folder);
    BatchSipAssembler<String> batcher = new BatchSipAssembler<>(sipAssembler, segmentationStrategy, dir);

    batcher.add(randomString());
    batcher.end();
    Collection<FileGenerationMetrics> sips = batcher.getSipsMetrics();

    sips.forEach(sip -> assertEquals(dir, sip.getFile().getParentFile(), "SIP directory"));
  }

  @Test
  void shouldInvokeCallback() throws IOException {
    BatchSipAssemblerWithCallback<String> batcher =
        new BatchSipAssemblerWithCallback<>(sipAssembler, segmentationStrategy, () -> newFile(), callback);

    batcher.add(randomString());

    verify(callback, never()).accept(any(FileGenerationMetrics.class));
    batcher.end();
    verify(callback).accept(notNull());
  }

  @Test
  void shouldRejectDomainObjectThatIsTooBig() throws IOException {
    File dir = newFolder(folder);
    int maxSize = 2;
    SipSegmentationStrategy<String> strategy =
        SipSegmentationStrategy.byMaxProspectiveSipSize(maxSize, new StringToDigitalObjects());
    BatchSipAssembler<String> batcher = new BatchSipAssembler<>(sipAssembler, strategy, dir);
    assertThrows(DomainObjectTooBigException.class, () -> batcher.add(randomString(maxSize + 1)));
  }

  @Test
  // #39
  void shouldBeAbleToDeleteFileFromCallback() throws IOException {
    AtomicReference<File> sip = new AtomicReference<>();
    Consumer<FileGenerationMetrics> deletingCallback = fgm -> {
      File file = fgm.getFile();
      sip.set(file);
      if (!file.delete()) {
        throw new IllegalStateException("Could not delete file " + file.getAbsolutePath());
      }
    };
    BatchSipAssemblerWithCallback<String> batcher =
        new BatchSipAssemblerWithCallback<>(sipAssembler, segmentationStrategy, () -> newFile(), deletingCallback);

    batcher.add(randomString());

    batcher.end();
    assertNotNull(sip.get(), "Callback not invoked");
    assertFalse(sip.get().isFile(), "File not deleted");
  }

  @Test
  // #39
  void shouldProduceValidSips() throws IOException {
    AtomicInteger numSips = new AtomicInteger();
    Consumer<FileGenerationMetrics> sipValidatingCallback = fgm -> {
      numSips.incrementAndGet();
      File zip = fgm.getFile();
      try (InputStream sip = Files.newInputStream(zip.toPath(), StandardOpenOption.READ)) {
        String packageInformation = getPackageInformation(sip);
        assertEquals(getSeqNo(packageInformation) > 1, isLast(packageInformation), "Is last");
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
    };
    BatchSipAssemblerWithCallback<String> batcher = new BatchSipAssemblerWithCallback<>(sipAssembler,
        (object, metrics) -> true, () -> newFile(), sipValidatingCallback);

    batcher.add(randomString());
    batcher.add(randomString());

    batcher.end();
    assertEquals(2, numSips.get(), "# SIPs");
  }

  private final class StringToDigitalObjects implements DigitalObjectsExtraction<String> {

    @Override
    public Iterator<? extends DigitalObject> apply(String testDomainObject) {
      int length = testDomainObject.length();
      Collection<DigitalObject> result = new ArrayList<>(length);
      for (int i = 0; i < length; i++) {
        result.add(
            DigitalObject.fromString(randomString(), testDomainObject.substring(i, i + 1), StandardCharsets.UTF_8));
      }
      return result.iterator();
    }

  }

}
