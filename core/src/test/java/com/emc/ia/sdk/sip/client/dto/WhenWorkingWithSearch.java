/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.emc.ia.sdk.sip.client.rest.SearchOptions;

public class WhenWorkingWithSearch {

  private final Search search = new Search();
  private final SearchComposition composition = new SearchComposition();
  private final SearchDataBuilder builder = SearchDataBuilder.builder();

  private static final String TEST_SUBJECT = "subject";
  private static final String TEST_VALUE = "Test";

  @Test
  public void defaultDecriptionShouldBeNull() {
    assertNull(search.getDescription());
  }

  @Test
  public void defaultStateShouldNotBeNull() {
    assertTrue(search.getState()
      .equals("DRAFT"));
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
    assertTrue(search.getDescription()
      .equals("Test Search"));
  }

  @Test
  public void setState() {
    search.setState("TestState");
    assertTrue(search.getState()
      .equals("TestState"));
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
  public void defaultSearchCompositionNameShouldBeSet1() {
    assertEquals("Set 1", composition.getName());
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

  @Test
  public void searchDataBuilderObjectCreationSuccessful() {
    assertNotNull(builder);
  }

  @Test
  public void searchDataBuilderCriteriaEqualSuccessful() {
    assertNotNull(builder.equal(TEST_SUBJECT, TEST_VALUE));
  }

  @Test
  public void searchDataBuilderCriteriaNotEqualSuccessful() {
    assertNotNull(builder.notEqual(TEST_SUBJECT, TEST_VALUE));
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

  @Test
  public void searchOptionsObjectCreationSuccessful() {
    assertNotNull(new SearchOptions());
  }

  @Test
  public void searchOptionsDefaultPageSizeIsValid() {
    assertEquals(new SearchOptions().getPagesize(), 20);
  }

  @Test
  public void searchOptionsDefaultNameIsValid() {
    assertEquals(new SearchOptions().getSearchSetName(), "Email");
  }

  @Test
  public void searchOptionsDefaultTimeOutIsValid() {
    assertEquals(new SearchOptions().getTimeoutInSec(), 120);
  }

  @Test
  public void searchOptionsDefaultIsNotNull() {
    assertNotNull(new SearchOptions().getDefault());
  }
}
