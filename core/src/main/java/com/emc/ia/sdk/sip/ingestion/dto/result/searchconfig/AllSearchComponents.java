/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto.result.searchconfig;

import com.emc.ia.sdk.sip.ingestion.dto.SearchComposition;
import com.emc.ia.sdk.sip.ingestion.dto.XForm;
import com.emc.ia.sdk.sip.ingestion.dto.result.ResultMaster;

public class AllSearchComponents {

  private SearchComposition searchComposition;
  private ResultMaster resultMaster;
  private XForm xform;

  public SearchComposition getSearchComposition() {
    return searchComposition;
  }

  public void setSearchComposition(SearchComposition searchComposition) {
    this.searchComposition = searchComposition;
  }

  public ResultMaster getResultMaster() {
    return resultMaster;
  }

  public void setResultMaster(ResultMaster resultMaster) {
    this.resultMaster = resultMaster;
  }

  public XForm getXform() {
    return xform;
  }

  public void setXform(XForm xform) {
    this.xform = xform;
  }

}
