/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;


public interface InfoArchiveConnectionProperties {

  String PREFIX = "ia.";

  String HTTP_CLIENT_CLASSNAME = PREFIX + "http.client";

  String PROXY_HOST = "proxy.host";
  String PROXY_PORT = "proxy.port";

  String SERVER_PREFIX = PREFIX + "server.";
  String SERVER_AUTHENTICATION_TOKEN = SERVER_PREFIX + "authentication.token";
  String SERVER_AUTHENTICATION_USER = SERVER_PREFIX + "authentication.user";
  String SERVER_AUTHENTICATION_PASSWORD = SERVER_PREFIX + "authentication.password";
  String SERVER_AUTHENTICATION_GATEWAY = SERVER_PREFIX + "authentication.gateway";
  String SERVER_CLIENT_ID = SERVER_PREFIX + "client_id";
  String SERVER_CLIENT_SECRET = SERVER_PREFIX + "client_secret";
  String SERVER_SCOPES = SERVER_PREFIX + "scopes";
  String SERVER_URI = SERVER_PREFIX + "uri";


}
