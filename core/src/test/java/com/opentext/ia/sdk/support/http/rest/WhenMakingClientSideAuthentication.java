/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Test;

import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.test.TestCase;


public class WhenMakingClientSideAuthentication extends TestCase {

  private final String username = randomString();
  private final String password = randomString();
  private final String token = randomString();
  private final BasicAuthentication basicAuth = new BasicAuthentication(username, password);
  private final NonExpiringTokenAuthentication tokenAuth = new NonExpiringTokenAuthentication(token);

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailBecauseOfUsername() {
    String illegalUsername = "";
    new BasicAuthentication(illegalUsername, password);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailBecauseOfPassword() {
    String illegalPassword = "";
    new BasicAuthentication(username, illegalPassword);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailBecauseOfToken() {
    String illegalToken = "";
    new NonExpiringTokenAuthentication(illegalToken);
  }

  @Test
  public void shouldIssueBasicHeader() {
    String finalToken = "Basic " + Base64.getEncoder()
                                       .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    Header authHeader = new Header("Authorization", finalToken);
    assertEquals("Tokens should be the same", authHeader, basicAuth.issueAuthHeader());
  }

  @Test
  public void shouldIssueGivenHeader() {
    String finalToken = "Bearer " + token;
    Header authHeader = new Header("Authorization", finalToken);
    assertEquals("Tokens should be the same", authHeader, tokenAuth.issueAuthHeader());
  }

}
