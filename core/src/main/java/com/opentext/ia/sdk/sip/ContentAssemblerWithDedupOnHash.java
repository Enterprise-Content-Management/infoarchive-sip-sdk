/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.opentext.ia.sdk.support.io.*;

/**
 * A ContentAssembler implementation which will perform deduplication based on the hash value of the content, i.e. a
 * digital object with a given hash is only included once in the SIP.
 * <p>
 * <b>Note</b>Important to note that this content assembler buffers the bytes that make up the digital object in memory
 * so it is not suitable to very large digital objects.
 * </p>
 * @param <D> The type of domain object to assemble SIPs from
 */
public class ContentAssemblerWithDedupOnHash<D> extends ContentAssemblerDefault<D> {

  private final Map<Collection<EncodedHash>, ContentInfo> hashesToContentInfo;
  private final HashAssembler noHashAssembler = new NoHashAssembler();

  public ContentAssemblerWithDedupOnHash(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler, int estimatedMaxDigitalObjects) {
    super(contentsExtraction, contentHashAssembler);
    hashesToContentInfo = new HashMap<>(estimatedMaxDigitalObjects);
  }

  @Override
  public void begin(ZipAssembler zip, Counters metrics) {
    super.begin(zip, metrics);
    hashesToContentInfo.clear();
  }

  @Override
  protected ContentInfo addContent(String ri, DigitalObject digitalObject) throws IOException {
    RepeatableInputStream memoryStream = memoryStreamOf(digitalObject);
    // First compute hashes
    Collection<EncodedHash> hashes = contentHashFor(memoryStream);

    // Check if contentInfo exist of the collection of hashes.
    // If yes, skip adding the content and return existing content info.
    ContentInfo contentInfo = hashesToContentInfo.get(hashes);
    if (contentInfo != null) {
      return contentInfo;
    }

    try (InputStream stream = memoryStream.get()) {
      getZip().addEntry(ri, stream, noHashAssembler);
      getMetrics().inc(SipMetrics.SIZE_DIGITAL_OBJECTS, getContentHashAssembler().numBytesHashed());
      contentInfo = new ContentInfo(ri, hashes);
      hashesToContentInfo.put(hashes, contentInfo);
    }
    return contentInfo;
  }

  private RepeatableInputStream memoryStreamOf(DigitalObject digitalObject) throws IOException {
    try (InputStream raw = digitalObject.get()) {
      return new RepeatableInputStream(raw);
    }
  }

  private Collection<EncodedHash> contentHashFor(RepeatableInputStream memoryStream) throws IOException {
    try (InputStream stream = memoryStream.get()) {
      return contentHashFor(stream);
    }
  }

}
