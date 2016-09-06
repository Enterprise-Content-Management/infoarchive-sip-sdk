/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto.result.searchconfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.emc.ia.sdk.sip.client.dto.SearchComposition;
import com.emc.ia.sdk.sip.client.dto.XForm;
import com.emc.ia.sdk.sip.client.dto.result.ResultMaster;

public class WhenWorkingWithAllSearchComponenents {

  @Test
  public void shouldHaveNoDefaults() {
    AllSearchComponents components = new AllSearchComponents();
    assertNull(components.getResultMaster());
    assertNull(components.getSearchComposition());
    assertNull(components.getXform());
  }

  @Test
  public void shouldUpdateStateWhenSettersAreCalled() {
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
