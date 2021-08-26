/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.sdk.support.io.DataBuffer;
import com.opentext.ia.sdk.support.io.RuntimeIoException;
import com.opentext.ia.test.TestCase;


@SuppressWarnings("unchecked")
public class WhenGeneratingFiles extends TestCase {

  @TempDir
  public Path folder;

  @Test
  public void shouldGenerateFileInProvidedDirectory() throws IOException {
    File dir = newFolder(folder);
    FileGenerator<String> generator = new FileGenerator<>(mock(Assembler.class), dir);

    File generated = generator.generate(Collections.singletonList(randomString()))
      .getFile();

    assertEquals(dir, generated.getParentFile(), "Directory");
  }

  @Test
  public void shouldProvideStreamOfGeneratedFileToWrappedGenerator() throws IOException {
    byte[] content = randomBytes();
    Assembler<String> wrapped = mock(Assembler.class);
    doAnswer(invocation -> {
      try {
        DataBuffer arg0 = invocation.getArgument(0);
        try (OutputStream stream = arg0.openForWriting()) {
          stream.write(content);
        }
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      return null;
    }).when(wrapped)
      .start(any(DataBuffer.class));
    FileGenerator<String> generator = new FileGenerator<>(wrapped);

    File generated = generator.generate(Collections.singletonList(randomString()))
      .getFile();

    try (InputStream stream = Files.newInputStream(generated.toPath(), StandardOpenOption.READ)) {
      assertArrayEquals(content, IOUtils.toByteArray(stream), "Content");
    }
  }

  @Test
  public void shouldCallWrappedGenerator() throws IOException {
    Assembler<String> wrapped = mock(Assembler.class);
    String content1 = randomString();
    String content2 = randomString();

    new FileGenerator<String>(wrapped).generate(Arrays.asList(content1, content2));

    verify(wrapped).add(content1);
    verify(wrapped).add(content2);
    verify(wrapped).end();
  }

  @Test
  public void shouldReturnMetrics() throws IOException {
    Assembler<String> wrapped = mock(Assembler.class);
    Counters counters = new Counters();
    long count = randomInt(1, 100);
    counters.set(TestMetrics.FOO, count);
    when(wrapped.getMetrics()).thenAnswer(invocation -> new TestMetrics(counters));

    FileGenerationMetrics actual = new FileGenerator<String>(wrapped).generate(Collections.singletonList(randomString()));

    assertEquals(count, ((TestMetrics)actual.getMetrics()).getFoo(), "Metric");
  }

}
