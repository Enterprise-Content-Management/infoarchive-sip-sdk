/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.InputStream;

import javax.validation.ValidationException;


/**
 * Validate that the contents of a stream meets the expectations.
 */
@FunctionalInterface
public interface Validator {

  /**
   * Validate that the contents of a stream meets the expectations.
   * @param stream The stream to validate
   * @throws ValidationException When the stream doesn't meet expectations
   */
  void validate(InputStream stream) throws ValidationException;

}
