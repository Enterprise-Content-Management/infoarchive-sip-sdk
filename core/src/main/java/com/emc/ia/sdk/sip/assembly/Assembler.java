/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.IOException;

import com.emc.ia.sdk.support.io.DataBuffer;


/**
 * Assemble a product from components. The assembly process always progresses through the following stages:<ol>
 * <li>Start assembling using {@linkplain #start(DataBuffer)}</li>
 * <li>Add components to the assembly by repeatedly calling {@linkplain #add(Object)}</li>
 * <li>Make the assembly available through {@linkplain #end()}</li>
 * </ol>
 * @param <C> The type of components to assemble
 */
public interface Assembler<C> {

  /**
   * Start the assembly process.
   * @param product Storage for the assembled product
   * @throws IOException When an I/O error occurs
   */
  void start(DataBuffer product) throws IOException;

  /**
   * Add a component to the assembly. The assembler must be {@linkplain #start(DataBuffer) opened} first.
   * @param component The component to add
   * @throws IOException When an I/O error occurs
   */
  void add(C component) throws IOException;

  /**
   * Finish the assembly process.
   * @throws IOException When an I/O error occurs
   */
  void end() throws IOException;

  /**
   * Return metrics about the assembly process. Implementations will generally provide dedicated classes that you should
   * cast the result to.
   * @return Metrics about the assembly process, or <code>null</code> if no metrics are provided
   */
  Metrics getMetrics();

}