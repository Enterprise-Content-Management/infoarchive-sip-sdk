/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class ReceiverNode extends NamedLinkContainer {

  private FileSystemFolder workingDirectory = new FileSystemFolder();
  private Sips sips = new Sips();

  public FileSystemFolder getWorkingDirectory() {
    return workingDirectory;
  }

  public void setWorkingDirectory(FileSystemFolder workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public Sips getSips() {
    return sips;
  }

  public void setSips(Sips sips) {
    this.sips = sips;
  }

}
