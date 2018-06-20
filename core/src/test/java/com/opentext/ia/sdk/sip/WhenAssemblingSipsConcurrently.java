/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.opentext.ia.sdk.support.io.MemoryBuffer;
import com.opentext.ia.test.TestCase;


public class WhenAssemblingSipsConcurrently extends TestCase {

  private static final int NUM_THREADS = 32;
  private static final long ASSEMBLE_SECONDS = 5;
  private static final int MIN_LENGTH = 1000;
  private static final int MAX_LENGTH = 100000;
  private static final long MAX_WAIT_THREAD_TERMINATION_MS = 1000;

  @Test
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  public void shouldNotFail() throws InterruptedException, IOException {
    URI entityUri = URI.create("urn:com.opentext.ia.sdk.test.concurrent:1.0");
    String entityName = "concurrent";
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .application("concurrentApplication")
            .holding("concurrentHolding")
            .producer("SIP SDK")
            .entity(entityName)
            .schema(entityUri.toString())
        .end()
    .build();

    PdiAssembler<String> pdiAssembler = new XmlPdiAssembler<String>(entityUri, entityName) {
      @Override
      protected void doAdd(String text, Map<String, ContentInfo> contentInfo) {
        getBuilder()
            .element("text", text)
            .element("size", Integer.toString(text.length()))
            .element("contentType", "text/plain")
            .elements("hashes", "hash", contentInfo.get(text).getContentHashes(), (hash, builder) -> {
              builder
                  .attribute("algorithm", hash.getHashFunction())
                  .attribute("encoding", hash.getEncoding())
                  .attribute("value", hash.getValue());
            });
      }
    };
    DigitalObjectsExtraction<String> contentExtraction = text -> {
        String content = randomString(randomInt(MIN_LENGTH, MAX_LENGTH));
        return Collections.singleton(DigitalObject.fromString(text, content, StandardCharsets.UTF_8)).iterator();
    };

    SipAssembler<String> assembler = SipAssembler.forPdiAndContent(prototype, pdiAssembler, contentExtraction);
    final AtomicReference<String> error = new AtomicReference<>();
    assembler.start(new MemoryBuffer());
    try {
      final AtomicBoolean canRun = new AtomicBoolean(true);
      Collection<Thread> threads = new ArrayList<>(NUM_THREADS);
      for (int i = 0; i < NUM_THREADS; i++) {
        Thread thread = new Thread(() -> {
          while (canRun.get()) {
            try {
              assembler.add(randomString());
            } catch (RuntimeException e) {
              canRun.set(false);
              error.set("Failed to add object to SIP: " + e.getMessage());
            }
          }
        }, "assemble-" + i);
        threads.add(thread);
      }
      threads.forEach(Thread::start);
      TimeUnit.SECONDS.sleep(ASSEMBLE_SECONDS);
      canRun.set(false);
      for (Thread thread : threads) {
        thread.join(MAX_WAIT_THREAD_TERMINATION_MS);
      }
    } finally {
      assembler.end();
    }
    assertNull("Error during concurrent SIP assembly", error.get());
  }

}
