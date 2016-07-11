/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhenWorkingWithSearch {
  private final Search search = new Search();
  private final SearchComposition composition = new SearchComposition();
  private final SearchCompositions compositions = new SearchCompositions();

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
  public void searchesObjectCreationShouldBeSuccessful() {
    assertNotNull(new Searches());
  }

@Test
public void setSearchesPageObject() {
  Searches searches = new Searches();
  Page page = new Page();
  page.setNumber(1);
  page.setSize(10);
  page.setTotalElements(15);
  page.setTotalPages(5);
  searches.setPage(page);
  assertEquals(searches.getPage().getNumber(), 1);
  assertEquals(searches.getPage().getSize(), 10);
  assertEquals(searches.getPage().getTotalElements(), 15);
  assertEquals(searches.getPage().getTotalPages(), 5);
}

  @Test
  public void defaultSearchCompoistionNameShouldNotBeNull() {
    assertTrue(composition.getSearchName().equals("Deafult emails Search Compoistion"));
  }

  @Test
  public void defaultSearchCompoistionsPageObjectShouldNotBeNull() {
    assertNotNull(compositions.getPage());
  }

  @Test
  public void setSearchCompositionsPageObject() {
    Page page = new Page();
    page.setNumber(1);
    page.setSize(10);
    page.setTotalElements(15);
    page.setTotalPages(5);
    compositions.setPage(page);
    assertEquals(compositions.getPage().getNumber(), 1);
    assertEquals(compositions.getPage().getSize(), 10);
    assertEquals(compositions.getPage().getTotalElements(), 15);
    assertEquals(compositions.getPage().getTotalPages(), 5);
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
