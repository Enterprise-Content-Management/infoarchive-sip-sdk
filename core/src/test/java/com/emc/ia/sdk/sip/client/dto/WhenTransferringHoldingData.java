/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;

public class WhenTransferringHoldingData extends TestCase {

  @Test
  public void shouldSetAllStoresWithOneAssignment() {
    String store = randomString();
    Holding holding = new Holding();

    holding.setAllStores(store);

    assertEquals("Ci Store", store, holding.getCiStore());
    assertEquals("Log Store", store, holding.getLogStore());
    assertEquals("Rendition Store", store, holding.getRenditionStore());
    assertEquals("XDB Store", store, holding.getXdbStore());
    assertEquals("XML Store", store, holding.getXmlStore());
    assertEquals("Managed Item Store", store, holding.getManagedItemStore());
  }

}
