/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;

import com.opentext.ia.sdk.support.JavaBean;


public class XdbPdiConfig extends JavaBean {

  private List<Operand> operands;
  private String schema;
  private String entityPath;
  private String template;

  public XdbPdiConfig() {
    setTemplate("return $aiu");
    setOperands(new ArrayList<>());
  }

  public List<Operand> getOperands() {
    return operands;
  }

  public final void setOperands(List<Operand> operands) {
    this.operands = operands;
  }

  public String getSchema() {
    return schema;
  }

  public final void setSchema(String schema) {
    this.schema = schema;
  }

  public String getEntityPath() {
    return entityPath;
  }

  public final void setEntityPath(String entityPath) {
    this.entityPath = entityPath;
  }

  public String getTemplate() {
    return template;
  }

  public final void setTemplate(String template) {
    this.template = template;
  }

}
