/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import java.util.Optional;

import com.opentext.ia.sdk.support.http.Header;


/**
 * Authenticate with a token that never expires. This is not very secure!
 */
public final class NonExpiringTokenAuthentication implements AuthenticationStrategy {

  private final String token;

  public static Optional<AuthenticationStrategy> optional(String token, String user, String password) {
    if ((user == null) && (password == null) && (token != null)) {
      return Optional.of(new NonExpiringTokenAuthentication(token));
    } else {
      return Optional.empty();
    }
  }

  public NonExpiringTokenAuthentication(String token) {
    if (token.isEmpty()) {
      throw new IllegalArgumentException("Token is empty");
    } else {
      this.token = "Bearer " + token;
    }
  }

  @Override
  public Header issueAuthHeader() {
    return new Header("Authorization", token);
  }

}
