/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenEncoding extends TestCase {

  private static final Charset CHAR_SET = StandardCharsets.UTF_8;

  @Test
  public void shouldConvertToBase64() {
    String text = randomString();

    String actual = Encoding.BASE64.encode(text.getBytes(CHAR_SET));

    assertEquals("Text", text, new String(Base64.decodeBase64(actual), CHAR_SET));
  }

  @Test
  public void shouldConvertToHex() throws DecoderException {
    String text = randomString();

    String actual = Encoding.HEX.encode(text.getBytes(CHAR_SET));

    assertEquals("Text", text, new String(new Hex().decode(actual.getBytes(CHAR_SET)), CHAR_SET));
  }

}
