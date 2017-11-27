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
   * Returns the first configured spaceRootFolder for the given space.
   * @param space The owner of the spaceRootFolder
   * @return The first configured spaceRootFolder for the given space
   * @throws IllegalArgumentException when no spaceRootFolders are configured for the given space
   */
  default T getSpaceRootFolder(T space) {
    return first(getSpaceRootFolders(space));
  }

  /**
   * Returns all configured spaceRootFolders for the given space.
   * @param space The space that owns the spaceRootFolders
   * @return All configured spaceRootFolders for the given space
   */
  List<T> getSpaceRootFolders(T space);


  /**
   * Returns the first configured spaceRootXdbLibrary for the given space.
   * @param space The owner of the spaceRootXdbLibrary
   * @return The first configured spaceRootXdbLibrary for the given space
   * @throws IllegalArgumentException when no spaceRootXdbLibraries are configured for the given space
   */
  default T getSpaceRootXdbLibrary(T space) {
    return first(getSpaceRootXdbLibraries(space));
  }

  /**
   * Returns all configured spaceRootXdbLibraries for the given space.
   * @param space The space that owns the spaceRootXdbLibraries
   * @return All configured spaceRootXdbLibraries for the given space
   */
  List<T> getSpaceRootXdbLibraries(T space);


  /**
   * Returns The first configured xdbLibrary for the given spaceRootXdbLibrary.
   * @param spaceRootXdbLibrary The spaceRootXdbLibrary that owns the xdbLibraries
   * @param spaceRootXdbLibrary The spaceRootXdbLibrary that owns the xdbLibraries
   * @return The first configured xdbLibrary for the given spaceRootXdbLibrary
   * @throws IllegalArgumentException when no xdbLibraries are configured
   * for the given spaceRootXdbLibrary
   */
  default T getXdbLibrary(T spaceRootXdbLibrary) {
    return first(getXdbLibraries(spaceRootXdbLibrary));
  }

  /**
   * Returns all configured xdbLibraries for the given spaceRootXdbLibrary.
   * @param spaceRootXdbLibrary The spaceRootXdbLibrary that owns the xdbLibraries
   * @return All configured xdbLibraries for the given spaceRootXdbLibrary
   */
  List<T> getXdbLibraries(T spaceRootXdbLibrary);


  /**
   * Returns the first configured pdi schema for the given application.
   * @param application The owner of the pdi schema
   * @return The first configured pdi schema for the given application
   * @throws IllegalArgumentException when no pdi schemas are configured for the given application
   */
  default T getPdiSchema(T application) {
    return first(getPdiSchemas(application));
  }

  /**
   * Returns all configured pdi schemas for the given application.
   * @param application The owner of the pdi schemas
   * @return All configured pdi schemas for the given application
   * @throws IllegalArgumentException when no pdi schemas are configured for the given application
   */
  List<T> getPdiSchemas(T application);


  /**
   * Returns the first configured holding for the first application of the first tenant.
   * @return The first configured holding for the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant or when no holdings are configured for the first application of the first tenant
   */
  default T getHolding() {
    return first(getHoldings());
  }

  /**
   * Returns all configured holdings for the first application of the first tenant.
   * @return All configured holdings for the first application of the first tenant
   * @throws IllegalArgumentException when no tenants are configured or no applications are configured for the first
   * tenant
   */
  default List<T> getHoldings() {
    return getHoldings(getApplication());
  }

  /**
   * Returns all configured holdings for the given application.
   * @param application The application that owns the holdings
   * @return All configured holdings for the given application
   */
  List<T> getHoldings(T application);

}
