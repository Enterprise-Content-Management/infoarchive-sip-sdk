/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


@FunctionalInterface
public interface ConfigurationProducer<T> {

  Configuration<T> produce(ConfigurationObject container);

}
