/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class WhenWorkingWithSearch {

  private static final String TEST_SUBJECT = "subject";
  private static final String TEST_VALUE = "Test";

  private final Search search = new Search();
  private final SearchComposition composition = new SearchComposition();
  private final SearchDataBuilder builder = SearchDataBuilder.builder();

  @Test
  public void defaultDecriptionShouldBeNull() {
    assertNull(search.getDescription());
  }

  @Test
  public void defaultStateShouldNotBeNull() {
    assertEquals("DRAFT", search.getState());
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
    String description = "Test Search";
    search.setDescription(description);
    assertEquals(description, search.getDescription());
  }

  @Test
  public void setState() {
    String state = "TestState";
    search.setState(state);
    assertEquals(state, search.getState());
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
    String aic = "http://emailAic";
    search.setAic(aic);
    assertEquals(aic, search.getAic());
  }

  @Test
  public void setQuery() {
    String query = "http://emailQuery";
    search.setQuery(query);
    assertEquals(query, search.getQuery());
  }

  @Test
  public void searchesObjectCreationShouldBeSuccessful() {
    assertNotNull(new Searches());
  }

  @Test
  public void defaultSearchCompositionNameShouldBeSet1() {
    assertEquals("Set 1", composition.getName());
  }

  @Test
  public void setSearchCompositionName() {
    String name = "Test";
    composition.setName(name);
    assertEquals(name, composition.getName());
  }

  @Test
  public void searchCompositionsObjectCreationShouldBeSuccessful() {
    assertNotNull(new SearchCompositions());
  }

  @Test
  public void searchDataBuilderObjectCreationSuccessful() {
    assertNotNull(builder);
  }

  @Test
  public void searchDataBuilderCriteriaEqualSuccessful() {
    assertNotNull(builder.isEqual(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  public void searchDataBuilderCriteriaNotEqualSuccessful() {
    assertNotNull(builder.isNotEqual(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  public void searchDataBuilderCriteriaStartsWithSuccessful() {
    assertNotNull(builder.startsWith(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  public void searchDataBuilderCriteriaEndsWithSuccessful() {
    assertNotNull(builder.endsWith(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  public void searchDataBuilderCriteriaContainsWithSuccessful() {
    assertNotNull(builder.contains(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  public void searchDataBuilderCriteriaBuildSuccessful() {
    assertNotNull(builder.build());
  }

  @Test
  public void searchResultsCreationIsSuccessful() {
    assertNotNull(new SearchResults());
  }

  @Test
  public void searchResultsDefaultRowsNotNull() {
    assertNotNull(new SearchResults().getRows());
  }

}
