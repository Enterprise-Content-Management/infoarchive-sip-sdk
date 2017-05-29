/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import java.util.HashMap;
import java.util.Map;


public class ContentAddressedStorage extends NamedLinkContainer {

  private String connexionString;
  private Map<String, String> peas = new HashMap<>();

  public String getConnexionString() {
    return connexionString;
  }

  public void setConnexionString(String connexionString) {
    this.connexionString = connexionString;
  }

  public Map<String, String> getPeas() {
    return peas;
  }

  public void setPeas(Map<String, String> peas) {
    this.peas = peas;
  }

}
