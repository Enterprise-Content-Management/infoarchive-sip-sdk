/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import com.emc.ia.sdk.support.io.DataBuffer;

/**
 * {@linkplain #generate(Iterable, DataBuffer) Generate} a product by {@linkplain Assembler assembling} components.
 * @param <C> The type of components to assemble
 */
public class Generator<C> {

  private final Assembler<C> assembler;

  /**
   * Assemble a product using the given {@linkplain Assembler}.
   * @param assembler The assembler to build up the product
   */
  public Generator(Assembler<C> assembler) {
    this.assembler = assembler;
  }

  /**
   * Generate a product by assembling components.
   * @param components The components to assemble
   * @param product The output to write the assembled product to
   * @return Metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public Metrics generate(Iterable<C> components, DataBuffer product) throws IOException {
    return generate(components.iterator(), product);
  }

  /**
   * Generate a product by assembling components.
   * @param components The components to assemble
   * @param product Storage for the assembled product
   * @return Metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public Metrics generate(Iterator<C> components, DataBuffer product) throws IOException {
    assembler.start(product);
    try {
      while (components.hasNext()) {
        assembler.add(components.next());
      }
    } finally {
      assembler.end();
    }
    return assembler.getMetrics();
  }

  /**
   * Generate a product by assembling components.
   * @param components The components to assemble
   * @param product The output to write the assembled product to
   * @return Metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public Metrics generate(Enumeration<C> components, DataBuffer product) throws IOException {
    return generate(new EnumerationIterator<C>(components), product);
  }

  /**
   * Generate a product from a single piece.
   * @param component The component to generate the product from
   * @param product The output to write the assembled product to
   * @return Metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public Metrics generate(C component, DataBuffer product) throws IOException {
    return generate(Collections.singletonList(component), product);
  }

}
