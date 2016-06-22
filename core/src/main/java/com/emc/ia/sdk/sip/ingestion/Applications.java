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

        private static final String KEY = "applications";
        
	@JsonProperty("_embedded")
	private Map<String,List<Application>> embedded = new HashMap<String,List<Application>>();	

	public Application byName(String name) {
	  if (embedded.containsKey(KEY))
	  {
		for (Application app : embedded.get(KEY)) {
			if (name.equals(app.getName())) {
				return app;
			}
		}
	  }

	  return null;
	}
	
	protected void setApplications(Map<String,List<Application>> apps) {
	 this.embedded = apps; 
	}
}