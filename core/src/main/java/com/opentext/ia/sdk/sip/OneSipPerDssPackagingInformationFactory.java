/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Optional;
import java.util.function.Supplier;

import com.opentext.ia.sdk.support.io.EncodedHash;

/**
 * {@linkplain PackagingInformationFactory} decorator that sets the Data Submission Session (DSS) ID according to
 * some strategy. This effectively guarantees that each SIP is part of a new DSS.
 */
public class OneSipPerDssPackagingInformationFactory implements PackagingInformationFactory {

  private final PackagingInformationFactory decorated;
  private final Supplier<String> dssIdSupplier;

  /**
   * Create an instance.
   * @param decorated The decorated factory
   * @param dssIdSupplier The strategy for generating DSS IDs
   */
  public OneSipPerDssPackagingInformationFactory(PackagingInformationFactory decorated,
      Supplier<String> dssIdSupplier) {
    this.decorated = decorated;
    this.dssIdSupplier = dssIdSupplier;
  }

  @Override
  public PackagingInformation newInstance(long aiuCount, Optional<EncodedHash> pdiHash) {
    decorated.setFinalSipInDss(true);
    PackagingInformation result = decorated.newInstance(aiuCount, pdiHash);
    result.setDss(DataSubmissionSession.builder()
      .from(result.getDss())
      .id(dssIdSupplier.get())
      .build());
    return result;
  }

  @Override
  public void setFinalSipInDss(boolean finalSipInDss) {
    // Ignored
  }

}
