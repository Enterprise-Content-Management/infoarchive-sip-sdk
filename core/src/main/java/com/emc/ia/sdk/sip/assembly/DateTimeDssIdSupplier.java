/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.util.Date;

import com.emc.ia.sdk.support.datetime.Dates;


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
