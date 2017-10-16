/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opentext.ia.sdk.support.http.rest.LinkContainer;


public class ProductInfo extends LinkContainer {

  private BuildProperties buildProperties;


  public BuildProperties getBuildProperties() {
    return buildProperties;
  }

  public void setBuildProperties(BuildProperties buildProperties) {
    this.buildProperties = buildProperties;
  }


  public static class BuildProperties {

    @JsonProperty("ia.server.version")
    private String serverVersion;
    @JsonProperty("ia.server.version.label")
    private String oldServerVersion;

    public String getServerVersion() {
      return serverVersion == null ? oldServerVersion : serverVersion;
    }

    public void setServerVersion(String serverVersion) {
      this.serverVersion = serverVersion;
    }

    public String getOldServerVersion() {
      return oldServerVersion;
    }

    public void setOldServerVersion(String oldServerVersion) {
      this.oldServerVersion = oldServerVersion;
    }

  }

}
