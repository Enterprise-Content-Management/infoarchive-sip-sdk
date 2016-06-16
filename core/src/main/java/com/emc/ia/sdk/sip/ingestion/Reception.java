/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

public class Reception {

	private String format = "sip_zip";

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	@Override
	public String toString() {
		return "Reception [format=" + format + "]";
	}

}
