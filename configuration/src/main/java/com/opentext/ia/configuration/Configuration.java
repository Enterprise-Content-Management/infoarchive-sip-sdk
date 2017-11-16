/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.List;


public interface Configuration<T> {

  default T getTenant() {
    return getTenants().get(0);
  }

  List<T> getTenants();


  default T getApplication() {
    return getApplication(getTenant());
  }

  default T getApplication(T tenant) {
    return getApplications(tenant).get(0);
  }

  List<T> getApplications(T tenant);


  default T getSearch() {
    return getSearches().get(0);
  }

  default List<T> getSearches() {
    return getSearches(getApplication());
  }

  List<T> getSearches(T application);

}
