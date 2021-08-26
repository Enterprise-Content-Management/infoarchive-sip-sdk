/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import com.opentext.ia.sdk.support.io.ByteArrayInputOutputStream;
import com.opentext.ia.sdk.support.io.DataBuffer;
import com.opentext.ia.sdk.support.io.EncodedHash;
import com.opentext.ia.sdk.support.io.Encoding;
import com.opentext.ia.sdk.support.io.HashAssembler;
import com.opentext.ia.sdk.support.io.HashFunction;
import com.opentext.ia.sdk.support.io.MemoryBuffer;
import com.opentext.ia.sdk.support.io.NoHashAssembler;
import com.opentext.ia.sdk.support.xml.XmlUtil;

@SuppressWarnings("unchecked")
public class WhenAssemblingSips extends XmlTestCase {

  private static final float DELTA_MS = 10f;

  @Test
  public void shouldZipContentsAndReportMetrics() throws IOException {
    Assembler<HashedContents<Object>> pdiAssembler = mock(Assembler.class);
    HashAssembler pdiHashAssembler = mock(HashAssembler.class);
    EncodedHash hash = someHash();
    when(pdiHashAssembler.get()).thenReturn(Collections.singletonList(hash));
    long pdiSize = randomInt(7, 128);
    when(pdiHashAssembler.numBytesHashed()).thenReturn(pdiSize);
    Object object1 = "object1_" + randomString(8);
    Object object2 = "object2_" + randomString(8);
    Collection<Object> objects = Arrays.asList(object1, object2);
    DigitalObjectsExtraction<Object> contentsExtraction = mock(DigitalObjectsExtraction.class);
    String id1a = randomString(8);
    String id1b = randomString(8);
    String id2 = randomString(8);
    List<? extends DigitalObject> digitalObjects1 =
        Arrays.asList(someContentDataObject(id1a), someContentDataObject(id1b));
    List<? extends DigitalObject> digitalObjects2 =
        Collections.singletonList(someContentDataObject(id2));
    when(contentsExtraction.apply(object1)).thenAnswer(invocation -> digitalObjects1.iterator());
    when(contentsExtraction.apply(object2)).thenAnswer(invocation -> digitalObjects2.iterator());
    HashAssembler contentHashAssembler = mock(HashAssembler.class);
    Collection<EncodedHash> hashes1a = Collections.singletonList(someHash());
    Collection<EncodedHash> hashes1b = Collections.singletonList(someHash());
    Collection<EncodedHash> hashes2 = Collections.singletonList(someHash());
    Iterator<Collection<EncodedHash>> hashes =
        Arrays.asList(hashes1a, hashes1b, hashes2).iterator();
    when(contentHashAssembler.get()).thenAnswer(invocation -> hashes.next());
    long digitalObjectSize = randomInt(5, 255);
    when(contentHashAssembler.numBytesHashed()).thenReturn(digitalObjectSize);
    Map<String, ContentInfo> hashesById1 = new HashMap<>();
    hashesById1.put(id1a, new ContentInfo(id1a, hashes1a));
    hashesById1.put(id1b, new ContentInfo(id1b, hashes1b));
    Map<String, ContentInfo> hashesById2 =
        Collections.singletonMap(id2, new ContentInfo(id2, hashes2));
    PackagingInformation packagingInformationPrototype = somePackagingInformation();
    SipAssembler<Object> sipAssembler =
        SipAssembler.forPdiAndContentWithHashing(packagingInformationPrototype, pdiAssembler,
            pdiHashAssembler, contentsExtraction, contentHashAssembler);
    DataBuffer buffer = new MemoryBuffer();

    long time = System.currentTimeMillis();
    sipAssembler.start(buffer);
    for (Object object : objects) {
      sipAssembler.add(object);
    }
    sipAssembler.end();
    time = System.currentTimeMillis() - time;

    verify(pdiAssembler).start(any(DataBuffer.class));
    verify(pdiAssembler).add(eq(new HashedContents<>(object1, hashesById1)));
    verify(pdiAssembler).add(eq(new HashedContents<>(object2, hashesById2)));
    verify(pdiAssembler).end();
    try (ZipInputStream zip = new ZipInputStream(buffer.openForReading())) {
      assertContentDataObject(zip, id1a);
      assertContentDataObject(zip, id1b);
      assertContentDataObject(zip, id2);
      assertPreservationDescriptionInformation(zip);
      assertPackagingInformation(zip, objects, hash);
      assertNull(zip.getNextEntry(), "Additional zip entries");
    }

    SipMetrics metrics = sipAssembler.getMetrics();
    assertEquals(2, metrics.numAius(), SipMetrics.NUM_AIUS);
    assertEquals(3, metrics.numDigitalObjects(), SipMetrics.NUM_DIGITAL_OBJECTS);
    assertEquals(time, metrics.assemblyTime(), DELTA_MS, SipMetrics.ASSEMBLY_TIME);
    assertEquals(3 * digitalObjectSize, metrics.digitalObjectsSize(),
        SipMetrics.SIZE_DIGITAL_OBJECTS);
    assertEquals(pdiSize, metrics.pdiSize(), SipMetrics.SIZE_PDI);
    long packagingInformationSize =
        getPackagingInformationSize(packagingInformationPrototype, 2, Optional.of(hash));
    assertEquals(pdiSize + 3 * digitalObjectSize + packagingInformationSize, metrics.sipSize(),
        SipMetrics.SIZE_SIP);
    assertEquals(buffer.length(), metrics.sipFileSize(), SipMetrics.SIZE_SIP_FILE);
  }

