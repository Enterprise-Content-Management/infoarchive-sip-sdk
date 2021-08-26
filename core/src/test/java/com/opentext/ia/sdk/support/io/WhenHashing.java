/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;


class WhenHashing extends TestCase {

  private static final String EXPECTED_HASH_FUNCTION = "SHA-256";
  private final HashAssembler hashAssembler = new SingleHashAssembler();

  @Test
  void shouldEncodeDigestOfContent() throws NoSuchAlgorithmException {
    byte[] content = randomBytes();

    hashAssembler.initialize();
    hashAssembler.add(content, content.length);
    Collection<EncodedHash> actual = hashAssembler.get();

    assertEquals(1, actual.size(), "# hashes");
    EncodedHash encodedHash = actual.iterator()
      .next();
    assertEquals(EXPECTED_HASH_FUNCTION, encodedHash.getHashFunction(), "Algorithm");
    assertEquals("base64", encodedHash.getEncoding(), "Encoding");
    assertArrayEquals(MessageDigest.getInstance(EXPECTED_HASH_FUNCTION).digest(content),
        Base64.decodeBase64(encodedHash.getValue()), "Digest");
  }

  @Test
  void shouldCountNumberOfHashedBytes() {
    hashAssembler.initialize();
    assertEquals(0, hashAssembler.numBytesHashed(), "Initial size");

    byte[] content = randomBytes();
    hashAssembler.add(content, content.length);
    assertEquals(content.length, hashAssembler.numBytesHashed(), "Size after hashing");

    hashAssembler.initialize();
    assertEquals(0, hashAssembler.numBytesHashed(), "Size after initializing");
  }

}
