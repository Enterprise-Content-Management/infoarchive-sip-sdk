/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * Build a job definition.
 * @author Ray Sinnema
 * @since 9.7.0
 *
 * @param <C> The type of configuration to build
 */
public class JobDefinitionBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ConfigurationBuilder<C>, JobDefinitionBuilder<C>, C> {

  private final Map<String, String> properties = new HashMap<>();

  protected JobDefinitionBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "jobDefinition");
    setReadOnly(false);
    setApplicationScoped(true);
    setTenantScoped(true);
    setProperty("systemScoped", true);
    setMaxAttempts(1);
    setExpirationInterval(60000);
    setRescheduleInterval(60000);
  }

  private void setReadOnly(boolean readOnly) {
    setProperty("readOnly", readOnly);
  }

  public JobDefinitionBuilder<C> readOnly() {
    setReadOnly(true);
    return this;
  }

  private void setApplicationScoped(boolean applicationScoped) {
    setProperty("applicationScoped", applicationScoped);
  }

  public JobDefinitionBuilder<C> scopedToTenant() {
    setApplicationScoped(false);
    return this;
  }

  private void setTenantScoped(boolean tenantScoped) {
    setProperty("tenantScoped", tenantScoped);
  }

  public JobDefinitionBuilder<C> scopedToSystem() {
    setApplicationScoped(false);
    setTenantScoped(false);
    return this;
  }

  private void setMaxAttempts(int maxAttempts) {
    setProperty("maxAttempts", maxAttempts);
  }

  public JobDefinitionBuilder<C> attemptMax(int maxAttempts) {
    setMaxAttempts(maxAttempts);
    return this;
  }

  private void setExpirationInterval(int expirationInterval) {
    setProperty("expirationInterval", expirationInterval);
  }

  public JobDefinitionBuilder<C> expireAfter(int expirationInterval) {
    setExpirationInterval(expirationInterval);
    return this;
  }

  private void setRescheduleInterval(int rescheduleInterval) {
    setProperty("rescheduleInterval", rescheduleInterval);
  }

  public JobDefinitionBuilder<C> rescheduleAfter(int rescheduleInterval) {
    setRescheduleInterval(rescheduleInterval);
    return this;
  }

  public JobDefinitionBuilder<C> handledBy(String handler) {
    setProperty("handlerName", handler);
    return this;
  }

  public JobDefinitionBuilder<C> withDescription(String description) {
    setProperty("description", description);
    return this;
  }

  public JobDefinitionBuilder<C> withProperty(String name, String value) {
    properties.put(name, value);
    return this;
  }

  @Override
  public ConfigurationBuilder<C> end() {
    setProperty("properties", new JSONObject(properties));
    return super.end();
  }

}
