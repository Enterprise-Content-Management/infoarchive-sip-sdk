/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import com.opentext.ia.sdk.support.http.Header;

public final class BasicAuthentication implements AuthenticationStrategy {

  private final String username;
  private final String password;

  public static Optional<AuthenticationStrategy> optional(String username, String password, String gateway) {
    if ((username != null) && (password != null) && (gateway == null)) {
      return Optional.of(new BasicAuthentication(username, password));
    } else {
      return Optional.empty();
    }
  }

  public BasicAuthentication(String username, String password) {
    if (username.isEmpty()) {
      throw new IllegalArgumentException("Username is empty");
    } else {
      this.username = username;
    }
    if (password.isEmpty()) {
      throw new IllegalArgumentException("Password is empty");
    } else {
      this.password = password;
    }
  }

  @Override
  public Header issueAuthHeader() {
    String token = "Basic " + Base64.getEncoder()
                                  .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    return new Header("Authorization", token);
  }
}
