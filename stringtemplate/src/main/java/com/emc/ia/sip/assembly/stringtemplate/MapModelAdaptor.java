/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sip.assembly.stringtemplate;

import java.util.Map;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;


class MapModelAdaptor implements ModelAdaptor {

  @Override
  @SuppressWarnings("rawtypes")
  public Object getProperty(Interpreter interpreter, ST template, Object model, Object property, String propertyName)
      throws STNoSuchPropertyException {

    return ((Map)model).get(propertyName);
  }

}
