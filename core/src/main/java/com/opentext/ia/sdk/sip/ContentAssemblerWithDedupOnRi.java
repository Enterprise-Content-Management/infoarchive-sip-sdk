/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.opentext.ia.sdk.support.io.EncodedHash;
import com.opentext.ia.sdk.support.io.HashAssembler;
import com.opentext.ia.sdk.support.io.ZipAssembler;

/**
 * A ContentAssembler implementation which will deduplication based on the reference information of a digital object.
 * That is a digital object with a given reference information is only included once in the SIP. Optionally can also
 * validate that the same reference information is not used to reference different digital objects and that the same
 * digital object is not included twice with different reference information.
 * @param <D> The type of domain object to assemble SIPs from
 */
public class ContentAssemblerWithDedupOnRi<D> extends ContentAssemblerDefault<D> {

  private final Map<String, ContentInfo> riToContentInfo;
  private final Map<Collection<EncodedHash>, String> hashesToRi;
  private final boolean errorWhenEqualHashAndNotEqualRI;
  private final boolean errorWhenEqualRiAndNotEqualHash;

  public ContentAssemblerWithDedupOnRi(DigitalObjectsExtraction<D> contentsExtraction,
      HashAssembler contentHashAssembler, boolean errorWhenEqualRiAndNotEqualHash,
      boolean errorWhenEqualHashAndNotEqualRI, int estimatedMaxDigitalObjects) {
    super(contentsExtraction, contentHashAssembler);
    riToContentInfo = new HashMap<>(estimatedMaxDigitalObjects);
    hashesToRi = new HashMap<>(estimatedMaxDigitalObjects);
    this.errorWhenEqualHashAndNotEqualRI = errorWhenEqualHashAndNotEqualRI;
    this.errorWhenEqualRiAndNotEqualHash = errorWhenEqualRiAndNotEqualHash;
  }

  @Override
  public void begin(ZipAssembler zip, Counters metrics) {
    super.begin(zip, metrics);
    riToContentInfo.clear();
  }

  @Override
  protected ContentInfo addContent(String ri, DigitalObject digitalObject) throws IOException {
    ContentInfo contentInfo = riToContentInfo.get(ri);
    if (contentInfo == null) {
      ContentInfo newContentInfo = super.addContent(ri, digitalObject);
      checkNotAlreadyIncluded(newContentInfo);
      riToContentInfo.put(ri, newContentInfo);
      hashesToRi.put(newContentInfo.getContentHashes(), ri);
      return newContentInfo;
    } else {
      checkSameRIMeansSameContent(ri, digitalObject, contentInfo.getContentHashes());
      return contentInfo;
    }
  }

  private void checkNotAlreadyIncluded(ContentInfo newContentInfo) {
    if (errorWhenEqualHashAndNotEqualRI) {
      String ri = hashesToRi.get(newContentInfo.getContentHashes());
      if (ri != null && !ri.equals(newContentInfo.getReferenceInformation())) {
        throw new IllegalStateException("Content already included with a different ri.");
      }
    }
  }

  private void checkSameRIMeansSameContent(String ri, DigitalObject digitalObject,
      Collection<EncodedHash> existingHashes) throws IOException {
    if (errorWhenEqualRiAndNotEqualHash) {
      Collection<EncodedHash> hashes = contentHashFor(digitalObject);
      if (!hashes.equals(existingHashes)) {
        throw new IllegalStateException(
            "The same reference information (" + ri + ") was used to reference 2 distinct digital objects.");
      }
    }
  }

  private Collection<EncodedHash> contentHashFor(DigitalObject digitalObject) throws IOException {
    try (InputStream stream = digitalObject.get()) {
      return contentHashFor(stream);
    }
  }

}
