/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import com.opentext.ia.sdk.support.http.Header;


/**
 *  How to authenticate with an HTTP server.
 */
public interface AuthenticationStrategy {

  Header issueAuthHeader();

}
