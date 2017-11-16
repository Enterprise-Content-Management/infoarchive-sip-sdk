/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import org.json.JSONObject;


public interface ConfigurationWriter {

  Configuration build(JSONObject object);

}
