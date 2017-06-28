/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.opentext.ia.test.TestCase;


public class WhenHashing extends TestCase {

  private static final String EXPECTED_HASH_FUNCTION = "SHA-256";
  private final HashAssembler hashAssembler = new SingleHashAssembler();

  @Test
  public void shouldEncodeDigestOfContent() throws NoSuchAlgorithmException {
    byte[] content = randomBytes();

    hashAssembler.initialize();
    hashAssembler.add(content, content.length);
    Collection<EncodedHash> actual = hashAssembler.get();

    assertEquals("# hashes", 1, actual.size());
    EncodedHash encodedHash = actual.iterator()
      .next();
    assertEquals("Algorithm", EXPECTED_HASH_FUNCTION, encodedHash.getHashFunction());
    assertEquals("Encoding", "base64", encodedHash.getEncoding());
    assertArrayEquals("Digest", MessageDigest.getInstance(EXPECTED_HASH_FUNCTION)
      .digest(content), Base64.decodeBase64(encodedHash.getValue()));
  }

  @Test
  public void shouldCountNumberOfHashedBytes() {
    hashAssembler.initialize();
    assertEquals("Initial size", 0, hashAssembler.numBytesHashed());

    byte[] content = randomBytes();
    hashAssembler.add(content, content.length);
    assertEquals("Size after hashing", content.length, hashAssembler.numBytesHashed());

    hashAssembler.initialize();
    assertEquals("Size after initializing", 0, hashAssembler.numBytesHashed());
  }

}
