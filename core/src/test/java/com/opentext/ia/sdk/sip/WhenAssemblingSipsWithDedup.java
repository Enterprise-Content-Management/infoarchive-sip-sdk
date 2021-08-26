/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.io.DataBuffer;
import com.opentext.ia.sdk.support.io.MemoryBuffer;
import com.opentext.ia.sdk.support.io.SingleHashAssembler;
import com.opentext.ia.sdk.support.test.validation.SipFileValidator;


class WhenAssemblingSipsWithDedup extends XmlTestCase {

  private static final String OBJECT_ID_5 = "file5";
  private static final String OBJECT_ID_4 = "file4";
  private static final String OBJECT_ID_3 = "file3";
  private static final String OBJECT_ID_2 = "file2";
  private static final String OBJECT_ID_1 = "file1";
  private static final String CONTENT_5 = "file5.txt";
  private static final String CONTENT_4 = "file4.txt";
  private static final String CONTENT_3 = "file3.txt";
  private static final String CONTENT_2 = "file2.txt";
  private static final String CONTENT_1 = "file1.txt";
  private DigitalObjectsExtraction<TestObject> contentsExtraction;
  private PackagingInformation prototype;
  private PdiAssembler<TestObject> pdiAssembler;
  private List<TestObject> domainObjects;
  private Map<String, String> contentIdToResourceName;
  private DataBuffer buffer;

  static class TestObject {

    private final String id;
    private final List<String> contentId;

    TestObject(String id, List<String> contentId) {
      this.id = requireNonNull(id);
      this.contentId = requireNonNull(contentId);
    }

    public String getId() {
      return id;
    }

    public List<String> getContentId() {
      return contentId;
    }

  }

  @BeforeEach
  public void before() {
    buffer = new MemoryBuffer();
    pdiAssembler = new XmlPdiAssembler<TestObject>(URI.create("test"), "objects") {
      @Override
      protected void doAdd(TestObject domainObject, Map<String, ContentInfo> contentInfo) {
        getBuilder().element("object")
          .element("id", domainObject.getId())
          .elements("contents", "content", domainObject.getContentId(),
              (cid, builder) -> builder.attribute("cid", contentInfo.get(cid)
                .getReferenceInformation()));
      }
    };
    prototype = PackagingInformation.builder()
      .dss()
      .application("app")
      .schema("test")
      .holding("holding")
      .entity("entity")
      .end()
      .build();

    // default mapping
    contentIdToResourceName = new HashMap<>();
    contentIdToResourceName.put(OBJECT_ID_1, CONTENT_1);
    contentIdToResourceName.put(OBJECT_ID_2, CONTENT_2);
    contentIdToResourceName.put(OBJECT_ID_3, CONTENT_3);
    contentIdToResourceName.put(OBJECT_ID_4, CONTENT_4);
    contentIdToResourceName.put(OBJECT_ID_5, CONTENT_5);

    contentsExtraction = t -> t.getContentId()
      .stream()
      .map(cid -> DigitalObject.fromResource(cid, getClass(), contentIdToResourceName.get(cid)))
      .collect(Collectors.toList())
      .iterator();

  }

  private List<TestObject> objects(String... ids) {
    List<TestObject> objects = new ArrayList<>(ids.length);
    for (String id : ids) {
      objects.add(object(id, id));
    }
    return objects;
  }

  private List<TestObject> objects(TestObject... objects) {
    return Arrays.asList(objects);
  }

  private TestObject object(String id) {
    return object(id, id);
  }

  private TestObject object(String id, String cid) {
    return new TestObject(id, Collections.singletonList(cid));
  }

  private SipFileValidator sip(ContentAssembler<TestObject> contentAssembler) throws IOException {

    return new SipFileValidator(this,
        new FileGenerator<TestObject>(sipAssembler(contentAssembler)).generate(domainObjects)
          .getFile());

  }

  private SipAssembler<TestObject> sipAssembler(ContentAssembler<TestObject> contentAssembler) {
    return SipAssembler.forPdiAndContent(prototype, pdiAssembler, contentAssembler);
  }

  @Test
  void withNoDedupShouldIncludeEachCopyWhenThereAreNoDuplicates() throws IOException {
    domainObjects = objects(OBJECT_ID_1, OBJECT_ID_2, OBJECT_ID_3, OBJECT_ID_4, OBJECT_ID_5);

    sip(ContentAssembler.noDedup(contentsExtraction)).assertFileCount(2 + 5)
      .assertPackagingInformation(5)
      .assertContentFileIdenticalTo(OBJECT_ID_1, CONTENT_1)
      .assertContentFileIdenticalTo(OBJECT_ID_2, CONTENT_2)
      .assertContentFileIdenticalTo(OBJECT_ID_3, CONTENT_3)
      .assertContentFileIdenticalTo(OBJECT_ID_4, CONTENT_4)
      .assertContentFileIdenticalTo(OBJECT_ID_5, CONTENT_5);
  }

