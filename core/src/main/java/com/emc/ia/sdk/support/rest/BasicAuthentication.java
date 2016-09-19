/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_GATEWAY;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_PASSWORD;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER;

public final class BasicAuthentication implements AuthenticationStrategy {

  private final String username;
  private final String password;

  public static Optional<AuthenticationStrategy> fromConfiguration(Map<String, String> configuration) {
    if (configuration.containsKey(SERVER_AUTHENTICATION_USER)
               && configuration.containsKey(SERVER_AUTHENTICATION_PASSWORD)
               && !configuration.containsKey(SERVER_AUTHENTICATION_GATEWAY)) {
      return Optional.of(new BasicAuthentication(configuration.get(SERVER_AUTHENTICATION_USER),
                                                    configuration.get(SERVER_AUTHENTICATION_PASSWORD)));
    } else {
      return Optional.empty();
    }
  }

  public BasicAuthentication(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public String issueToken() {
    return Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
  }
}
