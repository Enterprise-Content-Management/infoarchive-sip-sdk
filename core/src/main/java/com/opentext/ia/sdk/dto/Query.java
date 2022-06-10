/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;


public class Query extends NamedLinkContainer {

  private String resultSchema;
  private String resultRootElement = "result";
  private boolean resultRootNsEnabled;
  private List<Namespace> namespaces = new ArrayList<>();
  private String quotaAsync;
  private String quota;
  private List<String> aics = new ArrayList<>();

  public String getQuotaAsync() {
    return quotaAsync;
  }

  public void setQuotaAsync(String quotaAsync) {
    this.quotaAsync = quotaAsync;
  }

  public String getQuota() {
    return quota;
  }

  public void setQuota(String quota) {
    this.quota = quota;
  }

  public List<String> getAics() {
    return aics;
  }

  public void setAics(List<String> aics) {
    this.aics = aics;
  }

  public String getResultSchema() {
    return resultSchema;
  }

  public void setResultSchema(String resultSchema) {
    this.resultSchema = resultSchema;
  }

  public String getResultRootElement() {
    return resultRootElement;
  }

  public void setResultRootElement(String resultRootElement) {
    this.resultRootElement = resultRootElement;
  }

  public boolean isResultRootNsEnabled() {
    return resultRootNsEnabled;
  }

  public final void setResultRootNsEnabled(boolean resultRootNsEnabled) {
    this.resultRootNsEnabled = resultRootNsEnabled;
  }

  public List<Namespace> getNamespaces() {
    return namespaces;
  }

  public void setNamespaces(List<Namespace> namespaces) {
    this.namespaces = namespaces;
  }

}
