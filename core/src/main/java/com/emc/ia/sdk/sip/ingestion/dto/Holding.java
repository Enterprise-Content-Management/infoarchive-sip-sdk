/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Holding extends NamedLinkContainer {

  private IngestNode ingestNode = new IngestNode();

  public IngestNode getIngestNode() {
    return ingestNode;
  }

  public void setIngestNode(IngestNode ingestNode) {
    this.ingestNode = ingestNode;
  }

}
