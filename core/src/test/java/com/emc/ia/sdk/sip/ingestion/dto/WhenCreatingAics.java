/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class WhenCreatingAics {

  @Test
  public void shouldCreateAicsObject() {
    assertNotNull(new Aics());
  }

  @Test
  public void shouldGetDefaultCriteriaFromAic() {
    Aic aic = new Aic();
    assertNotNull(aic.getCriterias());
    assertEquals(aic.getCriterias().get(0).getName(), "To");
    assertEquals(aic.getCriterias().get(0).getLabel(), "To");
    assertEquals(aic.getCriterias().get(0).getType(), "STRING");
    assertNull(aic.getCriterias().get(0).getpKeyMinAttr());
    assertNull(aic.getCriterias().get(0).getpKeyMaxAttr());
    assertEquals(aic.getCriterias().get(0).getpKeyValuesAttr(), "pkeys.values02");
    assertTrue(aic.getCriterias().get(0).isIndexed());
  }

  @Test
  public void shouldSetCriteriaInAic() {
    String label = "Test";
    Aic aic = new Aic();
    List<Criteria> criterias = new ArrayList<>();
    Criteria test = new Criteria();
    test.setName(label);
    test.setLabel(label);
    test.setType("STRING");
    test.setpKeyMinAttr("pKeyTest01");
    test.setpKeyMaxAttr("pKeyTest02");
    test.setpKeyValuesAttr("pkeys.values02");
    test.setIndexed(true);
    criterias.add(test);
    aic.setCriterias(criterias);
    assertNotNull(aic.getCriterias());
    assertEquals(aic.getCriterias().get(0).getName(), label);
    assertEquals(aic.getCriterias().get(0).getLabel(), label);
    assertEquals(aic.getCriterias().get(0).getType(), "STRING");
    assertEquals(aic.getCriterias().get(0).getpKeyMinAttr(), "pKeyTest01");
    assertEquals(aic.getCriterias().get(0).getpKeyMaxAttr(), "pKeyTest02");
    assertEquals(aic.getCriterias().get(0).getpKeyValuesAttr(), "pkeys.values02");
    assertTrue(aic.getCriterias().get(0).isIndexed());
  }
}
