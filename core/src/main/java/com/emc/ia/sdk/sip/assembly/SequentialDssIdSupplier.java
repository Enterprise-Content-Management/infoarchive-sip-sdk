/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

/**
 * Generate Data Submission Session (DSS) IDs from a prefix and an increasing counter.
 */
public class SequentialDssIdSupplier extends PrefixDssIdSupplier {

  private long counter;

  /**
   * Create an instance that starts at 1.
   * @param prefix The prefix for all generated DSS IDs
   */
  public SequentialDssIdSupplier(String prefix) {
    this(prefix, 1);
  }

  /**
   * Create an instance.
   * @param prefix The prefix for all generated DSS IDs
   * @param counter Where to start counting
   */
  public SequentialDssIdSupplier(String prefix, long counter) {
    super(prefix);
    this.counter = counter;
  }

  @Override
  protected String postfix() {
    return Long.toString(counter++);
  }

}
