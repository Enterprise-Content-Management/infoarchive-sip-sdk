/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto.export;

import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;

public class ExportConfiguration extends NamedLinkContainer {

  private String description;
  private String exportType;
  private String pipeline;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExportType() {
    return exportType;
  }

  public void setExportType(String exportType) {
    this.exportType = exportType;
  }

  public String getPipeline() {
    return pipeline;
  }

  public void setPipeline(String pipeline) {
    this.pipeline = pipeline;
  }

}
