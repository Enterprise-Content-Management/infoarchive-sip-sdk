/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


class WhenWorkingWithSearch {

  private static final String TEST_SUBJECT = "subject";
  private static final String TEST_VALUE = "Test";

  private final Search search = new Search();
  private final SearchComposition composition = new SearchComposition();
  private final SearchDataBuilder builder = SearchDataBuilder.builder();

  @Test
  void defaultDecriptionShouldBeNull() {
    assertNull(search.getDescription());
  }

  @Test
  void defaultStateShouldNotBeNull() {
    assertEquals("DRAFT", search.getState());
  }

  @Test
  void defaultNestedSearchShouldNotBeNull() {
    assertFalse(search.isNestedSearch());
  }

  @Test
  void defaultInUseShouldNotBeNull() {
    assertFalse(search.isInUse());
  }

  @Test
  void setDecription() {
    String description = "Test Search";
    search.setDescription(description);
    assertEquals(description, search.getDescription());
  }

  @Test
  void setState() {
    String state = "TestState";
    search.setState(state);
    assertEquals(state, search.getState());
  }

  @Test
  void setNestedSearch() {
    search.setNestedSearch(true);
    assertTrue(search.isNestedSearch());
  }

  @Test
  void setInUse() {
    search.setInUse(true);
    assertTrue(search.isInUse());
  }

  @Test
  void setAic() {
    String aic = "http://emailAic";
    search.setAic(aic);
    assertEquals(aic, search.getAic());
  }

  @Test
  void setQuery() {
    String query = "http://emailQuery";
    search.setQuery(query);
    assertEquals(query, search.getQuery());
  }

  @Test
  void searchesObjectCreationShouldBeSuccessful() {
    assertNotNull(new Searches());
  }

  @Test
  void defaultSearchCompositionNameShouldBeSet1() {
    assertEquals("Set 1", composition.getName());
  }

  @Test
  void setSearchCompositionName() {
    String name = "Test";
    composition.setName(name);
    assertEquals(name, composition.getName());
  }

  @Test
  void searchCompositionsObjectCreationShouldBeSuccessful() {
    assertNotNull(new SearchCompositions());
  }

  @Test
  void searchDataBuilderObjectCreationSuccessful() {
    assertNotNull(builder);
  }

  @Test
  void searchDataBuilderCriteriaEqualSuccessful() {
    assertNotNull(builder.isEqual(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  void searchDataBuilderCriteriaNotEqualSuccessful() {
    assertNotNull(builder.isNotEqual(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  void searchDataBuilderCriteriaStartsWithSuccessful() {
    assertNotNull(builder.startsWith(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  void searchDataBuilderCriteriaEndsWithSuccessful() {
    assertNotNull(builder.endsWith(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  void searchDataBuilderCriteriaContainsWithSuccessful() {
    assertNotNull(builder.contains(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  void searchDataBuilderCriteriaBuildSuccessful() {
    assertNotNull(builder.build());
  }

  @Test
  void searchResultsCreationIsSuccessful() {
    assertNotNull(new SearchResults());
  }

  @Test
  void searchResultsDefaultRowsNotNull() {
    assertNotNull(new SearchResults().getRows());
  }

}
