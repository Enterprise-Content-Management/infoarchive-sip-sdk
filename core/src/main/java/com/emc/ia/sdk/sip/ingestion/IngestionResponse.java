/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

public class IngestionResponse extends LinkContainer {

	private String name;
	private String aipId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAipId() {
		return aipId;
	}

	public void setAipId(String aipId) {
		this.aipId = aipId;
	}

	@Override
	public String toString() {
		return "IngestionResponse [name=" + name + ", aipId=" + aipId
				+ ", getLinks()=" + getLinks() + "]";
	}

}
