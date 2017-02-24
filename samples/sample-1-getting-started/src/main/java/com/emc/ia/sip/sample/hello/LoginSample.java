/*
 * Copyright (c) 2017 EMC Corporation. All Rights Reserved.
 */

package com.emc.ia.sip.sample.hello;

import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.DefaultClock;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.rest.AuthenticationStrategy;
import com.emc.ia.sdk.support.rest.GatewayInfo;
import com.emc.ia.sdk.support.rest.JwtAuthentication;

public final class LoginSample {

  private LoginSample() { }

  public static void main(String[] args) {

    String gatewayUrl = "http://localhost:8080";
    String username = "sue@iacustomer.com";
    String password = "password";
    String clientId = "infoarchive.cli";
    String clientSecret = "secret";
    HttpClient httpClient = new ApacheHttpClient();
    Clock clock = new DefaultClock();
    GatewayInfo gatewayInfo = new GatewayInfo(gatewayUrl, clientId, clientSecret);
    AuthenticationStrategy authentication =
        new JwtAuthentication(username, password, gatewayInfo, httpClient, clock);

    authentication.issueAuthHeader();
  }

}
