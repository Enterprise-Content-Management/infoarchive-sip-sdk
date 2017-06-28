/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sip.assembly.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.DigitalObject;
import com.opentext.ia.sdk.sip.FixedHeaderAndFooterTemplate;


/**
 * {@linkplain Template} using the <a href="http://velocity.apache.org/">Apache Velocity</a> template engine.
 * Templates have access to the following objects:<dl>
 * <dt><code>model</code></dt>
 *   <dd>The domain object (of type D)</dd>
 * <dt><code>hashes</code></dt>
 *   <dd>The encoded hashes of the {@linkplain DigitalObject}s associated with the domain object, if any</dd>
 * <dt><code>isodate</code></dt>
 *   <dd>A utility class to format a date in ISO 8601 format using the <code>format()</code> function</dd>
 * </dl>
 * @param <D> The type of domain object to replace with text
 */
public class VelocityTemplate<D> extends FixedHeaderAndFooterTemplate<D> {

  private static final String TEMPLATE_NAME = VelocityTemplate.class.getName();

  private final Template template;

  /**
   * Create an instance.
   * @param header The fixed header
   * @param footer The fixed footer
   * @param row The template for the rows
   */
  public VelocityTemplate(InputStream header, InputStream footer, InputStream row) {
    this(toString(header), toString(footer), toString(row));
  }

  /**
   * Create an instance.
   * @param header The fixed header
   * @param footer The fixed footer
   * @param row The template for the rows
   */
  public VelocityTemplate(String header, String footer, String row) {
    super(header, footer);
    VelocityEngine engine = new VelocityEngine();
    engine.setProperty(Velocity.RESOURCE_LOADER, "string");
    engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
    engine.addProperty("string.resource.loader.repository.static", "false");
    engine.init();
    StringResourceRepository repository = (StringResourceRepository)engine.getApplicationAttribute(
        StringResourceLoader.REPOSITORY_NAME_DEFAULT);
    repository.putStringResource(TEMPLATE_NAME, row);
    template = engine.getTemplate(TEMPLATE_NAME);
  }

  @Override
  public void writeRow(D domainObject, Map<String, ContentInfo> contentInfo, PrintWriter writer)
      throws IOException {
    VelocityContext context = new VelocityContext();
    context.put("isodate", new DatesTool());
    context.put("model", domainObject);
    context.put("content", contentInfo);
    template.merge(context, writer);
  }

}
