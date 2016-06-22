/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sip.assembly.velocity;

import org.apache.velocity.tools.ConversionUtils;

import com.emc.ia.sdk.support.datetime.Dates;


public class DatesTool {

  public String format(Object date) {
    return Dates.toIso(ConversionUtils.toDate(date));
  }

}
