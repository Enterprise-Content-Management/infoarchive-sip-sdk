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
import java.util.function.Supplier;

import com.emc.ia.sdk.support.io.RuntimeIoException;


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
   * Create a {@linkplain DigitalObject} from a file.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param file The file that holds the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromFile(String referenceInformation, File file) {
    return fromSupplier(referenceInformation, () -> {
      try {
        return new FileInputStream(file);
      } catch (FileNotFoundException e) {
        throw new RuntimeIoException(e);
      }
    });
  }

  /**
   * Create a {@linkplain DigitalObject} from a supplier.
   * @param referenceInformation The unique identifier to use as Reference Information
   * @param supplier The supplier of the content of the {@linkplain DigitalObject}
   * @return The newly created {@linkplain DigitalObject}
   */
  static DigitalObject fromSupplier(String referenceInformation, Supplier<InputStream> supplier) {
    return new DigitalObject() {
      @Override
      public InputStream get() {
        return supplier.get();
      }

      @Override
      public String getReferenceInformation() {
        return referenceInformation;
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
    return fromSupplier(referenceInformation, () -> new ByteArrayInputStream(bytes));
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



  static DigitalObject fromResource(String referenceInformation, Class<?> type, String resourceName) {
    return fromSupplier(referenceInformation, () -> type.getResourceAsStream(resourceName));
  }
}
