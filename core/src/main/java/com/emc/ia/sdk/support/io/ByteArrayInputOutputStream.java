/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * {@linkplain OutputStream} that stores its data in memory and allows it to be {@linkplain #getInputStream() read} back.
 */
public class ByteArrayInputOutputStream extends ByteArrayOutputStream {

  /**
   * @return The stored data as an {@linkplain InputStream}
   */
  public InputStream getInputStream() {
    return new ByteArrayInputStream(buf, 0, count);
  }

}
