/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;


/**
 * How to communicate with the InfoArchive server.
 */
public class ServerConfiguration {

  private String billboardUri;
  private String applicationName;
  private String proxyHost;
  private String proxyPort;
  private String httpClientClassName;
  private String authenticationToken;
  private String authenticationUser;
  private String authenticationPassword;
  private String authenticationGateway;
  private String clientId;
  private String clientSecret;

  public String getBillboardUri() {
    return billboardUri;
  }

  public void setBillboardUri(String billboardUri) {
    this.billboardUri = billboardUri;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
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

}
