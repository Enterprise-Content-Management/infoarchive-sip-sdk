/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collections;

import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class InsertDefaultParentForFileSystemFolder extends BaseInsertDefaultReferences {

  InsertDefaultParentForFileSystemFolder() {
    super(Collections.singletonMap("/fileSystemFolders/\\d+", Collections.singletonList("parentSpaceRootFolder")));
  }

  @Override
  protected boolean missesProperty(Visit visit, String property) {
    YamlMap map = visit.getMap();
    return !map.containsKey(property) && !map.containsKey("parentFileSystemFolder");
  }

  @Override
  protected String typeOf(String property) {
    return "spaceRootFolder";
  }

}
