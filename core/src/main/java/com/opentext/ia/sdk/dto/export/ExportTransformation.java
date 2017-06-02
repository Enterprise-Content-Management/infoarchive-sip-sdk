/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.export;

import com.opentext.ia.sdk.dto.NamedLinkContainer;


public class ExportTransformation extends NamedLinkContainer {

  private String description;
  private String type;
  private String mainPath;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMainPath() {
    return mainPath;
  }

  public void setMainPath(String mainPath) {
    this.mainPath = mainPath;
  }

}
