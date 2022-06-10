/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

/**
 * Build an rDB data node.
 * 
 * @author Ray Sinnema
 * @since 12.6.0
 *
 * @param <C> The type of configuration to build
 */
public class RdbDataNodeBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ConfigurationBuilder<C>, RdbDataNodeBuilder<C>, C> {

  protected RdbDataNodeBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "rdbDataNode");
    setBootstrap("jdbc:postgresql://localhost:5432");
    setEncoding("base64");
  }

  private void setBootstrap(String bootstrap) {
    setProperty("bootstrap", bootstrap);
  }

  public RdbDataNodeBuilder<C> runningAt(String bootstrap) {
    setBootstrap(bootstrap);
    return this;
  }

  private void setUserName(String userName) {
    setProperty("userName", userName);
  }

  public RdbDataNodeBuilder<C> withUserName(String userName) {
    setUserName(userName);
    return this;
  }

  private void setSuperUserPassword(String superUserPassword) {
    setProperty("superUserPassword", superUserPassword);
  }

  public RdbDataNodeBuilder<C> protectedWithPassword(String userPassword) {
    setSuperUserPassword(userPassword);
    return this;
  }

  private void setName(String name) {
    setProperty("name", name);
  }

  public RdbDataNodeBuilder<C> withName(String nodeName) {
    setName(nodeName);
    return this;
  }

  private void setEncoding(String encoding) {
    setProperty("encoding", encoding);
  }

  public RdbDataNodeBuilder<C> encryptedBy(String cryptoObjectName) {
    setProperty("cryptoObject", cryptoObjectName);
    return this;
  }

  public RdbDatabaseBuilder<RdbDataNodeBuilder<C>, C> withRdbDatabase() {
    return new RdbDatabaseBuilder<>(this);
  }

}
