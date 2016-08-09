/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.ArrayList;
import java.util.List;

public class Aic extends NamedLinkContainer {

  private List<Criterion> criterias;
  private List<String> holdings;

  public Aic() {
    setCriterias(new ArrayList<>());
    setHoldings(new ArrayList<>());
  }

  public List<Criterion> getCriterias() {
    return criterias;
  }

  public void setCriterias(List<Criterion> criterias) {
    this.criterias = criterias;
  }

  public List<String> getHoldings() {
    return holdings;
  }

  public void setHoldings(List<String> holdings) {
    this.holdings = holdings;
  }

}
