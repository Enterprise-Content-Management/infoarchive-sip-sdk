/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;
public class Application extends LinkContainer {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(255);
		builder.append("Application [name=");
		builder.append(name);
		builder.append(", getLinks()=");
		builder.append(getLinks());
		builder.append(" ]");
		return builder.toString();
	}

}
