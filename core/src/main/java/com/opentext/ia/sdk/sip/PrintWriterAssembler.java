/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.validation.ValidationException;

import org.apache.commons.io.IOUtils;

import com.opentext.ia.sdk.support.io.DataBuffer;


/**
 * Base class for assembling a product from domain objects by writing to a {@linkplain PrintWriter}.
 * @param <D> The type of domain objects to assemble the product from
 */
public abstract class PrintWriterAssembler<D> implements Assembler<D> {

  private final Validator validator;
  private PrintWriter printWriter;
  private DataBuffer buffer;

  /**
   * Start the assembly process.
   * @param writer Where to write output
   * @throws IOException When an I/O error occures
   */
  protected abstract void start(PrintWriter writer) throws IOException;

  /**
   * Add a domain object.
   * @param domainObject The domain object to add
   * @param writer Where to write output
   * @throws IOException When an I/O error occures
   */
  protected abstract void add(D domainObject, PrintWriter writer) throws IOException;

  /**
   * End the assembly process.
   * @param writer Where to write output
   * @throws IOException When an I/O error occures
   */
  protected abstract void end(PrintWriter writer) throws IOException;

  /**
   * Create an instance that will not validate the assembled product.
   */
  public PrintWriterAssembler() {
    this(null);
  }

  /**
   * Create an instance.
   * @param validator Optional validator for checking whether the assembled product meets expectations
   */
  public PrintWriterAssembler(Validator validator) {
    this.validator = validator;
  }

  @Override
  public final void start(DataBuffer dataBuffer) throws IOException {
    buffer = dataBuffer;
    printWriter = new PrintWriter(new OutputStreamWriter(buffer.openForWriting(), StandardCharsets.UTF_8));
    start(printWriter);
  }

  @Override
  public final void add(D domainObject) throws IOException {
    Objects.requireNonNull(printWriter, "Should call start() first");
    add(domainObject, printWriter);
  }

  @Override
  public final void end() throws IOException {
    Objects.requireNonNull(printWriter, "Should call start() first");
    end(printWriter);
    printWriter.flush();
    printWriter.close();
    printWriter = null;
    if (validator != null) {
      validate();
    }
  }

  private void validate() throws IOException {
    try (InputStream output = buffer.openForReading()) {
      try {
        validator.validate(output);
      } catch (ValidationException e) {
        try (InputStream actual = buffer.openForReading()) {
          throw new IOException(String.format("Invalid content: %s%n%s", e.getMessage(),
              IOUtils.toString(actual, StandardCharsets.UTF_8)), e);
        }
      }
    }
  }

  @Nullable
  @Override
  @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract") // This *is* the real implementation
  public Metrics getMetrics() {
    return null;
  }

}
