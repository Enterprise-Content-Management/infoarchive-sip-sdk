/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.assembly;

import java.util.Date;

import com.opentext.ia.sdk.support.datetime.Dates;


/**
 * Generate Data Submission Session (DSS) IDs from a prefix and the current date and time.
 */
public class DateTimeDssIdSupplier extends PrefixDssIdSupplier {

  /**
   * Create an instance.
   * @param prefix The prefix for all generated DSS IDs
   */
  public DateTimeDssIdSupplier(String prefix) {
    super(prefix);
  }

  @Override
  protected String postfix() {
    return Dates.toIso(new Date());
  }

}
