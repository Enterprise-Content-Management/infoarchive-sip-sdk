/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.validation.ValidationException;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import com.emc.ia.sdk.support.xml.XmlUtil;


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
  public void validate(InputStream xmlInstance) throws ValidationException {
    try {
      validator.validate(new StreamSource(xmlInstance));
    } catch (SAXException | IOException e) {
      throw new ValidationException(e);
    }
  }

}
