/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import com.emc.ia.sdk.support.io.EncodedHash;


/**
 * Assemble a PDI using a {@linkplain Template}.
 * @param <D> The type of domain objects to assemble
 */
public class TemplatePdiAssembler<D> extends PdiAssembler<D> {

  private final Template<D> template;

  /** Create an instance that doesn't validate the PDI.
  * @param template The template to use
  */
  public TemplatePdiAssembler(Template<D> template) {
    this(template, null);
  }

  /** Create an instance.
  * @param template The template to use
  * @param validator Optional validator to check the assembled product
  */
  public TemplatePdiAssembler(Template<D> template, Validator validator) {
    super(validator);
    this.template = template;
  }

  @Override
  protected final void start(PrintWriter writer) throws IOException {
    template.writeHeader(writer);
  }

  @Override
  protected final void add(D domainObject, Map<String, Collection<EncodedHash>> hashes, PrintWriter writer)
      throws IOException {
    template.writeRow(domainObject, hashes, writer);
  }

  @Override
  protected final void end(PrintWriter writer) throws IOException {
    template.writeFooter(writer);
  }

}
