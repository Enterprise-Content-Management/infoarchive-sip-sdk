/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhenWorkingWithSearch {
  private final Search search = new Search();
  private final SearchComposition composition = new SearchComposition();

  @Test
  public void defaultDecriptionShouldNotBeNull() {
    assertTrue(search.getDescription().equals("Default emails search"));
  }

  @Test
  public void defaultStateShouldNotBeNull() {
    assertTrue(search.getState().equals("DRAFT"));
  }

  @Test
  public void defaultNestedSearchShouldNotBeNull() {
    assertFalse(search.isNestedSearch());
  }

  @Test
  public void defaultInUseShouldNotBeNull() {
    assertFalse(search.isInUse());
  }

  @Test
  public void setDecription() {
    search.setDescription("Test Search");
    assertTrue(search.getDescription().equals("Test Search"));
  }

  @Test
  public void setState() {
    search.setState("TestState");
    assertTrue(search.getState().equals("TestState"));
  }

  @Test
  public void setNestedSearch() {
    search.setNestedSearch(true);
    assertTrue(search.isNestedSearch());
  }

  @Test
  public void setInUse() {
    search.setInUse(true);
    assertTrue(search.isInUse());
  }

  @Test
  public void setAic() {
    search.setAic("http://emailAic");
    assertEquals(search.getAic(), "http://emailAic");
  }

  @Test
  public void setQuery() {
    search.setQuery("http://emailQuery");
    assertEquals(search.getQuery(), "http://emailQuery");
  }

@Test
  public void searchesObjectCreationShouldBeSuccessful() {
    assertNotNull(new Searches());
  }

  @Test
  public void defaultSearchCompoistionNameShouldNotBeNull() {
    assertTrue(composition.getSearchName().equals("Deafult emails Search Compoistion"));
  }

  @Test
  public void setSearchCompositionName() {
    composition.setName("Test");
    assertEquals(composition.getName(), "Test");
  }

  @Test
  public void searchCompositionsObjectCreationShouldBeSuccessful() {
    assertNotNull(new SearchCompositions());
  }

}
