/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Applications extends LinkContainer {

	@JsonProperty("_embedded")
	private Map<String,List<Application>> embedded = new HashMap<String,List<Application>>();	

	public Application byName(String name) {
		for (Application app : embedded.get("applications")) {
			if (name.equals(app.getName())) {
				return app;
			}
		}

		return null;
	}
}