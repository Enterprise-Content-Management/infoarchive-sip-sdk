/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import com.opentext.ia.sdk.support.io.EncodedHash;
import com.opentext.ia.test.TestCase;


public class WhenWorkingWithPackingInformation extends TestCase {

  @Test
  public void shouldAcceptSetValues() {
    DataSubmissionSession dss = mock(DataSubmissionSession.class);
    Date productionDate = mock(Date.class);
    int sequenceNumber = randomInt(0, Integer.MAX_VALUE);
    boolean isLast = true;
    long aiuCount = randomInt(0, Integer.MAX_VALUE);
    long pageCount = randomInt(0, Integer.MAX_VALUE);
    Optional<EncodedHash> pdiHash = Optional.empty();
    PackagingInformation pi =
        new PackagingInformation(dss, productionDate, sequenceNumber, isLast, aiuCount, pageCount, pdiHash);

    long newAiuCount = randomInt(0, Integer.MAX_VALUE);
    pi.setAiuCount(newAiuCount);
    assertEquals("AIU Count is changed.", newAiuCount, pi.getAiuCount());

    long newPageCount = randomInt(0, Integer.MAX_VALUE);
    pi.setPageCount(newPageCount);
    assertEquals("Page Count is changed.", newPageCount, pi.getPageCount());

  }
}
