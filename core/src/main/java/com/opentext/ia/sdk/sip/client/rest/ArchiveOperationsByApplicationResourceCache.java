/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.rest;

import java.util.Map;

public class ArchiveOperationsByApplicationResourceCache {

  private final String applicationName;
  private String ciResourceUri;
  private String aipResourceUri;
  private String aipIngestDirectResourceUri;
  private Map<String, String> dipResourceUriByAicName;

  public ArchiveOperationsByApplicationResourceCache(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getCiResourceUri() {
    return ciResourceUri;
  }

  public void setCiResourceUri(String ciResourceUri) {
    this.ciResourceUri = ciResourceUri;
  }

  public String getAipResourceUri() {
    return aipResourceUri;
  }

  public void setAipResourceUri(String aipResourceUri) {
    this.aipResourceUri = aipResourceUri;
  }

  public String getAipIngestDirectResourceUri() {
    return aipIngestDirectResourceUri;
  }

  public void setAipIngestDirectResourceUri(String aipIngestDirectResourceUri) {
    this.aipIngestDirectResourceUri = aipIngestDirectResourceUri;
  }

  public Map<String, String> getDipResourceUriByAicName() {
    return dipResourceUriByAicName;
  }

  public void setDipResourceUriByAicName(Map<String, String> dipResourceUriByAicName) {
    this.dipResourceUriByAicName = dipResourceUriByAicName;
  }

  public String getApplicationName() {
    return applicationName;
  }

}
