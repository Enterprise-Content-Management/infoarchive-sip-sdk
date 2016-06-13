/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.assembly;


/**
 * Metrics about the {@linkplain SipAssembler SIP assembly process}.
 */
public class SipMetrics implements Metrics {

  static final String NUM_AIUS = "# AIUs";
  static final String NUM_DIGITAL_OBJECTS = "# digitial objects";
  static final String SIZE_DIGITAL_OBJECTS = "size of digital objects";
  static final String SIZE_PDI = "size of PDI";
  static final String SIZE_SIP = "size of SIP";
  static final String ASSEMBLY_TIME = "time to assemble (ms)";

  private final Counters counters;

  SipMetrics(Counters metrics) {
    this.counters = metrics;
  }

  public long numAius() {
    return counters.get(NUM_AIUS);
  }

  public long numDigitalObjects() {
    return counters.get(NUM_DIGITAL_OBJECTS);
  }

  public long digitalObjectsSize() {
    return counters.get(SIZE_DIGITAL_OBJECTS);
  }

  public long pdiSize() {
    return counters.get(SIZE_PDI);
  }

  public long sipSize() {
    return counters.get(SIZE_SIP);
  }

  public long assemblyTime() {
    return counters.get(ASSEMBLY_TIME);
  }

  @Override
  public String toString() {
    return counters.toString();
  }

}
