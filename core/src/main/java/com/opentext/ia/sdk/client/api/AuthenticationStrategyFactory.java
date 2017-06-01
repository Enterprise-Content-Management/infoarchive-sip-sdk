/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.api;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.rest.*;


/**
 * Determine what type of {@linkplain AuthenticationStrategy} to use based on what configuration properties are
 * specified.
 */
public final class AuthenticationStrategyFactory {

  private final ServerConfiguration configuration;

  public AuthenticationStrategyFactory(ServerConfiguration configuration) {
    this.configuration = Objects.requireNonNull(configuration);
  }

  public AuthenticationStrategy getAuthenticationStrategy(Supplier<? extends HttpClient> httpClientSupplier,
                                                          Supplier<? extends Clock> clockSupplier) {
    return strategySuppliers(httpClientSupplier, clockSupplier)
        .map(Supplier::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Authentication is not configured"));
  }

  private Stream<Supplier<Optional<AuthenticationStrategy>>> strategySuppliers(
      Supplier<? extends HttpClient> httpClientSupplier, Supplier<? extends Clock> clockSupplier) {
    return Stream.<Supplier<Optional<AuthenticationStrategy>>>of(
        () -> NonExpiringTokenAuthentication.optional(configuration.getAuthenticationToken(),
            configuration.getAuthenticationUser(), configuration.getAuthenticationPassword()),
        () -> BasicAuthentication.optional(configuration.getAuthenticationUser(),
            configuration.getAuthenticationPassword(), configuration.getAuthenticationGateway()),
        () -> JwtAuthentication.optional(
            configuration.getAuthenticationUser(),
            configuration.getAuthenticationPassword(),
            GatewayInfo.optional(
                configuration.getAuthenticationGateway(),
                configuration.getClientId(),
                configuration.getClientSecret()
            ).orElse(null),
            httpClientSupplier.get(),
            clockSupplier.get()));
  }

}
