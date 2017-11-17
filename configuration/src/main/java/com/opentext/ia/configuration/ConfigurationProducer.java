/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Produce a configuration.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <T> The type of configuration to produce
 */
@FunctionalInterface
public interface ConfigurationProducer<T> {

  Configuration<T> produce(ConfigurationObject container);

}
