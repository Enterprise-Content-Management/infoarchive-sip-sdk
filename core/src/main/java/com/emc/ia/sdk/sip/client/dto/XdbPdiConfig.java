/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import java.util.ArrayList;
import java.util.List;

public class XdbPdiConfig {

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
