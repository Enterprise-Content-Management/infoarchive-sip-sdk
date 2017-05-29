/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.test.validation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import com.emc.ia.sdk.support.io.ByteArrayInputOutputStream;
import com.emc.ia.sdk.support.io.RepeatableInputStream;
import com.emc.ia.sdk.support.xml.XmlUtil;

public class SipFileValidator {

  private final Object owner;
  private final ZipFile zipFile;

  public SipFileValidator(Object owner, File file) throws ZipException, IOException {
    this.owner = owner;
    this.zipFile = new ZipFile(file);
  }

  public SipFileValidator assertFileCount(int count) {
    assertEquals("Entries in sip file.", count, zipFile.size());
    return this;
  }

  public SipFileValidator assertPackagingInformation(int aiuCount) throws IOException {
    ZipEntry entry = zipFile.getEntry("eas_sip.xml");
    Assert.assertNotNull("Missing Packaging Information", entry);

    try (ByteArrayInputOutputStream packagingInformation = new ByteArrayInputOutputStream();
        InputStream in = zipFile.getInputStream(entry)) {
      IOUtils.copy(in, packagingInformation);
      Element sipElement =
          assertValidXml(packagingInformation.getInputStream(), "PackagingInformation", "sip.xsd").getDocumentElement();
      String actualAiuCount = XmlUtil.getFirstChildElement(sipElement, "aiu_count")
        .getTextContent();
      assertEquals("# AIUs", aiuCount, Integer.parseInt(actualAiuCount));
    }

    return this;
  }

  public SipFileValidator assertPreservationInformationIdenticalTo(String resource) throws IOException {
    ZipEntry entry = zipFile.getEntry("eas_pdi.xml");
    Assert.assertNotNull("Missing Preservation Information", entry);

    try (InputStream expectedIn = owner.getClass()
      .getResourceAsStream(resource); InputStream actualIn = zipFile.getInputStream(entry)) {

      Diff myDiff = DiffBuilder.compare(Input.fromStream(expectedIn))
        .withTest(Input.fromStream(actualIn))
        .checkForSimilar()
        .ignoreWhitespace()
        .ignoreComments()
        .build();
      Assert.assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());

    }
    return this;

  }

  protected Document assertValidXml(InputStream stream, String humanFriendlyDocumentType, String schema)
      throws IOException {
    RepeatableInputStream repetableStream = new RepeatableInputStream(stream);
    XmlUtil.validate(repetableStream.get(), owner.getClass()
      .getResourceAsStream("/" + schema), humanFriendlyDocumentType);
    return XmlUtil.parse(repetableStream.get());
  }

  public SipFileValidator assertContentFileIdenticalTo(String referenceInformation, String resource)
      throws IOException {
    ZipEntry entry = zipFile.getEntry(referenceInformation);
    Assert.assertNotNull("Missing Digital Object", entry);
    try (InputStream expectedIn = owner.getClass()
      .getResourceAsStream(resource); InputStream actualIn = zipFile.getInputStream(entry)) {
      Assert.assertTrue("The DigitalObject " + referenceInformation + " is not the expected one.",
          IOUtils.contentEquals(expectedIn, actualIn));
    }
    return this;
  }

}
