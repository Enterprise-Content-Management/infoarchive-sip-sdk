/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.IOException;

/**
 * Unchecked version of {@linkplain IOException}.
 */
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
