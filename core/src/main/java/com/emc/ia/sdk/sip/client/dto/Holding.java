/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import java.util.ArrayList;
import java.util.List;

public class Holding extends NamedLinkContainer { // NOPMD TooManyFields - Dictated by IA

  private String logStore;
  private boolean keepSipAfterCommitEnabled;
  private String renditionStore;
  private String xdbMode;
  private String ciStore;
  private boolean syncCommitEnabled;
  private boolean ciHashValidationEnabled;
  private int priority;
  private boolean pdiXmlHashValidationEnabled;
  private String xmlStore;
  private List<IngestConfig> ingestConfigs;
  private String defaultRetentionClass;
  private List<String> ingestNodes;
  private String xdbStore;
  private boolean logStoreEnabled;
  private List<SubPriority> subPriorities;
  private String xdbLibraryParent;
  private boolean pdiXmlHashEnforced;
  private String sipStore;
  private List<RetentionClass> retentionClasses;
  private List<PdiConfig> pdiConfigs;

  public Holding() {
    setSyncCommitEnabled(true);
    setCiHashValidationEnabled(true);
    setPdiXmlHashValidationEnabled(true);
    setIngestConfigs(new ArrayList<>());
    setDefaultRetentionClass("default");
    setIngestNodes(new ArrayList<>());
    setSubPriorities(new ArrayList<>());
    setPdiConfigs(new ArrayList<>());
    setRetentionClasses(new ArrayList<>());
    setLogStoreEnabled(true);
    setXdbMode("PRIVATE");
    setPriority(1);
  }

  public String getLogStore() {
    return logStore;
  }

  public final void setLogStore(String logStore) {
    this.logStore = logStore;
  }

  public boolean isKeepSipAfterCommitEnabled() {
    return keepSipAfterCommitEnabled;
  }

  public final void setKeepSipAfterCommitEnabled(boolean keepSipAfterCommitEnabled) {
    this.keepSipAfterCommitEnabled = keepSipAfterCommitEnabled;
  }

  public String getRenditionStore() {
    return renditionStore;
  }

  public final void setRenditionStore(String renditionStore) {
    this.renditionStore = renditionStore;
  }

  public String getXdbMode() {
    return xdbMode;
  }

  public final void setXdbMode(String xdbMode) {
    this.xdbMode = xdbMode;
  }

  public String getCiStore() {
    return ciStore;
  }

  public final void setCiStore(String ciStore) {
    this.ciStore = ciStore;
  }

  public boolean isSyncCommitEnabled() {
    return syncCommitEnabled;
  }

  public final void setSyncCommitEnabled(boolean syncCommitEnabled) {
    this.syncCommitEnabled = syncCommitEnabled;
  }

  public boolean isCiHashValidationEnabled() {
    return ciHashValidationEnabled;
  }

  public final void setCiHashValidationEnabled(boolean ciHashValidationEnabled) {
    this.ciHashValidationEnabled = ciHashValidationEnabled;
  }

  public int getPriority() {
    return priority;
  }

  public final void setPriority(int priority) {
    this.priority = priority;
  }

  public boolean isPdiXmlHashValidationEnabled() {
    return pdiXmlHashValidationEnabled;
  }

  public final void setPdiXmlHashValidationEnabled(boolean pdiXmlHashValidationEnabled) {
    this.pdiXmlHashValidationEnabled = pdiXmlHashValidationEnabled;
  }

  public String getXmlStore() {
    return xmlStore;
  }

  public final void setXmlStore(String xmlStore) {
    this.xmlStore = xmlStore;
  }

  public List<IngestConfig> getIngestConfigs() {
    return ingestConfigs;
  }

  public final void setIngestConfigs(List<IngestConfig> ingestConfigs) {
    this.ingestConfigs = ingestConfigs;
  }

  public String getDefaultRetentionClass() {
    return defaultRetentionClass;
  }

  public final void setDefaultRetentionClass(String defaultRetentionClass) {
    this.defaultRetentionClass = defaultRetentionClass;
  }

  public List<String> getIngestNodes() {
    return ingestNodes;
  }

  public final void setIngestNodes(List<String> ingestNodes) {
    this.ingestNodes = ingestNodes;
  }

  public String getXdbStore() {
    return xdbStore;
  }

  public final void setXdbStore(String xdbStore) {
    this.xdbStore = xdbStore;
  }

  public boolean isLogStoreEnabled() {
    return logStoreEnabled;
  }

  public final void setLogStoreEnabled(boolean logStoreEnabled) {
    this.logStoreEnabled = logStoreEnabled;
  }

  public List<SubPriority> getSubPriorities() {
    return subPriorities;
  }

  public final void setSubPriorities(List<SubPriority> subPriorities) {
    this.subPriorities = subPriorities;
  }

  public String getXdbLibraryParent() {
    return xdbLibraryParent;
  }

  public final void setXdbLibraryParent(String xdbLibraryParent) {
    this.xdbLibraryParent = xdbLibraryParent;
  }

  public boolean isPdiXmlHashEnforced() {
    return pdiXmlHashEnforced;
  }

  public final void setPdiXmlHashEnforced(boolean pdiXmlHashEnforced) {
    this.pdiXmlHashEnforced = pdiXmlHashEnforced;
  }

  public String getSipStore() {
    return sipStore;
  }

  public final void setSipStore(String sipStore) {
    this.sipStore = sipStore;
  }

  public List<RetentionClass> getRetentionClasses() {
    return retentionClasses;
  }

  public final void setRetentionClasses(List<RetentionClass> retentionClasses) {
    this.retentionClasses = retentionClasses;
  }

  public List<PdiConfig> getPdiConfigs() {
    return pdiConfigs;
  }

  public final void setPdiConfigs(List<PdiConfig> pdiConfigs) {
    this.pdiConfigs = pdiConfigs;
  }

  public final void setAllStores(String store) {
    setCiStore(store);
    setLogStore(store);
    setRenditionStore(store);
    setSipStore(store);
    setXdbStore(store);
    setXmlStore(store);
  }

}
