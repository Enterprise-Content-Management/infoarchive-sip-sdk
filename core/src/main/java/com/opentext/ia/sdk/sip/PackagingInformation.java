/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Date;
import java.util.Optional;

import com.opentext.ia.sdk.sip.DataSubmissionSession.DataSubmissionSessionBuilder;
import com.opentext.ia.sdk.support.io.EncodedHash;

/**
 * <a href="http://public.ccsds.org/publications/archive/650x0m2.pdf">Packaging Information</a> for a Submission
 * Information Package (SIP), describing the contents of the SIP. It is probably easiest created using a
 * {@linkplain #builder() builder}.
 */
public class PackagingInformation {

  private DataSubmissionSession dss;
  private Date productionDate;
  private int sequenceNumber;
  private boolean last;
  private long aiuCount;
  private long pageCount;
  private Optional<EncodedHash> pdiHash;

  public PackagingInformation(DataSubmissionSession dss, Date productionDate, int sequenceNumber, boolean isLast,
      long aiuCount, long pageCount, Optional<EncodedHash> pdiHash) {
    this.dss = dss;
    this.productionDate = productionDate;
    this.sequenceNumber = sequenceNumber;
    this.last = isLast;
    this.aiuCount = aiuCount;
    this.pageCount = pageCount;
    this.pdiHash = pdiHash;
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

  @Override
  public String toString() {
    return getClass().getSimpleName() + " [dss=" + dss + ", productionDate=" + productionDate + ", sequenceNumber="
        + sequenceNumber + ", isLast=" + last + ", aiuCount=" + aiuCount + ", pageCount=" + pageCount + ", hashes="
        + pdiHash + "]";
  }

  /**
   * @return A <a href="http://c2.com/cgi/wiki?BuilderPattern">builder</a> object to construct
   *         {@linkplain PackagingInformation}
   */
  public static PackagingInformationBuilder builder() {
    return new PackagingInformationBuilder();
  }

  public static PackagingInformationBuilder builder(PackagingInformation prototype) {
    return builder().dss(prototype.getDss())
      .last(prototype.isLast())
      .pageCount(prototype.getPageCount())
      .productionDate(prototype.getProductionDate())
      .sequenceNumber(prototype.getSequenceNumber());
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

    public PackagingInformation build() {
      return new PackagingInformation(dssBuilder.build(), productionDate, sequenceNumber, isLast, aiuCount, pageCount,
          encodedHash);
    }

  }

}
