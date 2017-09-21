/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.*;

import javax.validation.ValidationException;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import com.opentext.ia.sdk.support.xml.XmlUtil;


/**
 * Validate an XML instance against an XML Schema.
 */
public class XmlSchemaValidator implements Validator {

  private final javax.xml.validation.Validator validator;

  /**
   * Create an instance.
   * @param xmlSchema The XML Schema to use for validation
   * @throws FileNotFoundException When the schema could not be found
   */
  public XmlSchemaValidator(File xmlSchema) throws FileNotFoundException {
    this(new FileInputStream(xmlSchema));
  }

  /**
   * Create an instance.
   * @param xmlSchema The XML Schema to use for validation
   */
  public XmlSchemaValidator(InputStream xmlSchema) {
    validator = XmlUtil.newXmlSchemaValidator(xmlSchema);
  }

  @Override
  public void validate(InputStream xmlInstance) {
    try {
      validator.validate(new StreamSource(xmlInstance));
    } catch (SAXException | IOException e) {
      throw new ValidationException(e);
    }
  }

}
