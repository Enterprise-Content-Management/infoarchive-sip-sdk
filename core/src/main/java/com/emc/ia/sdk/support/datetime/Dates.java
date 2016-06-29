/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.datetime;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;


/**
 * Utilities for working with dates.
 */
public final class Dates {

  private Dates() {
    // Utility class
  }

  /**
   * Convert a date to <a href="https://tools.ietf.org/html/rfc3339#section-5.6">ISO 8601 dateTime</a> format.
   * @param dateTime The datetime to convert
   * @return The dateTime in ISO format
   */
  public static String toIso(Date dateTime) {
    if (dateTime == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateTime);
    return DatatypeConverter.printDateTime(calendar);
  }

  /**
   * Convert an <a href="https://tools.ietf.org/html/rfc3339#section-5.6">ISO 8601 dateTime</a> string to a date.
   * @param dateTime The ISO dateTime to convert
   * @return The converted dateTime
   */
  public static Date fromIso(String dateTime) {
    if (dateTime == null) {
      return null;
    }
    return DatatypeConverter.parseDateTime(dateTime).getTime();
  }

}
