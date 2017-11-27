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

  /**
   * Returns the first configured file system root.
   * @return The first configured file system root
   * @throws IllegalArgumentException when no file system roots are configured
   */
  default T getFileSystemRoot() {
    return first(getFileSystemRoots());
  }

  /**
   * Returns all the configured file system roots.
   * @return All the configured file system roots
   */
  List<T> getFileSystemRoots();

  /**
   * Returns the first configured space for the first application of the first tenant.
   * @return The first configured space for the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant or when no spaces are configured for the first application of the first tenant
   */
  default T getSpace() {
    return first(getSpaces());
  }

  /**
   * Returns all configured spaces for the first application of the first tenant.
   * @return All configured spaces for the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant
   */
  default List<T> getSpaces() {
    return getSpaces(getApplication());
  }

  /**
   * Returns all configured spaces for the given application.
   * @param application The application that owns the spaces
   * @return All configured spaces for the given application
   */
  List<T> getSpaces(T application);

  /**
   * Returns the first configured spaceRootXdbLibrary for the first space of the fist application of the first tenant.
   * @return The first configured spaceRootXdbLibrary for the first space of the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant or when no spaces are configured for the first application of the first tenant or not spaceRootXdbLibraries
   * are configured for the first space of the first application of the first tenant
   */
  default T getSpaceRootXdbLibrary() {
    return first(getSpaceRootXdbLibraries());
  }

  /**
   * Returns all configured spaceRootXdbLibraries for the first space of the fist application of the first tenant.
   * @return All configured spaceRootXdbLibraries for the first space of the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant or when no spaces are configured for the first application of the first tenant or not spaceRootXdbLibraries
   * are configured for the first space of the first application of the first tenant
   */
  default List<T> getSpaceRootXdbLibraries() {
    return getSpaceRootXdbLibraries(getSpace());
  }

  /**
   * Returns all configured spaceRootXdbLibraries for the given space.
   * @param space The space that owns the spaceRootXdbLibraries
   * @return All configured spaceRootXdbLibraries for the given space
   */
  List<T> getSpaceRootXdbLibraries(T space);

  /**
   * Returns The first configured xdbLibrary for the first spaceRootXdbLibrary for the first space of the fist
   * application of the first tenant.
   * @return The first configured xdbLibrary for the first spaceRootXdbLibrary for the first space of the first
   * application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant or when no spaces are configured for the first application of the first tenant or not spaceRootXdbLibraries
   * are configured for the first space of the first application of the first tenant or no xdbLibraries are configured
   * for the first spaceRootXdbLibrary of the first space of the first application of the first tenant
   */
  default T getXdbLibrary() {
    return first(getXdbLibraries());
  }

  /**
   * Returns all configured xdbLibraries for the first spaceRootXdbLibrary for the first space of the fist application
   * of the first tenant.
   * @return All configured xdbLibraries for the first spaceRootXdbLibrary for the first space of the first application
   * of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant or when no spaces are configured for the first application of the first tenant or not spaceRootXdbLibraries
   * are configured for the first space of the first application of the first tenant or no xdbLibraries are configured
   * for the first spaceRootXdbLibrary of the first space of the first application of the first tenant
   */
  default List<T> getXdbLibraries() {
    return getXdbLibraries(getSpaceRootXdbLibrary());
  }

  /**
   * Returns all configured xdbLibraries for the given spaceRootXdbLibrary.
   * @param spaceRootXdbLibrary The spaceRootXdbLibrary that owns the xdbLibraries
   * @return All configured xdbLibraries for the given spaceRootXdbLibrary
   */
  List<T> getXdbLibraries(T spaceRootXdbLibrary);

}
