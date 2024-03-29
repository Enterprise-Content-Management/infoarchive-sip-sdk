/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;


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
    this.peas = SerializationUtils.clone(new HashMap<>(peas));
  }

}
