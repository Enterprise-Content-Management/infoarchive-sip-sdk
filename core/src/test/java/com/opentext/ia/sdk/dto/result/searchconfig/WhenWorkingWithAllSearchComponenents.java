/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.result.searchconfig;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.dto.SearchComposition;
import com.opentext.ia.sdk.dto.XForm;
import com.opentext.ia.sdk.dto.result.AllSearchComponents;
import com.opentext.ia.sdk.dto.result.ResultMaster;


class WhenWorkingWithAllSearchComponenents {

  @Test
  void shouldHaveNoDefaults() {
    AllSearchComponents components = new AllSearchComponents();
    assertNull(components.getResultMaster());
    assertNull(components.getSearchComposition());
    assertNull(components.getXform());
  }

  @Test
  void shouldUpdateStateWhenSettersAreCalled() {
    AllSearchComponents components = new AllSearchComponents();
    ResultMaster resultMaster = new ResultMaster();
    SearchComposition searchComposition = new SearchComposition();
    XForm xform = new XForm();
    components.setResultMaster(resultMaster);
    components.setSearchComposition(searchComposition);
    components.setXform(xform);
    assertEquals(searchComposition, components.getSearchComposition());
    assertEquals(resultMaster, components.getResultMaster());
    assertEquals(xform, components.getXform());
  }

}
