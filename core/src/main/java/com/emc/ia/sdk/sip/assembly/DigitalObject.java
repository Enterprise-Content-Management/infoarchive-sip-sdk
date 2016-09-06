/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

import com.emc.ia.sdk.support.io.RuntimeIoException;

/**
 * Digital Object (an object composed of a set of bit sequences) that is the original target of
 * <a href="http://public.ccsds.org/publications/archive/650x0m2.pdf">Long Term Preservation</a> in an Archive.
 * The object is identified by {@linkplain #getReferenceInformation() Reference Information} and its bits are accessed
 * through an {@linkplain #get() InputStream}.
 */
public interface DigitalObject extends Supplier<InputStream> {

  String UNKNOWN_MIME_TYPE = "application/octet-stream";

  /**
   * A unique identifier for this object that can be used as Reference Information.
   * @return A unique identifier for this object
   */
  String getReferenceInformation();

  /**
   * The type of content stored in this digital object.
   * @return The MIME type of the content
   */
  String getMimeType();

  /**
   * Create a {@linkplain DigitalObject} from a file.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param mimeType The type of content stored in this digital object
   * @param file The file that holds the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromFile(String referenceInformation, String mimeType, File file) {
    return fromSupplier(referenceInformation, mimeType, () -> {
      try {
        return new FileInputStream(file);
      } catch (FileNotFoundException e) {
        throw new RuntimeIoException(e);
      }
    });
  }

  /**
   * Create a {@linkplain DigitalObject} from a file.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param file The file that holds the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromFile(String referenceInformation, File file) {
    return fromFile(referenceInformation, UNKNOWN_MIME_TYPE, file);
  }

  /**
   * Create a {@linkplain DigitalObject} from a supplier.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param mimeType The type of content stored in this digital object
   * @param supplier The supplier of the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromSupplier(String referenceInformation, String mimeType, Supplier<InputStream> supplier) {
    Objects.requireNonNull(referenceInformation, "Missing reference information");
    return new DigitalObject() {

      @Override
      public InputStream get() {
        return supplier.get();
      }

      @Override
      public String getReferenceInformation() {
        return referenceInformation;
      }

      @Override
      public String getMimeType() {
        return mimeType;
      }
    };
  }

  /**
   * Create a {@linkplain DigitalObject} from a supplier.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param supplier The supplier of the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromSupplier(String referenceInformation, Supplier<InputStream> supplier) {
    return fromSupplier(referenceInformation, UNKNOWN_MIME_TYPE, supplier);
  }

  /**
   * Create a {@linkplain DigitalObject} from a path to a file.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param mimeType The type of content stored in this digital object
   * @param path The path to the file that holds the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromPath(String referenceInformation, String mimeType, Path path) {
    return fromFile(referenceInformation, mimeType, path.toFile());
  }

  /**
   * Create a {@linkplain DigitalObject} from a path to a file.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param path The path to the file that holds the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromPath(String referenceInformation, Path path) {
    return fromPath(referenceInformation, UNKNOWN_MIME_TYPE, path);
  }

  /**
   * Create a {@linkplain DigitalObject} from bytes.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param mimeType The type of content stored in this digital object
   * @param bytes The content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromBytes(String referenceInformation, String mimeType, byte[] bytes) {
    return fromSupplier(referenceInformation, () -> new ByteArrayInputStream(bytes));
  }

  /**
   * Create a {@linkplain DigitalObject} from bytes.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param bytes The content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromBytes(String referenceInformation, byte[] bytes) {
    return fromBytes(referenceInformation, UNKNOWN_MIME_TYPE, bytes);
  }

  /**
   * Create a {@linkplain DigitalObject} from a string.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param mimeType The type of content stored in this digital object
   * @param value The string that holds the content of the {@linkplain DigitalObject}
   * @param charset The encoding of the given string value
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromString(String referenceInformation, String mimeType, String value, Charset charset) {
    return fromBytes(referenceInformation, mimeType, value.getBytes(charset));
  }

  /**
   * Create a {@linkplain DigitalObject} from a string.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param value The string that holds the content of the {@linkplain DigitalObject}
   * @param charset The encoding of the given string value
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromString(String referenceInformation, String value, Charset charset) {
    return fromString(referenceInformation, UNKNOWN_MIME_TYPE, value, charset);
  }

  /**
   * Create a {@linkplain DigitalObject} from a classpath resource.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param mimeType The type of content stored in this digital object
   * @param type The type loaded by a classloader that has access to the resource
   * @param resourceName The name of the resource on the classpath
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromResource(String referenceInformation, String mimeType, Class<?> type, String resourceName) {
    return fromSupplier(referenceInformation, mimeType, () -> type.getResourceAsStream(resourceName));
  }

  /**
   * Create a {@linkplain DigitalObject} from a classpath resource.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param type The type loaded by a classloader that has access to the resource
   * @param resourceName The name of the resource on the classpath
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromResource(String referenceInformation, Class<?> type, String resourceName) {
    return fromResource(referenceInformation, UNKNOWN_MIME_TYPE, type, resourceName);
  }

}