  @Test
  void withNoDedupShouldIncludeEachCopyWhenThereAreDuplicates() throws IOException {
    domainObjects = objects(OBJECT_ID_1, OBJECT_ID_2, OBJECT_ID_3, OBJECT_ID_4, OBJECT_ID_5);
    contentIdToResourceName.put(OBJECT_ID_3, CONTENT_1);
    contentIdToResourceName.put(OBJECT_ID_5, CONTENT_1);

    sip(ContentAssembler.noDedup(contentsExtraction)).assertFileCount(2 + 5)
      .assertPackagingInformation(5)
      .assertContentFileIdenticalTo(OBJECT_ID_1, CONTENT_1)
      .assertContentFileIdenticalTo(OBJECT_ID_2, CONTENT_2)
      .assertContentFileIdenticalTo(OBJECT_ID_3, CONTENT_1)
      .assertContentFileIdenticalTo(OBJECT_ID_4, CONTENT_4)
      .assertContentFileIdenticalTo(OBJECT_ID_5, CONTENT_1);
  }

  @Test
  void withDedupOnRiShouldIncludeEachCopyWhereThereAreNoDuplicatesEvenIfContentIsSame() throws IOException {
    domainObjects = objects(OBJECT_ID_1, OBJECT_ID_2, OBJECT_ID_3, OBJECT_ID_4, OBJECT_ID_5);
    contentIdToResourceName.put(OBJECT_ID_3, CONTENT_1);
    contentIdToResourceName.put(OBJECT_ID_5, CONTENT_1);

    sip(ContentAssembler.withDedupOnRi(contentsExtraction)).assertFileCount(2 + 5)
      .assertPackagingInformation(5)
      .assertContentFileIdenticalTo(OBJECT_ID_1, CONTENT_1)
      .assertContentFileIdenticalTo(OBJECT_ID_2, CONTENT_2)
      .assertContentFileIdenticalTo(OBJECT_ID_3, CONTENT_1)
      .assertContentFileIdenticalTo(OBJECT_ID_4, CONTENT_4)
      .assertContentFileIdenticalTo(OBJECT_ID_5, CONTENT_1);
  }

  @Test
  void withDedupOnRiShouldIncludeOnlyUniqueRi() throws IOException {
    domainObjects = objects(object(OBJECT_ID_1), object(OBJECT_ID_2), object(OBJECT_ID_3, OBJECT_ID_1),
        object(OBJECT_ID_4), object(OBJECT_ID_5, OBJECT_ID_1));

    sip(ContentAssembler.withDedupOnRi(contentsExtraction)).assertFileCount(2 + 3)
      .assertPackagingInformation(5)

      .assertContentFileIdenticalTo(OBJECT_ID_1, CONTENT_1)
      .assertContentFileIdenticalTo(OBJECT_ID_2, CONTENT_2)
      .assertContentFileIdenticalTo(OBJECT_ID_4, CONTENT_4);
  }

  @Test
  void withDedupOnRiAndValidationErrorOnDifferentRISameContentShouldThrowException() throws IOException {
    TestObject object1 = object(OBJECT_ID_1);
    TestObject object2 = object(OBJECT_ID_2);
    contentIdToResourceName.put(OBJECT_ID_2, CONTENT_1);

    SipAssembler<TestObject> sipAssembler = sipAssembler(
        ContentAssembler.withDedupOnRiAndValidation(contentsExtraction, new SingleHashAssembler(), false, true));
    sipAssembler.start(buffer);
    sipAssembler.add(object1);
    assertThrows(IllegalStateException.class, () -> sipAssembler.add(object2));
  }

  @Test
  void withDedupOnRiAndValidationErrorOnSameRIDifferentContntShouldThrowException() throws IOException {
    TestObject object1 = object(OBJECT_ID_1);
    TestObject object2 = object(OBJECT_ID_2, OBJECT_ID_1);

    SipAssembler<TestObject> sipAssembler = sipAssembler(
        ContentAssembler.withDedupOnRiAndValidation(contentsExtraction, new SingleHashAssembler(), true, false));
    sipAssembler.start(buffer);
    sipAssembler.add(object1);
    // Swap so file1 now references another content
    contentIdToResourceName.put(OBJECT_ID_1, CONTENT_2);
    assertThrows(IllegalStateException.class, () -> sipAssembler.add(object2));
  }

  @Test
  void withDedupOnHashShouldIncludeOnlyUniqueContent() throws IOException {
    domainObjects = objects(OBJECT_ID_1, OBJECT_ID_2, OBJECT_ID_3, OBJECT_ID_4, OBJECT_ID_5);
    contentIdToResourceName.put(OBJECT_ID_3, CONTENT_1);
    contentIdToResourceName.put(OBJECT_ID_5, CONTENT_1);

    sip(ContentAssembler.withDedupOnHash(contentsExtraction, new SingleHashAssembler())).assertFileCount(2 + 3)
      .assertPackagingInformation(5)
      .assertContentFileIdenticalTo(OBJECT_ID_1, CONTENT_1)
      .assertContentFileIdenticalTo(OBJECT_ID_2, CONTENT_2)
      .assertContentFileIdenticalTo(OBJECT_ID_4, CONTENT_4);
  }
}
