/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Build an xDB cluster.
 * @author Ray Sinnema
 * @since 9.9.0
 *
 * @param <C> The type of configuration to build
 */
public class XdbClusterBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ConfigurationBuilder<C>, XdbClusterBuilder<C>, C> {

  protected XdbClusterBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "xdbCluster");
    setSuperUserPassword("test");
    setUninitialized("description");
    setBootstraps(Collections.emptyList());
  }

  private void setSuperUserPassword(String superUserPassword) {
    setProperty("superUserPassword", superUserPassword);
  }

  public XdbClusterBuilder<C> protectedWithPassword(String superUserPassword) {
    setSuperUserPassword(superUserPassword);
    return this;
  }

  public XdbClusterBuilder<C> withDescription(String description) {
    setProperty("description", description);
    return this;
  }

  private void setBootstraps(List<String> bootstraps) {
    setProperty("bootstraps", bootstraps);
  }

  public XdbClusterBuilder<C> withBootstrap(String bootstrap) {
    @SuppressWarnings("unchecked")
    List<String> newBootstraps = new ArrayList<>((List<String>)getProperty("bootstraps"));
    newBootstraps.add(bootstrap);
    setBootstraps(newBootstraps);
    return this;
  }

  public XdbDatabaseBuilder<XdbClusterBuilder<C>, C> withXdbDatabase() {
    return new XdbDatabaseBuilder<>(this);
  }

}
