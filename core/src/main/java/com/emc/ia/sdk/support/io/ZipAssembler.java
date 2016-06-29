/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;


/**
 * Assemble a ZIP from entries. The ZIP assembly process consists of the following stages:
 * <ol>
 * <li>{@linkplain #begin(OutputStream) Begin} the assembly process</li>
 * <li>{@linkplain #addEntry(String, InputStream, HashAssembler) Add} entries to the ZIP zero or more times</li>
 * <li>{@linkplain #close() Close} the ZIP</li>
 * </ol>
 */
public interface ZipAssembler extends Closeable {

  /**
   * Start the ZIP assembly process.
   * @param output Where the ZIP will be written
   * @throws IOException When an I/O error occurs
   */
  void begin(OutputStream output) throws IOException;

  /**
   * Add an entry to the ZIP.
   * @param name The name of the entry to add
   * @param content The content of the entry to add
   * @param hashAssembler The hasher to will calculate the hash of the entry's content
   * @return Any calculated hashes
   * @throws IOException When an I/O error occurs
   */
  Collection<EncodedHash> addEntry(String name, InputStream content, HashAssembler hashAssembler) throws IOException;

}
