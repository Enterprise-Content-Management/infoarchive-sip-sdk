/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;

public class WhenHashing extends TestCase {

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
    assertEquals("Algorithm", "SHA-1", encodedHash.getHashFunction());
    assertEquals("Encoding", "base64", encodedHash.getEncoding());
    assertArrayEquals("Digest", MessageDigest.getInstance("SHA-1")
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
