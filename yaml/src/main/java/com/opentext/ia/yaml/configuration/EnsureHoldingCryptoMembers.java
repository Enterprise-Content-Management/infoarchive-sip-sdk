/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collections;
import java.util.stream.Stream;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class EnsureHoldingCryptoMembers extends PathVisitor {

  EnsureHoldingCryptoMembers() {
    super(Collections.singleton("/holdingCryptoes/\\d+"));
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    Stream.of("ci", "pdi", "sip").forEach(member -> ensureMember(yaml, member));
  }

  private void ensureMember(YamlMap yaml, String member) {
    if (!yaml.containsKey(member)) {
      yaml.put(member, new YamlMap());
    }
  }

}
