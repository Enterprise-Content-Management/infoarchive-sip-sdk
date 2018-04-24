/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;

import com.opentext.ia.sdk.support.io.RuntimeIoException;


/**
 * Digital Object (an object composed of a set of bit sequences) that is the original target of
 * <a href="http://public.ccsds.org/publications/archive/650x0m2.pdf">Long Term Preservation</a> in an Archive.
 * The object is identified by {@linkplain #getReferenceInformation() Reference Information} and its bits are accessed
 * through an {@linkplain #get() InputStream}.
 */
public interface DigitalObject extends Supplier<InputStream> {

  /**
   * A unique identifier for this object that can be used as Reference Information.
   * @return A unique identifier for this object
   */
  String getReferenceInformation();

  /**
   * Returns the size of the content. Determining this may be slow, depending on the source of the content.
   * @return the size of the content, in bytes
   * @since 8.5.0
   */
  long getSize();

  /**
   * Create a {@linkplain DigitalObject} from a file.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param file The file that holds the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromFile(String referenceInformation, File file) {
    return fromSupplier(referenceInformation, file::length, () -> {
      try {
        return Files.newInputStream(file.toPath(), StandardOpenOption.READ);
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
    });
  }

  /**
   * Create a {@linkplain DigitalObject} from a supplier.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param sizeSupplier The supplier of the size of the content
   * @param contentSupplier The supplier of the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromSupplier(String referenceInformation, Supplier<Long> sizeSupplier,
      Supplier<InputStream> contentSupplier) {
    Objects.requireNonNull(referenceInformation, "Missing reference information");
    return new DigitalObject() {
      @Override
      public InputStream get() {
        return contentSupplier.get();
      }

      @Override
      public String getReferenceInformation() {
        return referenceInformation;
      }

      @Override
      public long getSize() {
        return sizeSupplier.get();
      }
    };
  }

  /**
   * Create a {@linkplain DigitalObject} from a path to a file.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param path The path to the file that holds the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromPath(String referenceInformation, Path path) {
    return fromFile(referenceInformation, path.toFile());
  }

  /**
   * Create a {@linkplain DigitalObject} from bytes.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param bytes The content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromBytes(String referenceInformation, byte[] bytes) {
    return fromSupplier(referenceInformation, () -> (long)bytes.length, () -> new ByteArrayInputStream(bytes));
  }

  /**
   * Create a {@linkplain DigitalObject} from a string.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param value The string that holds the content of the {@linkplain DigitalObject}
   * @param charset The encoding of the given string value
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromString(String referenceInformation, String value, Charset charset) {
    return fromBytes(referenceInformation, value.getBytes(charset));
  }

  /**
   * Create a {@linkplain DigitalObject} from a classpath resource.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param type The type loaded by a classloader that has access to the resource
   * @param resourceName The name of the resource on the classpath
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromResource(String referenceInformation, Class<?> type, String resourceName) {
    return fromSupplier(referenceInformation, () -> type.getResourceAsStream(resourceName));
  }

  /**
   * Create a {@linkplain DigitalObject} from a supplier.
   * <dl><dt>Warning:</dt><dd>The size of the content will be calculated by going through the stream and counting the
   * bytes. This may negatively impact performance.</dd></dl>
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param supplier The supplier of the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromSupplier(String referenceInformation, Supplier<InputStream> supplier) {
    return fromSupplier(referenceInformation, () -> getSize(supplier), supplier);
  }

  /**
   * Calculate the size of an input stream by counting the number of bytes in it. This will be slow if the stream is
   * large.
   * @param supplier The supplier of the content of the {@linkplain DigitalObject}
   * @return the size of the stream, or -1 in case of an I/O error.
   * @since 8.5.0
   */
  static long getSize(Supplier<InputStream> supplier) {
    try (BufferedInputStream input = new BufferedInputStream(supplier.get())) {
      long result = 0;
      while (input.read() != -1) {
        result++;
      }
      return result;
    } catch (IOException e) {
      return -1;
    }
  }

}
