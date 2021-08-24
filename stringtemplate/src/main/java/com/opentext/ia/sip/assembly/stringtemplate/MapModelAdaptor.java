/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sip.assembly.stringtemplate;

import java.util.Map;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;


@SuppressWarnings("rawtypes")
class MapModelAdaptor implements ModelAdaptor<Map> {

  @Override
  public Object getProperty(Interpreter interpreter, ST template, Map model, Object property,
      String propertyName) {
    return model.get(propertyName);
  }

}
