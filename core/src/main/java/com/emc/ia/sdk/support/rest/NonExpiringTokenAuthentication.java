/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.util.Map;
import java.util.Optional;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTENTICATON_TOKEN;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_PASSWORD;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER;

public final class NonExpiringTokenAuthentication implements AuthenticationStrategy {

  private final String token;

  public static Optional<AuthenticationStrategy> fromConfiguration(Map<String, String> configuration) {
    if (!configuration.containsKey(SERVER_AUTHENTICATION_USER)
               && !configuration.containsKey(SERVER_AUTHENTICATION_PASSWORD)
               && configuration.containsKey(SERVER_AUTENTICATON_TOKEN)) {
      return Optional.of(new NonExpiringTokenAuthentication(configuration.get(SERVER_AUTENTICATON_TOKEN)));
    } else {
      return Optional.empty();
    }
  }

  public NonExpiringTokenAuthentication(String token) {
    this.token = "Bearer " + token;
  }

  @Override
  public String issueToken() {
    return token;
  }
}
