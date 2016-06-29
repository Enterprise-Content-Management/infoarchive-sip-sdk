/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Sips {

  private String format = "zip_zip";
  private String extractorImpl = "com.emc.ia.reception.sip.extractor.impl.ZipSipExtractor";

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getExtractorImpl() {
    return extractorImpl;
  }

  public void setExtractorImpl(String extractorImpl) {
    this.extractorImpl = extractorImpl;
  }

}
