/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class WhenCreatingQueries {
  @Test
  public void shouldCreateQueriesObject() {
    assertNotNull(new Queries());
  }

  @Test
  public void shouldGetDefaultNameSpaceFromQuery() {
    Query query = new Query();
    assertNotNull(query.getNameSpaces());
    assertEquals(query.getNameSpaces().get(0).getUri(), "urn:emc:demo:email.xsd.1.0");
    assertEquals(query.getNameSpaces().get(0).getPrefix(), "n");
  }

  @Test
  public void shouldSetNameSpaceInQuery() {
    Query query = new Query();
    List<NameSpace> nsl = new ArrayList<>();
    NameSpace test = new NameSpace();
    test.setUri("urn:test:email.xsd.1.0");
    test.setPrefix("n");
    nsl.add(test);
    query.setNameSpaces(nsl);
    assertNotNull(query.getNameSpaces());
  }

}
