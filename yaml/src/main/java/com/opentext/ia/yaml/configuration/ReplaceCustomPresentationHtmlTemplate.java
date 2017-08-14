/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;


public class ReplaceCustomPresentationHtmlTemplate extends PathVisitor {

  private static final String HTML_TEMPLATE = "htmlTemplate";
  private static final String TEXT = "text";

  public ReplaceCustomPresentationHtmlTemplate() {
    super(Arrays.asList("/customPresentationConfigurations/\\d+"));
  }

  @Override
  public boolean test(Visit visit) {
    return super.test(visit) && visit.getMap().get(HTML_TEMPLATE, TEXT).isScalar();
  }

  @Override
  public void accept(Visit visit) {
    visit.getMap().put(HTML_TEMPLATE, visit.getMap().get(HTML_TEMPLATE, TEXT));
  }

}
