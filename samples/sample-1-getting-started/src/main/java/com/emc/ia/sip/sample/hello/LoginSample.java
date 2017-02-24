package com.emc.ia.sip.sample.hello;

import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.DefaultClock;
import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.rest.AuthenticationStrategy;
import com.emc.ia.sdk.support.rest.GatewayInfo;
import com.emc.ia.sdk.support.rest.JwtAuthentication;

/**
 * Created by zasied on 2/23/2017.
 */
public class LoginSample {

  public static void main(String[] args) {

    String GATEWAY_URL = "http://localhost:8080";
    String username = "sue@iacustomer.com";
    String password = "password";
    String clientId = "infoarchive.cli";
    String clientSecret = "secret";
    HttpClient httpClient = new ApacheHttpClient();
    Clock clock = new DefaultClock();
    GatewayInfo gatewayInfo = new GatewayInfo(GATEWAY_URL, clientId, clientSecret);
    AuthenticationStrategy authentication =
        new JwtAuthentication(username, password, gatewayInfo, httpClient, clock);

    Header authResult = authentication.issueAuthHeader();
    System.out.println("Authorization access_token is: " + authResult.getValue());
    System.exit(0);
  }

}
