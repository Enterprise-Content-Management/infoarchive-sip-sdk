/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import com.emc.ia.sdk.support.http.Header;

public interface AuthenticationStrategy {
  Header issueAuthHeader();
}
