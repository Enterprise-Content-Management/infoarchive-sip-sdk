/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class WhenWorkingWithResultMaster {

  @Test
  public void shouldHaveDefaul3Panels() {
    ResultMaster master = new ResultMaster();
    List<Panel> panels = master.getPanels();
    assertEquals(3, panels.size());

    Panel mainPanel = master.getPanelByName("Main Panel");
    assertEquals("Main Panel", mainPanel.getName());
    assertEquals(1, mainPanel.getTabs()
      .size());
    assertEquals(master.getDefaultTab(), mainPanel.getTabs()
      .get(0));

    Tab defaultTab = master.getDefaultTab();
    assertNotNull(defaultTab);
    assertEquals("_ia_Default_Main_tab_", defaultTab.getName());
    assertTrue(defaultTab.getColumns()
      .isEmpty());

    Panel sidePanel = master.getPanelByName("Side Panel");
    assertEquals("Side Panel", sidePanel.getName());
    assertTrue(sidePanel.getTabs()
      .isEmpty());

    Panel inlinePanel = master.getPanelByName("Inline Panel");
    assertEquals("Inline Panel", inlinePanel.getName());
    assertTrue(inlinePanel.getTabs()
      .isEmpty());

  }
}
