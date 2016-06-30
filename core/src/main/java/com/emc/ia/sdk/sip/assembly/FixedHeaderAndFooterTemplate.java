/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

import com.emc.ia.sdk.support.io.RuntimeIoException;


/**
 * {@linkplain Template} with fixed header and footer texts.
 * @param <D> The type of domain object to replace with text
 */
public abstract class FixedHeaderAndFooterTemplate<D> implements Template<D> {

  private final String header;
  private final String footer;

  /**
   * Create an instance.
   * @param header The fixed header
   * @param footer The fixed footer
   */
  public FixedHeaderAndFooterTemplate(InputStream header, InputStream footer) {
    this(toString(header), toString(footer));
  }

  protected static String toString(InputStream stream) {
    try {
      return IOUtils.toString(stream);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }

  /**
   * Create an instance.
   * @param header The fixed header
   * @param footer The fixed footer
   */
  public FixedHeaderAndFooterTemplate(String header, String footer) {
    this.header = header;
    this.footer = footer;
  }

  @Override
  public void writeHeader(PrintWriter writer) {
    writer.println(header);
  }

  @Override
  public void writeFooter(PrintWriter writer) {
    writer.println(footer);
  }

}
