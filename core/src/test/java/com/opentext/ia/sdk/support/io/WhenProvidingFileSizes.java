/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;


public class WhenProvidingFileSizes extends TestCase {

  private static final long KILO = 1024;

  @Test
  public void shouldUseHumanReadableNames() {
    int bytes = random();
    int kilo = random();
    int mega = random();
    int giga = random();
    int tera = random();

    assertEquals(bytes, FileSize.of(bytes).bytes(), "bytes");
    assertEquals(kilo * KILO, FileSize.of(kilo).kiloBytes(), "kilo");
    assertEquals(mega * KILO * KILO, FileSize.of(mega).megaBytes(), "mega");
    assertEquals(giga * KILO * KILO * KILO, FileSize.of(giga).gigaBytes(), "giga");
    assertEquals(tera * KILO * KILO * KILO * KILO, FileSize.of(tera).teraBytes(), "tera");
  }

  private int random() {
    return randomInt(1, (int)KILO - 1);
  }

}
