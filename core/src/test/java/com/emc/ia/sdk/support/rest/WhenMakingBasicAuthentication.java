package com.emc.ia.sdk.support.rest;

import static org.junit.Assert.assertEquals;

import com.emc.ia.sdk.support.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WhenMakingBasicAuthentication extends TestCase {

  private final String username = randomString();
  private final String password = randomString();
  private final BasicAuthentication auth = new BasicAuthentication(username, password);
  private String finalToken;

  @Before
  public void init() {
    finalToken = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void shouldIssueNewToken() {
    assertEquals("Tokens should be the same", finalToken, auth.issueToken());
  }

}
