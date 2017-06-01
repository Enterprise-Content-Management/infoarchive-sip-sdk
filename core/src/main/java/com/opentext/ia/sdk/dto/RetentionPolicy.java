/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

public class RetentionPolicy extends NamedLinkContainer {

  private AgingStrategy agingStrategy;
  private DispositionStrategy dispositionStrategy;

  public RetentionPolicy() {
    setAgingStrategy(new AgingStrategy());
    setDispositionStrategy(new DispositionStrategy());
  }

  public AgingStrategy getAgingStrategy() {
    return agingStrategy;
  }

  public final void setAgingStrategy(AgingStrategy agingStrategy) {
    this.agingStrategy = agingStrategy;
  }

  public DispositionStrategy getDispositionStrategy() {
    return dispositionStrategy;
  }

  public final void setDispositionStrategy(DispositionStrategy dispositionStrategy) {
    this.dispositionStrategy = dispositionStrategy;
  }

}
