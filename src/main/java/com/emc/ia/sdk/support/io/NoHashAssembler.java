/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import java.util.Collection;
import java.util.Collections;


/**
 * Function that computes no {@linkplain EncodedHash} at all.
 * (<a href="http://c2.com/cgi/wiki?NullObject">Null Object</a>)
 */
public class NoHashAssembler implements HashAssembler {

  private long size;

  @Override
  public void initialize() {
    size = 0;
  }

  @Override
  public void add(byte[] buffer, int length) {
    size += length;
  }

  @Override
  public long numBytesHashed() {
    return size;
  }

  @Override
  public Collection<EncodedHash> get() {
    return Collections.emptyList();
  }

}
