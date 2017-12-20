/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

/**
 * Build InfoArchive configurations.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <C> The type of configuration to build
 */
public class ConfigurationBuilder<C extends Configuration<?>> extends BaseBuilder<BaseBuilder<?, C>, C> {

  public ConfigurationBuilder(ConfigurationProducer<C> producer) {
    super(producer, null);
  }

  /**
   * Start building a tenant.
   * @return A builder for the new tenant
   */
  public TenantBuilder<C> withTenant() {
    return new TenantBuilder<>(this);
  }

  /**
   * Start building an application in a new tenant.
   * @return A builder for the new application
   */
  public ApplicationBuilder<C> withApplication() {
    return withTenant().withApplication();
  }

  /**
   * Start building a search in a new application in a new tenant.
   * @return A builder for the new search
   */
  public SearchBuilder<C> withSearch() {
    return withApplication().withSearch();
  }

  /**
   * Start building a file system root.
   * @return A builder for the new file system root
   * @since 9.5.0
   */
  public FileSystemRootBuilder<C> withFileSystemRoot() {
    return new FileSystemRootBuilder<>(this);
  }

  /**
   * Start building a space in a new application in a new tenant.
   * @return A builder for the new space
   * @since 9.5.0
   */
  public SpaceBuilder<C> withSpace() {
    return withApplication().withSpace();
  }

  /**
   * Start building a holding in a new application in a new tenant.
   * @return A builder for the new holding
   * @since 9.5.0
   */
  public HoldingBuilder<C> withHolding() {
    return withApplication().withHolding();
  }

  /**
   * Start building a crypto object.
   * @return A builder for the new crypto object
   * @since 9.6.0
   */
  public CryptoObjectBuilder<C> withCryptoObject() {
    return new CryptoObjectBuilder<>(this);
  }

  /**
   * Start building an xDB federation.
   * @return A builder for the new xDB federation
   * @since 9.6.0
   */
  public XdbFederationBuilder<C> withXdbFederation() {
    return new XdbFederationBuilder<>(this);
  }

  /**
   * Start building an xDB database in a new xDB federation.
   * @return A builder for the new xDB database
   * @since 9.6.0
   */
  public XdbDatabaseBuilder<XdbFederationBuilder<C>, C> withXdbDatabase() {
    return withXdbFederation().withXdbDatabase();
  }

  /**
   * Start building a job definition.
   * @return A builder for the new job definition
   * @since 9.7.0
   */
  public JobDefinitionBuilder<C> withJobDefinition() {
    return new JobDefinitionBuilder<>(this);
  }

  /**
   * Start building an xDB cluster.
   * @return A builder for the new xDB cluster
   * @since 9.9.0
   */
  public XdbClusterBuilder<C> withXdbCluster() {
    return new XdbClusterBuilder<>(this);
  }

}
