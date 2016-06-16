/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

public class IAHomeResource extends LinkContainer {

	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IAHomeResource [name=");
		builder.append(name);
		builder.append(", links=");
		builder.append(getLinks());
		builder.append("]");
		return builder.toString();
	}

}
