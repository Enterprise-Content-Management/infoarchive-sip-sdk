/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.assembly;

import java.util.Optional;

import com.opentext.ia.sdk.support.io.EncodedHash;

/**
 * <a href="http://c2.com/cgi/wiki?AbstractFactoryPattern">Factory</a> for creating {@linkplain PackagingInformation}.
 */
public interface PackagingInformationFactory {

  /**
   * Create Packaging Information.
   * @param aiuCount The number of Archival Information Units (AIUs)
   * @param pdiHash Optional encoded hash of the Preservation Description Information (PDI)
   * @return The newly created Packaging Information
   */
  PackagingInformation newInstance(long aiuCount, Optional<EncodedHash> pdiHash);

  /**
   * Indicate whether the Data Submission Session (DSS) is about to end.
   * @param finalSipInDss If <code>true</code>, the next SIP will be the last in the DSS
   */
  void setFinalSipInDss(boolean finalSipInDss);

}
