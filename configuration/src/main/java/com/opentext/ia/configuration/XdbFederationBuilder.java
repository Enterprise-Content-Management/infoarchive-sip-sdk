/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build an xDB federation.
 * @author Ray Sinnema
 * @since 9.6.0
 *
 * @param <C> The type of configuration to build
 */
public class XdbFederationBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ConfigurationBuilder<C>, XdbFederationBuilder<C>, C> {

  protected XdbFederationBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "xdbFederation");
    setBootstrap("xhive://127.0.0.1:2910");
    setSuperUserPassword("test");
  }

  private void setBootstrap(String bootstrap) {
    setProperty("bootstrap", bootstrap);
  }

  public XdbFederationBuilder<C> runningAt(String bootstrap) {
    setBootstrap(bootstrap);
    return this;
  }

  private void setSuperUserPassword(String superUserPassword) {
    setProperty("superUserPassword", superUserPassword);
  }

  public XdbFederationBuilder<C> protectedWithPassword(String superUserPassword) {
    setSuperUserPassword(superUserPassword);
    return this;
  }

  public XdbFederationBuilder<C> encryptedBy(String cryptoObjectName) {
    setProperty("cryptoObject", cryptoObjectName);
    return this;
  }

  public XdbDatabaseBuilder<XdbFederationBuilder<C>, C> withXdbDatabase() {
    return new XdbDatabaseBuilder<>(this);
  }

}
