/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build an xDB database.
 * @author Ray Sinnema
 * @since 9.6.0
 *
 * @param <C> The type of configuration to build
 */
public class XdbDatabaseBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<XdbFederationBuilder<C>, XdbDatabaseBuilder<C>, C> {

  protected XdbDatabaseBuilder(XdbFederationBuilder<C> parent) {
    super(parent, "xdbDatabase");
    setAdminPassword("secret");
  }

  private void setAdminPassword(String adminPassword) {
    setProperty("adminPassword", adminPassword);
  }

  public XdbDatabaseBuilder<C> protectedWithPassword(String adminPassword) {
    setAdminPassword(adminPassword);
    return this;
  }

}
