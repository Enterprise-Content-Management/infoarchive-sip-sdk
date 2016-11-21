package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;

public interface ObjectConfiguration<T extends NamedLinkContainer> {
  T configure();
  boolean same(T base);
}
