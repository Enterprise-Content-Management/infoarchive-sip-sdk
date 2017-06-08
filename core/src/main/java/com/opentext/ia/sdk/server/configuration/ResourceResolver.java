/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.util.function.Function;


/**
 * Resolve a resource name to its contents.
 */
public interface ResourceResolver extends Function<String, String> {

}
