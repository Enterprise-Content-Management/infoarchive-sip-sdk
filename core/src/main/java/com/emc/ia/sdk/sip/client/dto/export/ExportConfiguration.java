/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto.export;

import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;

public class ExportConfiguration extends NamedLinkContainer {

  private String description;
  private String exportType;
  private String pipeline;
  private Transformation[] transformations;
  private Options options;

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

  public Transformation[] getTransformations() {
    return transformations == null ? null : this.transformations.clone();
  }

  public void setTransformations(Transformation[] transformations) {
    this.transformations = transformations.clone();
  }

  public Options getOptions() {
    return options;
  }

  public void setOptions(Options options) {
    this.options = options;
  }

  @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
  public static class Transformation {

    private String portName;
    private String transformation;

    public String getPortName() {
      return portName;
    }

    public void setPortName(String portName) {
      this.portName = portName;
    }

    public String getTransformation() {
      return transformation;
    }

    public void setTransformation(String transformation) {
      this.transformation = transformation;
    }
  }

  public static class Options {

    private String xslResultFormat;
    private String xqueryResultFormat;

    public String getXslResultFormat() {
      return xslResultFormat;
    }

    public void setXslResultFormat(String xslResultFormat) {
      this.xslResultFormat = xslResultFormat;
    }

    public String getXqueryResultFormat() {
      return xqueryResultFormat;
    }

    public void setXqueryResultFormat(String xqueryResultFormat) {
      this.xqueryResultFormat = xqueryResultFormat;
    }
  }
}
