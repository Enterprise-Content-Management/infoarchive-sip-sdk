/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import com.opentext.ia.test.TestCase;


public class SipAssemblingTestCase extends TestCase {

  protected String getPackageInformation(InputStream sip) throws IOException {
    try (ZipInputStream zip = new ZipInputStream(sip)) {
      ZipEntry entry = zip.getNextEntry();
      while (entry != null) {
        if ("eas_sip.xml".equals(entry.getName())) {
          return IOUtils.toString(zip, StandardCharsets.UTF_8);
        }
        zip.closeEntry();
        entry = zip.getNextEntry();
      }
    }
    return null;
  }

  protected int getSeqNo(String packageInformation) {
    return Integer.parseInt(getXmlElement(packageInformation, "seqno"));
  }

  private String getXmlElement(String xml, String tag) {
    String regex = String.format(".*<%1$s>(?<text>[^<]*)<\\/%1$s>.*", tag);
    Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(xml);
    return matcher.matches() ? matcher.group("text") : null;
  }

  protected boolean isLast(String packageInformation) {
    return Boolean.parseBoolean(getXmlElement(packageInformation, "is_last"));
  }

}
