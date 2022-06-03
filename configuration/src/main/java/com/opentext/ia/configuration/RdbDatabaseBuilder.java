/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

/**
 * Build an xDB database.
 * 
 * @author Ray Sinnema
 * @since 9.6.0
 *
 * @param <C> The type of configuration to build
 */
public class RdbDatabaseBuilder<P extends BaseBuilder<?, C>, C extends Configuration<?>>
    extends NamedObjectBuilder<P, RdbDatabaseBuilder<P, C>, C> {

  protected RdbDatabaseBuilder(P parent) {
    super(parent, "rdbDatabase");
    setEncoding("base64");
  }

  private void setAdminPassword(String adminPassword) {
    setProperty("adminPassword", adminPassword);
  }

  public RdbDatabaseBuilder<P, C> protectedWithAdminPassword(String adminPassword) {
    setAdminPassword(adminPassword);
    return this;
  }

  private void setAdminUser(String adminUser) {
    setProperty("adminUser", adminUser);
  }

  public RdbDatabaseBuilder<P, C> withAdminUser(String adminUser) {
    setAdminUser(adminUser);
    return this;
  }

  private void setName(String name) {
    setProperty("name", name);
  }

  public RdbDatabaseBuilder<P, C> withName(String nodeName) {
    setName(nodeName);
    return this;
  }

  private void setEncoding(String encoding) {
    setProperty("encoding", encoding);
  }

  public RdbDatabaseBuilder<P, C> encryptedBy(String cryptoObjectName) {
    setProperty("cryptoObject", cryptoObjectName);
    return this;
  }

  public RdbDatabaseBuilder<P, C> withEncoding(String encoding) {
    setEncoding(encoding);
    return this;
  }

}
