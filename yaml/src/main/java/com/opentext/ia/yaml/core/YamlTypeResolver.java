/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.util.regex.Pattern;

import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;


class YamlTypeResolver extends Resolver {

  private static final Pattern OFFSET_DATE_TIME = Pattern.compile(
      "[0-9]{4}-[0-9]{2}-[0-9]{2}T([0-9]{2}:){2}[0-9]{2}(\\.[0-9]{1,3})?(([+|-][0-9]{2}:[0-9]{2})|Z)");

  @Override
  protected void addImplicitResolvers() {
    addImplicitResolver(Tag.STR, OFFSET_DATE_TIME, "0123456789");
    super.addImplicitResolvers();
  }

}
