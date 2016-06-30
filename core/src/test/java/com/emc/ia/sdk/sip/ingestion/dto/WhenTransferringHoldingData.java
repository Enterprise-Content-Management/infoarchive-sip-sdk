/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenTransferringHoldingData extends TestCase {

  @Test
  public void shouldSetAllStoresWithOneAssignment() {
    String store = randomString();
    Holding holding = new Holding();

    holding.setAllStores(store);

    assertEquals("Ci", store, holding.getCiStore());
    assertEquals("Ci", store, holding.getLogStore());
    assertEquals("Ci", store, holding.getRenditionStore());
    assertEquals("Ci", store, holding.getXdbStore());
    assertEquals("Ci", store, holding.getXmlStore());
  }

}
