/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sip.assembly.velocity;

import org.apache.velocity.tools.ConversionUtils;

import com.opentext.ia.sdk.support.datetime.Dates;


public class DatesTool {

  public String format(Object date) {
    return Dates.toIso(ConversionUtils.toDate(date));
  }

}
