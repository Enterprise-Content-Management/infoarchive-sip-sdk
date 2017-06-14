/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.api;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import com.opentext.ia.sdk.dto.Services;
import com.opentext.ia.sdk.support.NewInstance;
import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.datetime.DefaultClock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.apache.ApacheHttpClient;
import com.opentext.ia.sdk.support.http.rest.AuthenticationStrategy;
import com.opentext.ia.sdk.support.http.rest.RestClient;

/**
 * How to communicate with the Archive server.
 */
public class ArchiveConnection {

  private Clock clock = new DefaultClock();
  private String billboardUri;
  private String proxyHost;
  private String proxyPort;
  private String httpClientClassName;
  private String authenticationToken;
  private String authenticationUser;
  private String authenticationPassword;
  private String authenticationGateway;
  private String clientId;
  private String clientSecret;
  private RestClient restClient;

  public String getBillboardUri() {
    return billboardUri;
  }

  public void setBillboardUri(String billboardUri) {
    this.billboardUri = billboardUri;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public String getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(String proxyPort) {
    this.proxyPort = proxyPort;
  }

  public String getHttpClientClassName() {
    return httpClientClassName;
  }

  public void setHttpClientClassName(String httpClientClassName) {
    this.httpClientClassName = httpClientClassName;
  }

  public String getAuthenticationToken() {
    return authenticationToken;
  }

  public void setAuthenticationToken(String authenticationToken) {
    this.authenticationToken = authenticationToken;
  }

  public String getAuthenticationUser() {
    return authenticationUser;
  }

  public void setAuthenticationUser(String authenticationUser) {
    this.authenticationUser = authenticationUser;
  }

  public String getAuthenticationPassword() {
    return authenticationPassword;
  }

  public void setAuthenticationPassword(String authenticationPassword) {
    this.authenticationPassword = authenticationPassword;
  }

  public String getAuthenticationGateway() {
    return authenticationGateway;
  }

  public void setAuthenticationGateway(String authenticationGateway) {
    this.authenticationGateway = authenticationGateway;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public Clock getClock() {
    return clock;
  }

  public void setClock(Clock clock) {
    this.clock = Optional.ofNullable(clock).orElseGet(DefaultClock::new);
  }

  public RestClient getRestClient() {
    if (restClient == null) {
      HttpClient httpClient;
      if (StringUtils.isBlank(proxyHost) && StringUtils.isBlank(proxyPort)) {
        httpClient = NewInstance.of(getHttpClientClassName(), ApacheHttpClient.class.getName())
            .as(HttpClient.class);
      } else {
        httpClient = new ApacheHttpClient(proxyHost, Integer.parseInt(proxyPort));
      }
      restClient = new RestClient(httpClient);
      AuthenticationStrategy authentication = new AuthenticationStrategyFactory(this).getAuthenticationStrategy(
          () -> httpClient, () -> clock);
      restClient.init(authentication);
    }
    return restClient;
  }

  public void setRestClient(RestClient restClient) {
    this.restClient = restClient;
  }

  public Services getServices() throws IOException {
    return getRestClient().get(getBillboardUri(), Services.class);
  }

}
