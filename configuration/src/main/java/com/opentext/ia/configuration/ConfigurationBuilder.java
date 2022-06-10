/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

/**
 * Build InfoArchive configurations.
 * 
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <C> The type of configuration to build
 */
public class ConfigurationBuilder<C extends Configuration<?>>
    extends BaseBuilder<BaseBuilder<?, C>, C> {

  public ConfigurationBuilder(ConfigurationProducer<C> producer) {
    super(producer, null);
  }

  /**
   * Start building a tenant.
   * 
   * @return A builder for the new tenant
   */
  public TenantBuilder<C> withTenant() {
    return new TenantBuilder<>(this);
  }

  /**
   * Start building an application in a new tenant.
   * 
   * @return A builder for the new application
   */
  public ApplicationBuilder<C> withApplication() {
    return withTenant().withApplication();
  }

  /**
   * Start building a search in a new application in a new tenant.
   * 
   * @return A builder for the new search
   */
  public SearchBuilder<C> withSearch() {
    return withApplication().withSearch();
  }

  /**
   * Start building a file system root.
   * 
   * @return A builder for the new file system root
   * @since 9.5.0
   */
  public FileSystemRootBuilder<C> withFileSystemRoot() {
    return new FileSystemRootBuilder<>(this);
  }

  /**
   * Start building a space in a new application in a new tenant.
   * 
   * @return A builder for the new space
   * @since 9.5.0
   */
  public SpaceBuilder<C> withSpace() {
    return withApplication().withSpace();
  }

  /**
   * Start building a holding in a new application in a new tenant.
   * 
   * @return A builder for the new holding
   * @since 9.5.0
   */
  public HoldingBuilder<C> withHolding() {
    return withApplication().withHolding();
  }

  /**
   * Start building a crypto object.
   * 
   * @return A builder for the new crypto object
   * @since 9.6.0
   */
  public CryptoObjectBuilder<C> withCryptoObject() {
    return new CryptoObjectBuilder<>(this);
  }

  /**
   * Start building an rDB data node.
   * 
   * @return A builder for the new rDB data node
   * @since 12.6.0
   */
  public RdbDataNodeBuilder<C> withRdbDataNode() {
    return new RdbDataNodeBuilder<C>(this);
  }

  /**
   * Start building an rDB database in a new rDB data node.
   * 
   * @return A builder for the new rDB database
   * @since 12.6.0
   */
  public RdbDatabaseBuilder<RdbDataNodeBuilder<C>, C> withRdbDatabase() {
    return withRdbDataNode().withRdbDatabase();
  }

}
