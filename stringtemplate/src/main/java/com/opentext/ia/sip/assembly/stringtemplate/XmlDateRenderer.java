/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sip.assembly.stringtemplate;

import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

import org.stringtemplate.v4.AttributeRenderer;

import com.opentext.ia.sdk.support.datetime.Dates;


class XmlDateRenderer implements AttributeRenderer<Date> {

  @Nullable
  @Override
  public String toString(Date o, String formatString, Locale locale) {
    return Dates.toIso(o);
  }

}
