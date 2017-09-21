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
 * Determine what type of {@linkplain AuthenticationStrategy} to use based on connection properties.
 */
public final class AuthenticationStrategyFactory {

  private final ArchiveConnection connection;

  public AuthenticationStrategyFactory(ArchiveConnection connection) {
    this.connection = Objects.requireNonNull(connection);
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
        () -> NonExpiringTokenAuthentication.optional(connection.getAuthenticationToken(),
            connection.getAuthenticationUser(), connection.getAuthenticationPassword()),
        () -> BasicAuthentication.optional(connection.getAuthenticationUser(),
            connection.getAuthenticationPassword(), connection.getAuthenticationGateway()),
        () -> JwtAuthentication.optional(
            connection.getAuthenticationUser(),
            connection.getAuthenticationPassword(),
            GatewayInfo.optional(
                connection.getAuthenticationGateway(),
                connection.getClientId(),
                connection.getClientSecret()
            ).orElse(null),
            httpClientSupplier.get(),
            clockSupplier.get()));
  }

}
