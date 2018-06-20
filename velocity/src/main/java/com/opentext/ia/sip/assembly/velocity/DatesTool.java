/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sip.assembly.velocity;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import com.opentext.ia.sdk.support.datetime.Dates;


/**
 * Tools for working with dates in Velocity templates.
 */
public class DatesTool {

  @Nullable
  public String format(Object date) {
    return Dates.toIso(toDate(date));
  }

  @Nullable
  private Date toDate(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof Date) {
      return (Date)obj;
    }
    if (obj instanceof Calendar) {
      return ((Calendar)obj).getTime();
    }
    if (obj instanceof Number) {
      Date result = new Date();
      result.setTime(((Number)obj).longValue());
      return result;
    }
    return null;
  }

}
