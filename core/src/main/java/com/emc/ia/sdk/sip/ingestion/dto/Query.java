/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.ArrayList;
import java.util.List;

public class Query extends NamedLinkContainer {
  private List<NameSpace> nameSpaces;

  public Query() {
    nameSpaces = new ArrayList<NameSpace>();
    //TODO - need to change default namesapce
    NameSpace ns = new NameSpace("urn:emc:demo:email.xsd.1.0", "n");
    nameSpaces.add(ns);
  }

  public List<NameSpace> getNameSpaces() {
    return nameSpaces;
  }

  public void setNameSpaces(List<NameSpace> nameSpaces) {
    this.nameSpaces = nameSpaces;
  }

}
