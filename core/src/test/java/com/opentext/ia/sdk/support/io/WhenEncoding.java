/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;


class WhenEncoding extends TestCase {

  private static final Charset CHAR_SET = StandardCharsets.UTF_8;

  @Test
  void shouldConvertToBase64() {
    String text = randomString();

    String actual = Encoding.BASE64.encode(text.getBytes(CHAR_SET));

    assertEquals(text, new String(Base64.decodeBase64(actual), CHAR_SET), "Text");
  }

  @Test
  void shouldConvertToHex() throws DecoderException {
    String text = randomString();

    String actual = Encoding.HEX.encode(text.getBytes(CHAR_SET));

    assertEquals(text, new String(new Hex().decode(actual.getBytes(CHAR_SET)), CHAR_SET), "Text");
  }

}
