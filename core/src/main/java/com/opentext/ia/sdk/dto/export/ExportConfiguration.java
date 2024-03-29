/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import com.opentext.ia.sdk.dto.NamedLinkContainer;


public class ExportConfiguration extends NamedLinkContainer {

  private String description;
  private String exportType;
  private String pipeline;
  private List<Transformation> transformations = new ArrayList<>();
  private Map<String, String> options = new HashMap<>();
  private Map<String, String> encryptedOptions = new HashMap<>();

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

  public List<Transformation> getTransformations() {
    return transformations;
  }

  public void setTransformations(List<Transformation> transformations) {
    this.transformations = new ArrayList<>(transformations.size());
    this.transformations.addAll(transformations);
  }

  public Map<String, String> getOptions() {
    return new HashMap<String, String>(options);
  }

  public void setOptions(Map<String, String> options) {
    this.options = options;
  }

  public Map<String, String> getEncryptedOptions() {
    return new HashMap<String, String>(encryptedOptions);
  }

  public void setEncryptedOptions(Map<String, String> encryptedOptions) {
    this.encryptedOptions = SerializationUtils.clone(new HashMap<>(encryptedOptions));
  }


  public enum DefaultOption {

    XSL_RESULT_FORMAT("xslResultFormat"), XQUERY_RESULT_FORMAT("xqueryResultFormat");

    private final String name;

    DefaultOption(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

  }


  @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
  public static class Transformation {

    private String portName;
    private String name;

    public String getPortName() {
      return portName;
    }

    public void setPortName(String portName) {
      this.portName = portName;
    }

    public String getName() {
      return name;
    }

    public void setName(String transformation) {
      this.name = transformation;
    }
  }
}
