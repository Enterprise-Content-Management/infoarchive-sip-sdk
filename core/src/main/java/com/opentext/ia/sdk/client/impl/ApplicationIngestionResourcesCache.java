/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.impl;

import java.util.Map;


/**
 * Cache of REST resources used for ingesting SIPs into an InfoArchive application.
 */
public class ApplicationIngestionResourcesCache {

  private final String applicationName;
  private String ciResourceUri;
  private String aipResourceUri;
  private String aipIngestDirectResourceUri;
  private Map<String, String> dipResourceUriByAicName;

  public ApplicationIngestionResourcesCache(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getApplicationName() {
    return applicationName;
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

}
