/*
 * Copyright (c) 2017 by OpenText Corporation. All Rights Reserved.
 */

package com.opentext.ia.sdk.sample.authenticate;

import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.apache.ApacheHttpClient;
import com.opentext.ia.sdk.support.rest.AuthenticationStrategy;
import com.opentext.ia.sdk.support.rest.GatewayInfo;
import com.opentext.ia.sdk.support.rest.JwtAuthentication;


public final class Authenticate {

  private static final String CLIENT_SECRET = "secret";
  private static final String CLIENT_ID = "infoarchive.cli";
  private static final String PASSWORD = "password";
  private static final String USER_NAME = "sue@iacustomer.com";
  private static final String GATEWAY_URL = "http://localhost:8888";

  private Authenticate() { }

  public static void main(String[] args) {
    HttpClient httpClient = new ApacheHttpClient();
    try {
      // Log in to the gateway with user name & password
      AuthenticationStrategy authentication = new JwtAuthentication(USER_NAME, PASSWORD,
          new GatewayInfo(GATEWAY_URL, CLIENT_ID, CLIENT_SECRET), httpClient);

      // Get an authentication header using a token provided by the gateway
      Header header = authentication.issueAuthHeader();

      // Use this header in calls using the HTTP client
      System.out.println(header); // NOPMD
    } finally {
      httpClient.close();
    }
  }

}
