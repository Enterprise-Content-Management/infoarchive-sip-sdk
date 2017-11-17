/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.List;


/**
 * Configuration of an InfoArchive installation.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <T> The type of configuration to build
 */
public interface Configuration<T> {

  /**
   * Returns the first configured tenant.
   * @return The first configured tenant
   * @throws IllegalArgumentException when no tenants are configured
   */
  default T getTenant() {
    return first(getTenants());
  }

  /**
   * Returns the first item.
   * @param items The available items
   * @return The first item
   * @throws IllegalArgumentException when no items are available
   */
  default T first(List<T> items) {
    if (items.isEmpty()) {
      throw new IllegalArgumentException("Missing item");
    }
    return items.get(0);
  }

  /**
   * Returns all the configured tenants.
   * @return All the configured tenants
   */
  List<T> getTenants();


  /**
   * Returns the first configured application for the first tenant.
   * @return The first configured application for the first tenant
   * @throws IllegalArgumentException when no tenants are configured or when no applications are configured for the
   * first tenant
   */
  default T getApplication() {
    return getApplication(getTenant());
  }

  /**
   * Returns the first configured application for the given tenant.
   * @param tenant The tenant that owns the application
   * @return The first configured application for the given tenant
   * @throws IllegalArgumentException when no applications are configured for the given tenant
   */
  default T getApplication(T tenant) {
    return first(getApplications(tenant));
  }

  /**
   * Returns all configured applications for the given tenant.
   * @param tenant The tenant that owns the applications
   * @return All configured applications for the given tenant
   */
  List<T> getApplications(T tenant);


  /**
   * Returns the first configured search for the first application of the first tenant.
   * @return The first configured search for the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant or when no searches are configured for the first application of the first tenant
   */
  default T getSearch() {
    return first(getSearches());
  }

  /**
   * Returns all configured searches for the first application of the first tenant.
   * @return All configured searches for the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant
   */
  default List<T> getSearches() {
    return getSearches(getApplication());
  }

  /**
   * Returns all configured searches for the given application.
   * @param application The application that owns the searches
   * @return All configured searches for the given application
   */
  List<T> getSearches(T application);

}
