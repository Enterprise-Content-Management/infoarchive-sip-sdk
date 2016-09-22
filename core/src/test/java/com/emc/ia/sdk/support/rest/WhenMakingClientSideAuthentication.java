/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Test;

import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.test.TestCase;

public class WhenMakingClientSideAuthentication extends TestCase {

  private final String username = randomString();
  private final String password = randomString();
  private final String token = randomString();
  private final BasicAuthentication basicAuth = new BasicAuthentication(username, password);
  private final NonExpiringTokenAuthentication tokenAuth = new NonExpiringTokenAuthentication(token);

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
