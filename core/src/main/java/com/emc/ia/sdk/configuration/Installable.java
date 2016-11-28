package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.support.rest.RestClient;

public interface Installable {
  void install(RestClient client, IACache cache);
}
