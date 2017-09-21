/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * A pattern for writing variable strings based on domain objects.
 * @param <D> The type of domain object to replace with text
 */
public interface Template<D> {

  /**
   * Write the starting text.
   * @param writer The writer to write to
   * @throws IOException When an I/O error occurs
   */
  void writeHeader(PrintWriter writer) throws IOException;

  /**
   * Write the template text with placeholders replaced by the given values.
   * @param domainObject The domain object which will be used as a basis for replacement of placeholders in the template
   *          with actual values
   * @param contentInfo Reference information and hashes for content associated with the domain object
   * @param writer The writer to write to
   * @throws IOException When an I/O error occurs
   */
  void writeRow(D domainObject, Map<String, ContentInfo> contentInfo, PrintWriter writer) throws IOException;

  /**
   * Write the closing text.
   * @param writer The writer to write to
   * @throws IOException When an I/O error occurs
   */
  void writeFooter(PrintWriter writer) throws IOException;

}
