/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.export;

import com.opentext.ia.sdk.dto.NamedLinkContainer;


public class ExportPipeline extends NamedLinkContainer {

  private String description;
  private String outputFormat;
  private String inputFormat;
  private String envelopeFormat;
  private String type;
  private String content;
  private boolean includesContent;
  private boolean composite;
  private boolean collectionBasedExport;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOutputFormat() {
    return outputFormat;
  }

  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  public String getEnvelopeFormat() {
    return envelopeFormat;
  }

  public void setEnvelopeFormat(String envelopeFormat) {
    this.envelopeFormat = envelopeFormat;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isIncludesContent() {
    return includesContent;
  }

  public void setIncludesContent(boolean includesContent) {
    this.includesContent = includesContent;
  }

  public boolean isComposite() {
    return composite;
  }

  public void setComposite(boolean composite) {
    this.composite = composite;
  }

  public String getInputFormat() {
    return inputFormat;
  }

  public void setInputFormat(String inputFormat) {
    this.inputFormat = inputFormat;
  }

  public boolean isCollectionBasedExport() {
    return collectionBasedExport;
  }

  public void setCollectionBasedExport(boolean collectionBasedExport) {
    this.collectionBasedExport = collectionBasedExport;
  }

}
