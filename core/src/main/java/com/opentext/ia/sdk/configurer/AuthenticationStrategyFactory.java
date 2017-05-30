/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.configurer;

import static com.opentext.ia.sdk.configurer.InfoArchiveConfiguration.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.rest.*;

public final class AuthenticationStrategyFactory {

  private final Map<String, String> configuration;

  public AuthenticationStrategyFactory(Map<String, String> configuration) {
    this.configuration = Objects.requireNonNull(configuration);
  }

  public AuthenticationStrategy getAuthenticationStrategy(Supplier<? extends HttpClient> httpClientSupplier,
                                                          Supplier<? extends Clock> clockSupplier) {
    return Stream.<Supplier<Optional<AuthenticationStrategy>>>of(
               () -> NonExpiringTokenAuthentication.optional(configuration.get(SERVER_AUTENTICATON_TOKEN),
                   configuration.get(SERVER_AUTHENTICATION_USER), configuration.get(SERVER_AUTHENTICATION_PASSWORD)),
               () -> BasicAuthentication.optional(configuration.get(SERVER_AUTHENTICATION_USER),
                   configuration.get(SERVER_AUTHENTICATION_PASSWORD), configuration.get(SERVER_AUTHENTICATION_GATEWAY)),
               () -> JwtAuthentication.optional(
                   configuration.get(SERVER_AUTHENTICATION_USER),
                   configuration.get(SERVER_AUTHENTICATION_PASSWORD),
                   GatewayInfo.optional(
                       configuration.get(SERVER_AUTHENTICATION_GATEWAY),
                       configuration.get(SERVER_CLIENT_ID),
                       configuration.get(SERVER_CLIENT_SECRET)
                   ).orElse(null),
                   httpClientSupplier.get(),
                   clockSupplier.get()
               )
    ).map(Supplier::get)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Authentication is not configured"));
  }
}
