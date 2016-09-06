/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class PrefixDssIdSupplier implements Supplier<String> {

  private final String prefix;

  /**
   * Create an instance.
   * @param prefix The prefix for all generated DSS IDs
   */
  public PrefixDssIdSupplier(String prefix) {
    this.prefix = Objects.requireNonNull(prefix);
  }

  @Override
  public final String get() {
    return prefix + postfix();
  }

  protected abstract String postfix();

}
