/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Unchecked version of {@linkplain IOException}.
 */
@SuppressFBWarnings(value = "IMC_IMMATURE_CLASS_BAD_SERIALVERSIONUID",
    justification = "backward compatiblity")
public class RuntimeIoException extends RuntimeException {

  private static final long serialVersionUID = -6265008221590623835L;

  public RuntimeIoException(IOException cause) {
    super(cause);
  }

  /**
   * Return the cause of this exception.
   * @return The cause of this exception
   */
  @Override
  public IOException getCause() {
    return (IOException)super.getCause();
  }

}
