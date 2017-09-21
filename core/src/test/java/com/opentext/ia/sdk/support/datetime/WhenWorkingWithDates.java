/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.datetime;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.opentext.ia.test.RandomData;


public class WhenWorkingWithDates {

  private final RandomData random = new RandomData();

  @Test
  public void shouldFormatXsdDateTime() {
    Date dateTime = randomDate();
    TimeZone timeZone = TimeZone.getDefault();
    int offset = timeZone.getOffset(dateTime.getTime()) / 1000;
    String sign = Arrays.asList("-", "", "+")
      .get(1 + (int)Math.signum(offset));
    offset = Math.abs(offset);
    int tzHour = offset / 60 / 60;
    int tzMinute = offset / 60 % 60;
    String expected = String.format("%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS%2$s", dateTime, sign);
    if (tzHour == 0 && tzMinute == 0) {
      expected += "Z";
    } else {
      expected += String.format("%02d:%02d", tzHour, tzMinute);
    }

    String actual = Dates.toIso(dateTime);

    assertEquals("Date time", expected, actual);
  }

  @SuppressWarnings("deprecation")
  private Date randomDate() {
    int year = random.integer(1945, 2045);
    int month = random.integer(1, 12);
    int day = random.integer(1, 28);
    int hour = random.integer(0, 23);
    int minute = random.integer(0, 59);
    int second = random.integer(0, 29) * 2;
    return new Date(year - 1900, month - 1, day, hour, minute, second);
  }

  @Test
  public void shouldReturnNullOnMissingDateTime() {
    assertNull("To ISO", Dates.toIso(null));
    assertNull("From ISO", Dates.fromIso(null));
  }

  @Test
  public void shouldParseXsdDateTime() {
    Date expected = randomDate();

    Date actual = Dates.fromIso(Dates.toIso(expected));

    long deltaSeconds = Math.abs(actual.getTime() - expected.getTime()) / 1000;
    assertEquals("Date time", 0, deltaSeconds);
  }

}
