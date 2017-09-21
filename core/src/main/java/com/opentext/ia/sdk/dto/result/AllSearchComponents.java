/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.result;

import com.opentext.ia.sdk.dto.SearchComposition;
import com.opentext.ia.sdk.dto.XForm;
import com.opentext.ia.sdk.support.JavaBean;


public class AllSearchComponents extends JavaBean {

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
