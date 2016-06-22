/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import com.emc.ia.sdk.support.io.EncodedHash;


/**
 * Assemble a PDI from domain objects.
 * @param <D> The type of domain objects to assemble the PDI from
 */
public abstract class PdiAssembler<D> extends PrintWriterAssembler<HashedContents<D>> {

  /**
   * Create an instance.
   * @param validator Optional validator for checking whether the PDI meets expectations
   */
  public PdiAssembler(Validator validator) {
    super(validator);
  }

  @Override
  protected final void add(HashedContents<D> hashedContents, PrintWriter writer) throws IOException {
    add(hashedContents.getSource(), hashedContents.getContentHashes(), writer);
  }

  /**
   * Add a domain object.
   * @param domainObject The domain object to add
   * @param contentHashes The encoded hashes of the content associated with the domain object
   * @param writer The writer to print the PDI fragment to
   * @throws IOException When an I/O error occures
   */
  protected abstract void add(D domainObject, Map<String, Collection<EncodedHash>> contentHashes, PrintWriter writer)
      throws IOException;

}
