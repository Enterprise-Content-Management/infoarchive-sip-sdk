/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Function for extracting a number of related {@linkplain DigitalObject}s from a source.
 * @param <S> The type of source to extract from
 */
@FunctionalInterface
public interface DigitalObjectsExtraction<S> extends Function<S, Iterator<? extends DigitalObject>> {

}
