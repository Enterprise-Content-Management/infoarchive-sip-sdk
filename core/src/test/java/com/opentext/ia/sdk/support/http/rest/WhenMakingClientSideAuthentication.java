/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.test.TestCase;


public class WhenMakingClientSideAuthentication extends TestCase {

  private final String username = randomString();
  private final String password = randomString();
  private final String token = randomString();
  private final BasicAuthentication basicAuth = new BasicAuthentication(username, password);
  private final NonExpiringTokenAuthentication tokenAuth = new NonExpiringTokenAuthentication(token);

  @Test
  public void shouldFailBecauseOfUsername() {
    String illegalUsername = "";
    assertThrows(IllegalArgumentException.class,
        () -> new BasicAuthentication(illegalUsername, password));
  }

  @Test
  public void shouldFailBecauseOfPassword() {
    String illegalPassword = "";
    assertThrows(IllegalArgumentException.class,
        () -> new BasicAuthentication(username, illegalPassword));
  }

  @Test
  public void shouldFailBecauseOfToken() {
    String illegalToken = "";
    assertThrows(IllegalArgumentException.class,
        () -> new NonExpiringTokenAuthentication(illegalToken));
  }

  @Test
  public void shouldIssueBasicHeader() {
    String finalToken = "Basic " + Base64.getEncoder()
                                       .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    Header authHeader = new Header("Authorization", finalToken);
    assertEquals(authHeader, basicAuth.issueAuthHeader(), "Tokens should be the same");
  }

  @Test
  public void shouldIssueGivenHeader() {
    String finalToken = "Bearer " + token;
    Header authHeader = new Header("Authorization", finalToken);
    assertEquals(authHeader, tokenAuth.issueAuthHeader(), "Tokens should be the same");
  }

}
