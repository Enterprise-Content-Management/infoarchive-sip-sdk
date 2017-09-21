/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Date;
import java.util.Optional;

import com.opentext.ia.sdk.support.io.EncodedHash;

/**
 * Default implementation of {@linkplain PackagingInformationFactory}.
 */
public class DefaultPackagingInformationFactory implements PackagingInformationFactory {

  private final PackagingInformation prototype;
  private boolean finalSipInDss;
  private int numSipsInDss;

  public DefaultPackagingInformationFactory(PackagingInformation prototype) {
    this.prototype = prototype;
    // Assume single SIP in DSS. This makes the factory easy to use in a default setting. This assumption will be
    // changed when generating multiple SIPs in a batch using BatchSipAssembler.
    this.finalSipInDss = true;
  }

  @Override
  public PackagingInformation newInstance(long aiuCount, Optional<EncodedHash> pdiHash) {
    PackagingInformation result = PackagingInformation.builder(prototype)
      .productionDate(new Date())
      .aiuCount(aiuCount)
      .pdiHash(pdiHash)
      .sequenceNumber(++numSipsInDss)
      .last(finalSipInDss)
      .build();
    if (finalSipInDss) {
      numSipsInDss = 0;
      finalSipInDss = false;
    }
    return result;
  }

  @Override
  public void setFinalSipInDss(boolean finalSipInDss) {
    this.finalSipInDss = finalSipInDss;
  }

}
