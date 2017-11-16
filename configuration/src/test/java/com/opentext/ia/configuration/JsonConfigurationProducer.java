/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.List;


public class JsonConfigurationProducer implements ConfigurationProducer<ConfigurationObject> {

  @Override
  public Configuration<ConfigurationObject> produce(ConfigurationObject container) {
    return new JsonConfiguration(container);
  }


  public static class JsonConfiguration implements Configuration<ConfigurationObject> {

    private final ConfigurationObject container;

    public JsonConfiguration(ConfigurationObject container) {
      this.container = container;
    }

    @Override
    public String toString() {
      return container.toString();
    }

    @Override
    public List<ConfigurationObject> getTenants() {
      return childrenOf(container, "tenants");
    }

    private List<ConfigurationObject> childrenOf(ConfigurationObject parent, String collection) {
      return parent.getChildObjects().get(collection);
    }

    @Override
    public List<ConfigurationObject> getApplications(ConfigurationObject tenant) {
      return childrenOf(tenant, "applications");
    }

    @Override
    public List<ConfigurationObject> getSearches(ConfigurationObject application) {
      return childrenOf(application, "searches");
    }

  }

}
