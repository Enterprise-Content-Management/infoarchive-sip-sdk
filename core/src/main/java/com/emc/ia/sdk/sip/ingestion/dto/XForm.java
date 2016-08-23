/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

public class XForm extends NamedLinkContainer {

  private String form;
  private String searchName;
  private String compositionName;

  public String getForm() {
    return form;
  }

  public final void setForm(String form) {
    this.form = form;
  }

  public String getSearchName() {
    return searchName;
  }

  public final void setSearchName(String searchName) {
    this.searchName = searchName;
  }

  public String getCompositionName() {
    return compositionName;
  }

  public final void setCompositionName(String compositionName) {
    this.compositionName = compositionName;
  }

}
