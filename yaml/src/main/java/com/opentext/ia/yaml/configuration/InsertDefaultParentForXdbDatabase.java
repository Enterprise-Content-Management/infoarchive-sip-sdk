/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collections;

import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class InsertDefaultParentForXdbDatabase extends BaseInsertDefaultReferences {

  InsertDefaultParentForXdbDatabase() {
    super(Collections.singletonMap("/xdbDatabases/\\d+", Collections.singletonList("xdbFederation")));
  }

  @Override
  protected boolean missesProperty(Visit visit, String property) {
    YamlMap map = visit.getMap();
    return !map.containsKey(property) && !map.containsKey("xdbCluster");
  }

  @Override
  protected String typeOf(String property) {
    return "xdbFederation";
  }

}
