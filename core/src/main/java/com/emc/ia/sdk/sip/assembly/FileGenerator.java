/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Supplier;

import com.emc.ia.sdk.support.io.FileBuffer;
import com.emc.ia.sdk.support.io.FileSupplier;

/**
 * {@linkplain #generate(Iterable) Generate} a file by {@linkplain Assembler assembling} components.
 * @param <C> The type of components to assemble
 */
public class FileGenerator<C> extends Generator<C> {

  private final Supplier<File> fileSupplier;

  /**
   * Assemble a file using the given assembler into a temporary directory.
   * @param assembler The assembler to build up the file
   */
  public FileGenerator(Assembler<C> assembler) {
    this(assembler, FileSupplier.fromTemporaryDirectory());
  }

  /**
   * Assemble a file using the given assembler into the given directory.
   * @param assembler The assembler to build up the file
   * @param dir The directory in which to generate the file
   */
  public FileGenerator(Assembler<C> assembler, File dir) {
    this(assembler, FileSupplier.fromDirectory(dir));
  }

  /**
   * Assemble a file using the given assembler into the given directory.
   * @param assembler The assembler to build up the file
   * @param fileSupplier A function that supplies the file to write to
   */
  public FileGenerator(Assembler<C> assembler, Supplier<File> fileSupplier) {
    super(assembler);
    this.fileSupplier = fileSupplier;
  }

  /**
   * Generate a file by assembling components.
   * @param components The components to assemble
   * @return The generated file and metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public FileGenerationMetrics generate(Iterable<C> components) throws IOException {
    return generate(components.iterator());
  }

  /**
   * Generate a file by assembling components.
   * @param components The components to assemble
   * @return The generated file and metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public FileGenerationMetrics generate(Iterator<C> components) throws IOException {
    File result = fileSupplier.get();
    return new FileGenerationMetrics(result, generate(components, new FileBuffer(result)));
  }

  /**
   * Generate a file by assembling components.
   * @param components The components to assemble
   * @return The generated file and metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public FileGenerationMetrics generate(Enumeration<C> components) throws IOException {
    return generate(new EnumerationIterator<>(components));
  }

  /**
   * Generate a file from a single component.
   * @param component The component to generate the file from
   * @return The generated file and metrics about the generation process
   * @throws IOException When an I/O error occurs
   */
  public FileGenerationMetrics generate(C component) throws IOException {
    return generate(Collections.singletonList(component));
  }

}
