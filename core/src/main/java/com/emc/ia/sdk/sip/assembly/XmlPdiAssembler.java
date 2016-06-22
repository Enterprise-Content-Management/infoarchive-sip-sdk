/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.atteo.evo.inflector.English;

import com.emc.ia.sdk.support.io.EncodedHash;
import com.emc.ia.sdk.support.xml.XmlBuilder;
import com.emc.ia.sdk.support.xml.XmlUtil;


/**
 * Base class for assembling XML documents from domain objects using an {@linkplain XmlBuilder}.
 * @param <D> The type of domain objects to assemble the XML document from
 */
public abstract class XmlPdiAssembler<D> extends PdiAssembler<D> {

  private final URI namespace;
  private final String documentElementName;
  private final String domainObjectName;
  private XmlBuilder builder;

  /**
   * Create an instance.
   * @param namespace Optional URI of the XML Namespace to use for the XML document
   * @param domainObjectName The name/tag of the element that captures the domain object. The pluralized version of this
   * will be used for the document element name/tag.
   */
  public XmlPdiAssembler(URI namespace, String domainObjectName) {
    this(namespace, English.plural(domainObjectName), domainObjectName);
  }

  /**
   * Create an instance.
   * @param namespace Optional URI of the XML Namespace to use for the XML document
   * @param documentElementName The name/tag of the document element wrapping the domain objects. This may be
   * <code>null</code> if only one domain object is ever added
   * @param domainObjectName The name/tag of the element that captures the domain object
   */
  public XmlPdiAssembler(URI namespace, String documentElementName, String domainObjectName) {
    this(namespace, documentElementName, domainObjectName, (Validator)null);
  }

  /**
   * Create an instance.
   * @param namespace Optional URI of the XML Namespace to use for the XML document
   * @param domainObjectName The name/tag of the element that captures the domain object
   * @param schema Optional XML Schema for validating the assembled XML document
   */
  public XmlPdiAssembler(URI namespace, String domainObjectName, InputStream schema) {
    this(namespace, English.plural(domainObjectName), domainObjectName, schema);
  }

  /**
   * Create an instance.
   * @param namespace Optional URI of the XML Namespace to use for the XML document
   * @param documentElementName The name/tag of the document element wrapping the domain objects. This may be
   * <code>null</code> if only one domain object is ever added
   * @param domainObjectName The name/tag of the element that captures the domain object
   * @param schema Optional XML Schema for validating the assembled XML document
   */
  public XmlPdiAssembler(URI namespace, String documentElementName, String domainObjectName, InputStream schema) {
    this(namespace, documentElementName, domainObjectName, schema == null ? null : new XmlSchemaValidator(schema));
  }

  /**
   * Create an instance.
   * @param namespace Optional URI of the XML Namespace to use for the XML document
   * @param documentElementName The name/tag of the document element wrapping the domain objects. This may be
   * <code>null</code> if only one domain object is ever added
   * @param domainObjectName The name/tag of the element that captures the domain object
   * @param validator Optional validator for checking whether the XML document meets expectations
   */
  public XmlPdiAssembler(URI namespace, String documentElementName, String domainObjectName, Validator validator) {
    super(validator);
    this.namespace = namespace;
    this.documentElementName = documentElementName;
    this.domainObjectName = Objects.requireNonNull(domainObjectName);
  }

  /**
   * Return the XML document builder for capturing a domain object.
   * @return The XML document builder for capturing a domain object
   */
  protected XmlBuilder getBuilder() {
    return builder;
  }

  /**
   * Add the domain object to the XML document, using {@linkplain #getBuilder()}.
   * @param domainObject The domain object to add
   * @param contentHashes The encoded hashed of the content associated with the domain object
   */
  protected abstract void doAdd(D domainObject, Map<String, Collection<EncodedHash>> contentHashes);

  @Override
  public void start(PrintWriter writer) {
    writer.println(XmlUtil.XML_DECLARATION);
    if (documentElementName != null) {
      writer.print('<');
      writer.print(documentElementName);
      if (namespace != null) {
        writer.print(" xmlns=\"");
        writer.print(namespace);
        writer.print('"');
      }
      writer.println('>');
    }
  }

  @Override
  public final void add(D domainObject, Map<String, Collection<EncodedHash>> contentHashes, PrintWriter writer) {
    builder = XmlBuilder.newDocument();
    try {
      if (namespace != null) {
        builder.namespace(namespace.toString());
      }
      builder.element(domainObjectName);
      doAdd(domainObject, contentHashes);
      writer.println(XmlUtil.toString(builder.build().getDocumentElement(), "  "));
    } finally {
      builder = null;
    }
  }

  @Override
  public final void end(PrintWriter writer) {
    if (documentElementName != null) {
      writer.print("</");
      writer.print(documentElementName);
      writer.println(">");
    }
  }

}
