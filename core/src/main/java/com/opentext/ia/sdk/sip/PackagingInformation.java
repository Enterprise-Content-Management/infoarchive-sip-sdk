/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.opentext.ia.sdk.sip.DataSubmissionSession.DataSubmissionSessionBuilder;
import com.opentext.ia.sdk.support.io.EncodedHash;

/**
 * <a href="http://public.ccsds.org/publications/archive/650x0m2.pdf">Packaging Information</a> for a Submission
 * Information Package (SIP), describing the contents of the SIP. It is probably easiest created using a
 * {@linkplain #builder() builder}.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class PackagingInformation {

  private DataSubmissionSession dss;
  private Date productionDate;
  private int sequenceNumber;
  private boolean last;
  private long aiuCount;
  private long pageCount;
  private Optional<EncodedHash> pdiHash;
  private final Map<String, String> customAttributes = new HashMap<>();
  private final String externalId;

  public PackagingInformation(DataSubmissionSession dss, Date productionDate, int sequenceNumber, boolean isLast,
      long aiuCount, long pageCount, Optional<EncodedHash> pdiHash) {
    this(dss, productionDate, sequenceNumber, isLast, aiuCount, pageCount, pdiHash, Collections.emptyMap(), null);
  }

  public PackagingInformation(DataSubmissionSession dss, Date productionDate, int sequenceNumber, boolean isLast,
      long aiuCount, long pageCount, Optional<EncodedHash> pdiHash, Map<String, String> customAttributes, String externalId) {
    this.dss = dss;
    this.productionDate = productionDate;
    this.sequenceNumber = sequenceNumber;
    this.last = isLast;
    this.aiuCount = aiuCount;
    this.pageCount = pageCount;
    this.pdiHash = pdiHash;
    this.customAttributes.putAll(customAttributes);
    this.externalId = externalId;
  }

  public DataSubmissionSession getDss() {
    return dss;
  }

  public void setDss(DataSubmissionSession dss) {
    this.dss = dss;
  }

  public Date getProductionDate() {
    return productionDate;
  }

  public void setProductionDate(Date productionDate) {
    this.productionDate = productionDate;
  }

  public int getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public boolean isLast() {
    return last;
  }

  public void setLast(boolean last) {
    this.last = last;
  }

  public long getAiuCount() {
    return aiuCount;
  }

  public void setAiuCount(long aiuCount) {
    this.aiuCount = aiuCount;
  }

  public long getPageCount() {
    return pageCount;
  }

  public void setPageCount(long pageCount) {
    this.pageCount = pageCount;
  }

  public Optional<EncodedHash> pdiHash() {
    return pdiHash;
  }

  public void setHash(EncodedHash encodedHash) {
    this.pdiHash = Optional.of(encodedHash);
  }

  public Map<String, String> getCustomAttributes() {
    return Collections.unmodifiableMap(customAttributes);
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes.putAll(customAttributes);
  }

  public String getExternalId() {
    return externalId;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " [dss=" + dss + ", productionDate=" + productionDate + ", sequenceNumber="
        + sequenceNumber + ", isLast=" + last + ", aiuCount=" + aiuCount + ", pageCount=" + pageCount + ", hashes="
        + pdiHash + ", externalId=" + externalId + "]";
  }

  /**
   * @return A <a href="http://c2.com/cgi/wiki?BuilderPattern">builder</a> object to construct
   *         {@linkplain PackagingInformation}
   */
  public static PackagingInformationBuilder builder() {
    return new PackagingInformationBuilder();
  }

  public static PackagingInformationBuilder builder(PackagingInformation prototype) {
    PackagingInformationBuilder result = builder()
      .dss(prototype.getDss())
      .last(prototype.isLast())
      .pageCount(prototype.getPageCount())
      .productionDate(prototype.getProductionDate())
      .sequenceNumber(prototype.getSequenceNumber());
    prototype.getCustomAttributes().forEach(result::customAttribute);

    // we want to ensure the externalId is not blank
    if (StringUtils.isNotBlank(prototype.getExternalId())) {
      result.externalId(prototype.getExternalId());
    }

    return result;
  }

  /**
   * <a href="http://c2.com/cgi/wiki?BuilderPattern">Builder</a> object to construct {@linkplain PackagingInformation}.
   */
  public static class PackagingInformationBuilder {

    private final DataSubmissionSessionBuilder dssBuilder = new DataSubmissionSessionBuilder(this);
    private Optional<EncodedHash> encodedHash = Optional.empty();
    private Date productionDate = new Date();
    private int sequenceNumber = 1;
    private boolean isLast = true;
    private long aiuCount;
    private long pageCount;
    private final Map<String, String> customAttributes = new HashMap<>();
    private String externalId;

    public PackagingInformationBuilder dss(DataSubmissionSession dss) {
      dssBuilder.from(dss);
      return this;
    }

    public DataSubmissionSessionBuilder dss() {
      return dssBuilder;
    }

    public PackagingInformationBuilder productionDate(Date date) {
      this.productionDate = date;
      return this;
    }

    public PackagingInformationBuilder sequenceNumber(int sequence) {
      this.sequenceNumber = sequence;
      return this;
    }

    public PackagingInformationBuilder last(boolean last) {
      this.isLast = last;
      return this;
    }

    public PackagingInformationBuilder aiuCount(long count) {
      this.aiuCount = count;
      return this;
    }

    public PackagingInformationBuilder pageCount(long count) {
      this.pageCount = count;
      return this;
    }

    public PackagingInformationBuilder pdiHash(Optional<EncodedHash> other) {
      this.encodedHash = other;
      return this;
    }

    public PackagingInformationBuilder encodedHash(EncodedHash other) {
      return pdiHash(Optional.of(other));
    }

    public PackagingInformationBuilder hash(String algorithm, String encoding, String value) {
      return encodedHash(new EncodedHash(algorithm, encoding, value));
    }

    public PackagingInformationBuilder customAttribute(String name, String value) {
      customAttributes.put(name, value);
      return this;
    }

    public PackagingInformationBuilder externalId(String id) {
        this.externalId = id;
        return this;
    }

    public PackagingInformation build() {
      return new PackagingInformation(dssBuilder.build(), productionDate, sequenceNumber, isLast, aiuCount, pageCount,
          encodedHash, customAttributes, externalId);
    }

  }

}
