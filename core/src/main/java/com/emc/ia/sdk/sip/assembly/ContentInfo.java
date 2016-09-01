/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.emc.ia.sdk.support.io.EncodedHash;

/**
 * A reference information and its related hashes, if any.
 */
public class ContentInfo {

  private final Collection<EncodedHash> contentHashes;
  private final String referenceInformation;
  private final String mimeType;

  /**
   * Construct a new instance.
   * @param referenceInformation The identifying information of the content
   * @param contentHashes Any hashes over the content
   * @deprecated Use {@linkplain #ContentInfo(String, String, Collection)} instead.
   */
  @Deprecated
  public ContentInfo(String referenceInformation, Collection<EncodedHash> contentHashes) {
    this(referenceInformation, "application/octet-stream", contentHashes);
  }

  /**
   * Construct a new instance.
   * @param referenceInformation The identifying information of the content
   * @param mimeType The MIME type of the content
   * @param contentHashes Any hashes over the content
   */
  public ContentInfo(String referenceInformation, String mimeType, Collection<EncodedHash> contentHashes) {
    this.referenceInformation = Objects.requireNonNull(referenceInformation, "Missing reference information");
    this.mimeType = Objects.requireNonNull(mimeType, "Missing MIME type");
    this.contentHashes = Collections.unmodifiableList(new ArrayList<EncodedHash>(contentHashes));
  }

  /**
   * Return the hashes of the contents extracted from the source.
   * @return The hashes of the contents extracted from the source
   */
  public Collection<EncodedHash> getContentHashes() {
    return contentHashes;
  }

  public String getReferenceInformation() {
    return referenceInformation;
  }

  public String getMimeType() {
    return mimeType;
  }

  /**
   * Return a hash code value for this object.
   * @return A hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(referenceInformation, mimeType, contentHashes);
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * @param obj The reference object with which to compare
   * @return <code>true</code> if this object is the same as the reference object; <code>false</code> otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ContentInfo other = (ContentInfo)obj;
    return referenceInformation.equals(other.referenceInformation) && mimeType.equals(other.mimeType)
        && contentHashes.equals(other.contentHashes);
  }

  /**
   * Return a human-readable version of this object.
   * @return A human-readable version of this object
   */
  @Override
  public String toString() {
    return referenceInformation + " (" + mimeType + ") is hashed as " + contentHashes;
  }

}
