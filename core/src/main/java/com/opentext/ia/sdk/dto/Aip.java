/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

public class Aip extends NamedLinkContainer {

  private String externalId;
  private String stateCode;
  private String phaseCode;
  private Integer sipSeqno;
  private boolean sipIsLast;
  private Long sipAiuCount;
  private Long sipPageCount;
  private String sipPdiHashAlgorithm;
  private String sipPdiHashEncoding;
  private String sipPdiHash;
  private boolean dirty;

  public Integer getSipSeqno() {
    return sipSeqno;
  }

  public void setSipSeqno(Integer sipSeqno) {
    this.sipSeqno = sipSeqno;
  }

  public boolean isSipIsLast() {
    return sipIsLast;
  }

  public void setSipIsLast(boolean sipIsLast) {
    this.sipIsLast = sipIsLast;
  }

  public Long getSipAiuCount() {
    return sipAiuCount;
  }

  public void setSipAiuCount(Long sipAiuCount) {
    this.sipAiuCount = sipAiuCount;
  }

  public Long getSipPageCount() {
    return sipPageCount;
  }

  public void setSipPageCount(Long sipPageCount) {
    this.sipPageCount = sipPageCount;
  }

  public String getSipPdiHashAlgorithm() {
    return sipPdiHashAlgorithm;
  }

  public void setSipPdiHashAlgorithm(String sipPdiHashAlgorithm) {
    this.sipPdiHashAlgorithm = sipPdiHashAlgorithm;
  }

  public String getSipPdiHashEncoding() {
    return sipPdiHashEncoding;
  }

  public void setSipPdiHashEncoding(String sipPdiHashEncoding) {
    this.sipPdiHashEncoding = sipPdiHashEncoding;
  }

  public String getSipPdiHash() {
    return sipPdiHash;
  }

  public void setSipPdiHash(String sipPdiHash) {
    this.sipPdiHash = sipPdiHash;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public String getPhaseCode() {
    return phaseCode;
  }

  public void setPhaseCode(String phaseCode) {
    this.phaseCode = phaseCode;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }
}
