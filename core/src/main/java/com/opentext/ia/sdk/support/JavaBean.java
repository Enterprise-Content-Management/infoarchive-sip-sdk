/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;


public class JavaBean {

  @Override
  public String toString() {
    try {
      Map<String, String> properties = BeanUtils.describe(this);
      properties.remove("class");
      return properties.toString();
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      return super.toString();
    }
  }

}
