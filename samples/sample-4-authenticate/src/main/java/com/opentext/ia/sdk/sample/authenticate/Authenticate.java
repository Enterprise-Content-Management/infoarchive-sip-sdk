/*
 * Copyright (c) 2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.authenticate;

import java.util.HashMap;
import java.util.Map;

import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.apache.ApacheHttpClient;
import com.opentext.ia.sdk.support.http.rest.AuthenticationStrategy;
import com.opentext.ia.sdk.support.http.rest.GatewayInfo;
import com.opentext.ia.sdk.support.http.rest.JwtAuthentication;


@SuppressWarnings("PMD")
public final class Authenticate {

  private static final String SETTING_USERNAME = "username";
  private static final String SETTING_PASSWORD = "password";
  private static final String SETTING_GATEWAY_URL = "gateway.url";
  private static final String SETTING_CLIENT_ID = "client.id";
  private static final String SETTING_CLIENT_SECRET = "client.secret";
  // Make sure you have a running InfoArchive cluster with the following characteristics,
  // or set system properties with the correct values
  private static final Map<String, String> DEFAULT_SETTINGS = new HashMap<String, String>() {{
    put(SETTING_USERNAME, "sue@iacustomer.com");
    put(SETTING_PASSWORD, "password");
    put(SETTING_GATEWAY_URL, "http://localhost:8080");
    put(SETTING_CLIENT_ID, "infoarchive.cli");
    put(SETTING_CLIENT_SECRET, "secret");
  }};

  private Authenticate() { }

  public static void main(String[] args) {
    HttpClient httpClient = new ApacheHttpClient();
    try {
      // Log in to the gateway with user name & password
      AuthenticationStrategy authentication = new JwtAuthentication(get(SETTING_USERNAME),
          get(SETTING_PASSWORD), new GatewayInfo(get(SETTING_GATEWAY_URL), get(SETTING_CLIENT_ID),
          get(SETTING_CLIENT_SECRET)), httpClient);

      // Get an authentication header using a token provided by the gateway
      Header header = authentication.issueAuthHeader();

      // Use this header in calls using the HTTP client
      System.out.println(header);
    } finally {
      httpClient.close();
    }
  }

  private static String get(String name) {
    return System.getProperty(name, DEFAULT_SETTINGS.getOrDefault(name, name));
  }

}
