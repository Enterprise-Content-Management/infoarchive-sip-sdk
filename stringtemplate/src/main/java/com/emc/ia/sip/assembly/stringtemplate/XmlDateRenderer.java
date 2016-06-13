/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sip.assembly.stringtemplate;

import java.util.Date;
import java.util.Locale;

import org.stringtemplate.v4.AttributeRenderer;

import com.emc.ia.sdk.support.datetime.Dates;


class XmlDateRenderer implements AttributeRenderer {

  @Override
  public String toString(Object o, String formatString, Locale locale) {
    return Dates.toIso((Date)o);
  }

}
