/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto.result;

import java.util.ArrayList;
import java.util.List;

import com.emc.ia.sdk.sip.ingestion.dto.Namespace;

public class ResultMaster {

  private List<Panel> panels;

  private List<Namespace> namespaces;

  public ResultMaster() {
    panels = new ArrayList<>();
    namespaces = new ArrayList<>();
    panels.add(new Panel("Main Panel", new Tab("_ia_Default_Main_tab_")));
    panels.add(new Panel("Side Panel"));
    panels.add(new Panel("Inline Panel"));
  }

  public Tab getDefaultTab() {
    return getPanelByName("Main Panel").getTabByName("_ia_Default_Main_tab_");
  }

  public Panel getPanelByName(String panelName) {
    return panels.stream()
      .filter(p -> panelName.equals(p.getName()))
      .findFirst()
      .orElse(null);
  }

  public List<Panel> getPanels() {
    return panels;
  }

  public void setPanels(List<Panel> panels) {
    this.panels = panels;
  }

  public List<Namespace> getNamespaces() {
    return namespaces;
  }

  public void setNamespaces(List<Namespace> namespaces) {
    this.namespaces = namespaces;
  }
}
