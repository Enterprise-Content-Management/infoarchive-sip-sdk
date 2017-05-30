/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.rest;

import com.opentext.ia.sdk.support.http.Header;

public interface AuthenticationStrategy {
  Header issueAuthHeader();
}
