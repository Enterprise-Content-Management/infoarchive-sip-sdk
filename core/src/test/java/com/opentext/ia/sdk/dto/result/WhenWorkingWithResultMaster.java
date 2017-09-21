/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.result;

import static org.junit.Assert.*;

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
    assertEquals(1, mainPanel.getTabs().size());
    assertEquals(master.getDefaultTab(), mainPanel.getTabs().get(0));

    Tab defaultTab = master.getDefaultTab();
    assertNotNull(defaultTab);
    assertEquals("_ia_Default_Main_tab_", defaultTab.getName());
    assertTrue(defaultTab.getColumns().isEmpty());

    Panel sidePanel = master.getPanelByName("Side Panel");
    assertEquals("Side Panel", sidePanel.getName());
    assertTrue(sidePanel.getTabs().isEmpty());

    Panel inlinePanel = master.getPanelByName("Inline Panel");
    assertEquals("Inline Panel", inlinePanel.getName());
    assertTrue(inlinePanel.getTabs().isEmpty());
  }

}
