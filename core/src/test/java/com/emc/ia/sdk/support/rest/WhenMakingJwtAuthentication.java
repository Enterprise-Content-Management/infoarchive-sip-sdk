package com.emc.ia.sdk.support.rest;

import com.emc.ia.sdk.sip.client.dto.result.AuthenticationSuccess;
import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenMakingJwtAuthentication extends TestCase {

  private final String gatewayUri = "http://authgateway.com/";
  private final String username = randomString();
  private final String password = randomString();
  private final HttpClient httpClient = mock(HttpClient.class);
  private final AuthenticationStrategy authentication =
      new JwtAuthentication(gatewayUri, username, password, httpClient);
  private final String token = randomString();
  private final AuthenticationSuccess authResult = new AuthenticationSuccess();

  @Before
  public void init() throws IOException {
    authResult.setAccessToken(token);
    when(httpClient.post(any(), any(), any(), eq(AuthenticationSuccess.class))).thenReturn(authResult);
  }

  @Test
  public void shouldCorrectlyFormatToken() {
    assertEquals("Should add prefix", "Bearer " + token, authentication.issueToken());
  }

  @Test
  public void shouldCorrectlyFormUrl() throws IOException {
    authentication.issueToken();
    verify(httpClient).post(eq("http://authgateway.com/oauth/token"), any(), any(), eq(AuthenticationSuccess.class));
  }

  @Test
  public void shouldCorrectlyFormAuthorizationHeader() throws IOException {
    String authToken = Base64.getEncoder().encodeToString("infoarchive.sipsdk:secret".getBytes(StandardCharsets.UTF_8));
    Collection<Header> headers = Collections.singletonList(new Header("Authorization", "Basic " + authToken));
    authentication.issueToken();
    verify(httpClient).post(any(), eq(headers), any(), eq(AuthenticationSuccess.class));
  }

  @Test
  public void shouldCorrectlyFormPayload() throws IOException {
    String payload = "grant_type=password&username=" + username + "&password=" + password;
    authentication.issueToken();
    verify(httpClient).post(any(), any(), eq(payload), eq(AuthenticationSuccess.class));
  }

}
