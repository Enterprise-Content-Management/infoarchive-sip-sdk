/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.impl;

import com.github.zafarkhaja.semver.Version;


/**
 * Cache of REST resources used for ingesting SIPs into an InfoArchive application.
 */
public class ApplicationIngestionResourcesCache {

  private final String applicationName;
  private String ciResourceUri;
  private String aipResourceUri;
  private String aipIngestDirectResourceUri;
  private String servicesUri;
  private String serverVersion;

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

  public String getServerVersion() {
    return serverVersion;
  }

  public void setServerVersion(String serverVersion) {
    this.serverVersion = serverVersion;
  }

  public boolean isVersionOrLater(String version) {
    return toVersion(getServerVersion()).compareTo(toVersion(version)) >= 0;
  }

  private Version toVersion(String version) {
    StringBuilder semVer = new StringBuilder(version);
    for (int i = 0; i < 2 - version.replaceAll("[^.]", "").length(); i++) {
      semVer.append(".0");
    }
    return Version.valueOf(semVer.toString());
  }

  public boolean isVersionOrEarlier(String version) {
    return toVersion(getServerVersion()).compareTo(toVersion(version)) <= 0;
  }

  public String getServicesUri() {
    return servicesUri;
  }

  public void setServicesUri(String servicesUri) {
    this.servicesUri = servicesUri;
  }
}
