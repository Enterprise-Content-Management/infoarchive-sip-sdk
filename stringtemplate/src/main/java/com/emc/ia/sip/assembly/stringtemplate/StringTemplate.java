/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sip.assembly.stringtemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.NoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.emc.ia.sdk.sip.assembly.DigitalObject;
import com.emc.ia.sdk.sip.assembly.FixedHeaderAndFooterTemplate;
import com.emc.ia.sdk.sip.assembly.Template;
import com.emc.ia.sdk.support.io.EncodedHash;


/**
 * {@linkplain Template} using the <em>StringTemplate</em> templating engine.
 * Templates have access to the following objects:<dl>
 * <dt><code>model</code></dt>
 *   <dd>The domain object (of type D)</dd>
 * <dt><code>hashes</code></dt>
 *   <dd>The encoded hashes of the {@linkplain DigitalObject}s associated with the domain object, if any</dd>
 * </dl>
 * @param <D> The type of domain object to replace with text
 */
public class StringTemplate<D> extends FixedHeaderAndFooterTemplate<D> {

  private static final char DEFAULT_DELIMETER_START_CHAR = '$';
  private static final char DEFAULT_DELIMETER_END_CHAR = '$';
  private static final String TEMPLATE_NAME = "template";
  private static final String MODEL_VARIABLE = "model";
  private static final String HASHES_VARIABLE = "hashes";

  private final ST template;

  /**
   * Create an instance.
   * @param header The fixed header
   * @param footer The fixed footer
   * @param row The template for the rows
   */
  public StringTemplate(InputStream header, InputStream footer, InputStream row) {
    this(toString(header), toString(footer), toString(row));
  }

  /**
   * Create an instance with the default start and end delimeters.
   * @param header The fixed header
   * @param footer The fixed footer
   * @param row The template for the rows
   */
  public StringTemplate(String header, String footer, String row) {
    this(header, footer, row, DEFAULT_DELIMETER_START_CHAR, DEFAULT_DELIMETER_END_CHAR);

  }

  /**
   * Create an instance.
   * @param header The fixed header
   * @param footer The fixed footer
   * @param row The template for the rows
   * @param delimeterStart The character that starts a StringTemplate expression.
   * @param delimeterEnd The character that ends a StringTemplate expression.
   */
  public StringTemplate(String header, String footer, String row, char delimeterStart, char delimeterEnd) {
    super(header, footer);
    this.template = compileTemplate(row, delimeterStart, delimeterEnd);
  }

  private ST compileTemplate(String row, char delimeterStartChar, char delimeterEndChar) {
    STGroup group = new STGroup(delimeterStartChar, delimeterEndChar);
    prepareGroup(group);
    group.defineTemplate(TEMPLATE_NAME, MODEL_VARIABLE + ',' + HASHES_VARIABLE, row);
    return group.getInstanceOf(TEMPLATE_NAME);
  }

  /**
   * Prepares a group by adding renderers, adaptors and sub templates. Override this if you want to add additional
   * renderers. By default adds:
   * <ul>
   * <li>an {@linkplain XmlDateRenderer} which renders Date instances into the standard XML date and time format</li>
   * <li>an {@linkplain MapModelAdaptor} which allows {@linkplain java.util.Map} to be used transparently as domain
   * objects.</li>
   * </ul>
   * @param group The template group
   */
  protected void prepareGroup(STGroup group) {
    registerAdaptor(group, Map.class, new MapModelAdaptor());
    registerRenderer(group, Date.class, new XmlDateRenderer());
  }

  /**
   * Registers a ModelAdaptor with the group. Override this method if you want to suppress one OOTB adaptor but not all.
   * @param <S> The domain object type
   * @param group The template group
   * @param type The domain object class
   * @param adaptor The adaptor that will be used to extract properties of objects of type S
   */
  protected <S> void registerAdaptor(STGroup group, Class<S> type, ModelAdaptor adaptor) {
    group.registerModelAdaptor(type, adaptor);
  }

  /**
   * Registers a renderer with the group. Override this method if you want to suppress one OOTB renderer but not all.
   * @param <S> The domain object type
   * @param group The template group
   * @param type The domain object class
   * @param attributeRenderer The renderer that will be used to render objects of type S into a String
   */
  protected <S> void registerRenderer(STGroup group, Class<S> type, AttributeRenderer attributeRenderer) {
    group.registerRenderer(type, attributeRenderer);
  }

  @Override
  public void writeRow(D domainObject, Map<String, Collection<EncodedHash>> hashes, PrintWriter writer)
      throws IOException {
    setTemplateVariable(MODEL_VARIABLE, domainObject);
    setTemplateVariable(HASHES_VARIABLE, hashes);
    template.write(new NoIndentWriter(writer));
  }

  private void setTemplateVariable(String name, Object value) {
    template.remove(name);
    template.add(name, value);
  }

}
