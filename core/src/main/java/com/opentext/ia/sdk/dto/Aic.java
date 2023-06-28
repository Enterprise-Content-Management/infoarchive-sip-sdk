/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;


public class Aic extends NamedLinkContainer {

  private List<Criterion> criterias = new ArrayList<>();
  private List<String> holdings = new ArrayList<>();

  public List<Criterion> getCriterias() {
    return criterias;
  }

  public void setCriterias(List<Criterion> criterias) {
    this.criterias = new ArrayList<>(criterias.size());
    this.criterias.addAll(criterias);
  }

  public List<String> getHoldings() {
    return holdings;
  }

  public void setHoldings(List<String> holdings) {
    this.holdings = new ArrayList<>(holdings.size());

    this.holdings.addAll(holdings);
  }

}