  private long getPackagingInformationSize(PackagingInformation packagingInformationPrototype,
      long numAius, Optional<EncodedHash> pdiHash) throws IOException {
    InfoArchivePackagingInformationAssembler packagingInformationAssembler =
        new InfoArchivePackagingInformationAssembler();
    DataBuffer buffer = new MemoryBuffer();
    packagingInformationAssembler.start(buffer);
    packagingInformationAssembler
        .add(new DefaultPackagingInformationFactory(packagingInformationPrototype)
            .newInstance(numAius, pdiHash));
    packagingInformationAssembler.end();
    return buffer.length();
  }

  private PackagingInformation somePackagingInformation() {
    return PackagingInformation.builder().dss().application(randomString(64))
        .holding(randomString(64)).schema(randomString(256)).entity(randomString(64)).end().build();
  }

  private DigitalObject someContentDataObject(String id) {
    DigitalObject result = mock(DigitalObject.class);
    when(result.getReferenceInformation()).thenReturn(id);
    when(result.get()).thenReturn(new ByteArrayInputStream(randomBytes()));
    return result;
  }

  private EncodedHash someHash() {
    return new EncodedHash(someHashAlgorithm(), someEncoding(), randomString(32));
  }

  private String someHashAlgorithm() {
    return HashFunction.values()[randomInt(HashFunction.values().length)].toString();
  }

  private String someEncoding() {
    return Encoding.values()[randomInt(Encoding.values().length)].toString();
  }

  private void assertContentDataObject(ZipInputStream zip, String id) throws IOException {
    ZipEntry entry = zip.getNextEntry();
    assertNotNull(entry, "Missing content: " + id);
    assertEquals(id, entry.getName(), "Zip entry");

    zip.closeEntry();
  }

  private void assertPreservationDescriptionInformation(ZipInputStream zip) throws IOException {
    ZipEntry entry = zip.getNextEntry();
    assertNotNull(entry, "Missing PDI");
    assertEquals("eas_pdi.xml", entry.getName(), "Zip entry");

    zip.closeEntry();
  }

  private void assertPackagingInformation(ZipInputStream zip, Collection<Object> objects,
      EncodedHash pdiHash) throws IOException {
    ZipEntry entry = zip.getNextEntry();
    assertNotNull(entry, "Missing Packaging Information");
    assertEquals("eas_sip.xml", entry.getName(), "Zip entry");

    try (ByteArrayInputOutputStream packagingInformation = new ByteArrayInputOutputStream()) {
      IOUtils.copy(zip, packagingInformation);
      Element sipElement =
          assertValidXml(packagingInformation.getInputStream(), "PackagingInformation", "sip.xsd")
              .getDocumentElement();
      assertTrue(XmlUtil.namedElementsIn(sipElement, "pdi_hash").filter(e -> equals(pdiHash, e))
          .findAny().isPresent(), "Missing pdi_hash: " + pdiHash);
      String aiuCount = XmlUtil.getFirstChildElement(sipElement, "aiu_count").getTextContent();
      assertEquals(objects.size(), Integer.parseInt(aiuCount), "# AIUs");
    }
    zip.closeEntry();
  }

  private boolean equals(EncodedHash encodedHash, Element hashElement) {
    return encodedHash.getHashFunction().equals(hashElement.getAttributeNS(null, "algorithm"))
        && encodedHash.getEncoding().equals(hashElement.getAttributeNS(null, "encoding"))
        && encodedHash.getValue().equals(hashElement.getTextContent());
  }

  @Test
  public void shouldMeasurePdiSizeBeforeEnd() throws IOException {
    long pdiSize = randomInt(13, 313);
    DataBuffer pdiBuffer = new MemoryBuffer() {

      @Override
      public long length() {
        return pdiSize;
      }
    };
    HashAssembler noHashAssembler = new NoHashAssembler();
    Assembler<HashedContents<Object>> pdiAssembler = mock(Assembler.class);
    SipAssembler<Object> sipAssembler =
        new SipAssembler<>(new DefaultPackagingInformationFactory(somePackagingInformation()),
            pdiAssembler, noHashAssembler, () -> pdiBuffer,
            ContentAssembler.noDedup(domainObject -> Collections.emptyIterator(), noHashAssembler));

    sipAssembler.start(new MemoryBuffer());
    sipAssembler.add(new Object());

    SipMetrics metrics = sipAssembler.getMetrics();
    assertEquals(pdiSize, metrics.pdiSize(), SipMetrics.SIZE_PDI);
  }

}
