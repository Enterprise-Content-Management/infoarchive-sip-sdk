package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Tenant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the cached Infoarchive Application. It also helps with finding defaults for referencing
 */

public final class IACache {

  /*
      ???: Maybe to change both hash maps to single Multimap???
   */
  private HashMap<Class<?>, List> collections = new HashMap<>();
  private HashMap<Class<?>, Object> singles = new HashMap<>();

  public IACache() {
    singles.put(Services.class, null);
    singles.put(Tenant.class, null);
    singles.put(Application.class, null);
    singles.put(Federation.class, null);
    singles.put(FileSystemRoot.class, null);
  }

  @SuppressWarnings("unchecked")
  public <T extends NamedLinkContainer> List<T> getAll(Class<T> token) {
    if (singles.containsKey(token)) {
      return Collections.singletonList((T) singles.get(token));
    } else if (collections.containsKey(token)) {
      return collections.get(token);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T extends NamedLinkContainer> void cacheOne(T object) {
    if (singles.containsKey(object.getClass())) {
      singles.put(object.getClass(), object);
    } else {
      List<T> collection = collections.get(object.getClass());
      if (collection == null) {
        collection = new ArrayList<>();
        collections.put(object.getClass(), collection);
      }
      collection.add(object);// TODO: Should check for existence of the object with the same name???
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends NamedLinkContainer> T getByClassWithName(Class<T> token, String name) {
    if (singles.containsKey(token)) {
      T object = (T) singles.get(token);
      if (object.getName().equals(name)) {
        return object;
      }
    } else if (collections.containsKey(token)) {
      List<T> collection = collections.get(token);
      for (T object : collection) {
        if (object.getName().equals(name)) {
          return object;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T extends NamedLinkContainer> T getFirst(Class<T> token) {
    if (singles.containsKey(token)) {
      return (T) singles.get(token);
    } else if (collections.containsKey(token)) {
      List<T> collection = collections.get(token);
      return collection.get(0);
    }
    return null;
  }
}
