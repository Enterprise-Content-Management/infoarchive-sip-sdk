/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class IngestNode extends NamedLinkContainer {

  private boolean enumerationMinusRunning;
  private int enumerationCutoffDays;
  private String logLevel;
  private String workingDirectory;

  public IngestNode() {
    setEnumerationMinusRunning(true);
    setEnumerationCutoffDays(30);
    setLogLevel("INFO");
  }

  public boolean isEnumerationMinusRunning() {
    return enumerationMinusRunning;
  }

  public final void setEnumerationMinusRunning(boolean enumerationMinusRunning) {
    this.enumerationMinusRunning = enumerationMinusRunning;
  }

  public int getEnumerationCutoffDays() {
    return enumerationCutoffDays;
  }

  public final void setEnumerationCutoffDays(int enumerationCutoffDays) {
    this.enumerationCutoffDays = enumerationCutoffDays;
  }

  public String getLogLevel() {
    return logLevel;
  }

  public final void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  public String getWorkingDirectory() {
    return workingDirectory;
  }

  public final void setWorkingDirectory(String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

}
