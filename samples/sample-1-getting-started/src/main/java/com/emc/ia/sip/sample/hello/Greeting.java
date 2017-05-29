/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sip.sample.hello;


/**
 *  Our domain object that holds the data to be archived.
 */
public class Greeting {

  private final String message;

  public Greeting(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}
