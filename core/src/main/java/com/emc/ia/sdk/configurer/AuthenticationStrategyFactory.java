/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTENTICATON_TOKEN;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_GATEWAY;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_PASSWORD;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_CLIENT_ID;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_CLIENT_SECRET;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_TOKEN_EXPIRATION_THRESHOLD;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.rest.AuthenticationStrategy;
import com.emc.ia.sdk.support.rest.BasicAuthentication;
import com.emc.ia.sdk.support.rest.GatewayInfo;
import com.emc.ia.sdk.support.rest.JwtAuthentication;
import com.emc.ia.sdk.support.rest.NonExpiringTokenAuthentication;

public final class AuthenticationStrategyFactory {

  private final Map<String, String> configuration;

  public AuthenticationStrategyFactory(Map<String, String> configuration) {
    this.configuration = Objects.requireNonNull(configuration);
  }

  public AuthenticationStrategy getAuthenticationStrategy(Supplier<? extends HttpClient> httpClientSupplier) {
    return Stream.<Supplier<Optional<AuthenticationStrategy>>>of(
               () -> NonExpiringTokenAuthentication.of(configuration.get(SERVER_AUTENTICATON_TOKEN),
                   configuration.get(SERVER_AUTHENTICATION_USER), configuration.get(SERVER_AUTHENTICATION_PASSWORD)),
               () -> BasicAuthentication.of(configuration.get(SERVER_AUTHENTICATION_USER),
                   configuration.get(SERVER_AUTHENTICATION_PASSWORD), configuration.get(SERVER_AUTHENTICATION_GATEWAY)),
               () -> JwtAuthentication.of(
                   configuration.get(SERVER_AUTHENTICATION_USER),
                   configuration.get(SERVER_AUTHENTICATION_PASSWORD),
                   GatewayInfo.of(
                       configuration.get(SERVER_AUTHENTICATION_GATEWAY),
                       configuration.get(SERVER_CLIENT_ID),
                       configuration.get(SERVER_CLIENT_SECRET)
                   ),
                   Integer.parseInt(configuration.getOrDefault(SERVER_TOKEN_EXPIRATION_THRESHOLD, "10")),
                   httpClientSupplier.get()
               )
    ).map(Supplier::get)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Authentication is not configured"));
  }
}
