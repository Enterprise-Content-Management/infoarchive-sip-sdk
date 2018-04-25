/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.output.NullOutputStream;

import com.opentext.ia.sdk.support.io.EncodedHash;
import com.opentext.ia.sdk.support.io.HashAssembler;
import com.opentext.ia.sdk.support.io.IOStreams;
import com.opentext.ia.sdk.support.io.ZipAssembler;


/**
 * The default ContentAssembler implementation. Will perform no de-duplication and no validation.
 * @param <D> The type of domain object to assemble SIPs from
 */
public class ContentAssemblerDefault<D> implements ContentAssembler<D> {

  private static final int BUFFER_SIZE = 4096;

  private final DigitalObjectsExtraction<D> contentsExtraction;
  private final HashAssembler contentHashAssembler;

  private ZipAssembler zip;
  private Counters metrics;

  public ContentAssemblerDefault(DigitalObjectsExtraction<D> contentsExtraction, HashAssembler contentHashAssembler) {
    this.contentsExtraction = contentsExtraction;
    this.contentHashAssembler = contentHashAssembler;
  }

  @Override
  public synchronized void begin(ZipAssembler aZip, Counters aMetrics) {
    this.zip = aZip;
    this.metrics = aMetrics;
  }

  public DigitalObjectsExtraction<D> getContentsExtraction() {
    return contentsExtraction;
  }

  public HashAssembler getContentHashAssembler() {
    return contentHashAssembler;
  }

  public synchronized Counters getMetrics() {
    return metrics.forReading();
  }

  @Override
  public Map<String, ContentInfo> addContentsOf(D domainObject) throws IOException {
    Map<String, ContentInfo> result = new TreeMap<>();
    Iterator<? extends DigitalObject> digitalObjects = contentsExtraction.apply(domainObject);
    while (digitalObjects.hasNext()) {
      DigitalObject digitalObject = digitalObjects.next();
      incMetric(SipMetrics.NUM_DIGITAL_OBJECTS, 1);
      String entry = digitalObject.getReferenceInformation();
      result.put(entry, addContent(entry, digitalObject));
    }
    return result;
  }

  protected synchronized void incMetric(String metric, long delta) {
    if (metrics == null) {
      throw new IllegalStateException("Missing metrics; did youc call begin()?");
    }
    metrics.inc(metric, delta);
  }

  protected synchronized ContentInfo addContent(String ri, DigitalObject digitalObject) throws IOException {
    if (zip == null) {
      throw new IllegalStateException("Missing zip; did youc call begin()?");
    }
    try (InputStream stream = digitalObject.get()) {
      String referenceInformation = digitalObject.getReferenceInformation();
      Collection<EncodedHash> hashes;
      long numBytesHashed;
      synchronized (zip) {
        hashes = zip.addEntry(referenceInformation, stream, contentHashAssembler);
        numBytesHashed = contentHashAssembler.numBytesHashed();
      }
      incMetric(SipMetrics.SIZE_DIGITAL_OBJECTS, numBytesHashed);
      return new ContentInfo(ri, hashes);
    }
  }

  protected Collection<EncodedHash> contentHashFor(InputStream stream) throws IOException {
    final HashAssembler hashAssembler = getContentHashAssembler();
    synchronized (hashAssembler) {
      hashAssembler.initialize();
      IOStreams.copy(stream, new NullOutputStream(), BUFFER_SIZE, hashAssembler);
      return hashAssembler.get();
    }
  }

  protected synchronized void addZipEntry(String name, InputStream content, HashAssembler hashAssembler)
      throws IOException {
    zip.addEntry(name, content, hashAssembler);
  }

}
