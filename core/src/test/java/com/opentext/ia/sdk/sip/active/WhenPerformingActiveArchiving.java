/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.active;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.PackagingInformation;
import com.opentext.ia.sdk.sip.SipAssemblingTestCase;
import com.opentext.ia.sdk.sip.SipSegmentationStrategy;
import com.opentext.ia.sdk.sip.XmlPdiAssembler;


class WhenPerformingActiveArchiving extends SipAssemblingTestCase {

  private int numSips;
  private final SipSegmentationStrategy<String> segmentationStrategy = (domainObject, metrics) -> true;
  private final ArchiveClient archiveClient = mock(ArchiveClient.class);
  private ActiveArchiver<String> archiver;
  private final Collection<File> failedSips = new ArrayList<>();

  @BeforeEach
  public void init() {
    numSips = 0;
  }

  @Test
  void shouldIngestSipsAsTheyAreAssembled() throws IOException {
    when(archiveClient.ingestDirect(any())).thenAnswer(invocation -> {
      numSips++;
      String packageInformation = getPackageInformation(invocation.getArgument(0));
      assertNotNull("Missing package information in SIP", packageInformation);
      assertEquals(1, getSeqNo(packageInformation), "SeqNo");
      assertTrue(isLast(packageInformation), "IsLast");
      return null;
    });
    archiver = newArchiver();

    addDomainObjectToArchive();
    addDomainObjectToArchive();
    archiver.end();

    assertTrue(failedSips.isEmpty(), "Failed to ingest SIPs");
  }

  private ActiveArchiver<String> newArchiver() {
    return new ActiveArchiver<>(segmentationStrategy, packagingInformation(), dssPrefix(),
        pdiAssembler(), archiveClient, (failedSip, exception) -> failedSips.add(failedSip));
  }

  private PackagingInformation packagingInformation() {
    return PackagingInformation.builder()
        .dss()
            .holding(randomString(8))
            .application(randomString(8))
            .producer(getClass().getSimpleName())
            .entity(randomString(8))
            .schema(randomString(64))
        .end()
        .build();
  }

  private String dssPrefix() {
    return randomString(3);
  }

  private XmlPdiAssembler<String> pdiAssembler() {
    return new XmlPdiAssembler<String>(URI.create(randomString(16)), randomString(8)) {
      @Override
      protected void doAdd(String domainObject, Map<String, ContentInfo> contentInfo) {
        getBuilder().element("aiu", domainObject);
      }
    };
  }

  private void addDomainObjectToArchive() throws IOException {
    archiver.add(randomString(randomInt(13, 42)));
    verify(archiveClient, times(numSips)).ingestDirect(any());
  }

  @Test
  void shouldListSipsThatFailedToIngest() throws IOException {
    when(archiveClient.ingestDirect(any())).thenAnswer(invocation -> {
      numSips++;
      throw new IOException("Failed to ingest SIP");
    });
    archiver = newArchiver();

    addDomainObjectToArchive();
    addDomainObjectToArchive();
    archiver.end();

    assertEquals(numSips, failedSips.size(), "# Failed SIPs");
  }

}
