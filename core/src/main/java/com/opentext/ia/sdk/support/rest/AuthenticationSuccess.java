/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.rest;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthenticationSuccess {

  private String accessToken;
  private String tokenType;
  private String refreshToken;
  private long expiresIn;
  private String scope;
  private String jti;

  public AuthenticationSuccess() {
    setAccessToken("");
    setTokenType("");
    setRefreshToken("");
    setScope("");
    setJti("");
  }

  public String getAccessToken() {
    return accessToken;
  }

  @JsonProperty("access_token")
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  @JsonProperty("token_type")
  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  @JsonProperty("refresh_token")
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  @JsonProperty("expires_in")
  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getJti() {
    return jti;
  }

  public void setJti(String jti) {
    this.jti = jti;
  }

}
