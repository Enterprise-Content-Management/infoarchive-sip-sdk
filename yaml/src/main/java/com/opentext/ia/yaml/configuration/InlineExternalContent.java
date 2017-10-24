/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


class InlineExternalContent extends PathVisitor {

  static final List<String> RESOURCE_CONTAINER_PATHS = Arrays.asList(
      "/.+/content",
      "/customPresentationConfiguration(s/[^/]+)?/htmlTemplate",
      "/database(s/[^/]+)?/metadata/\\d+",
      "/transformation(s/[^/]+)?/xquery",
      "/transformation(s/[^/]+)?/xslt",
      "/xform(s/[^/]+)?/form",
      "/xqueryModule(s/[^/]+)?/moduleContent");
  static final String TEXT = "text";
  private static final String FORMAT = "format";
  private static final String RESOURCE = "resource";
  private static final Collection<String> SEGMENTS_WITH_FORMAT = Arrays.asList("content");

  private final ResourceResolver resolver;
  private final Map<String, String> formatByExtension = new HashMap<>();

  InlineExternalContent(ResourceResolver resolver) {
    super(RESOURCE_CONTAINER_PATHS);
    this.resolver = resolver;
    formatByExtension.put("xpl", "xproc");
    formatByExtension.put("xq", "xquery");
    formatByExtension.put("xsl", "xslt");
  }

  @Override
  public boolean test(Visit visit) {
    return super.test(visit) && visit.getMap().containsKey(RESOURCE);
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    String resourceName = yaml.get(RESOURCE).toString();
    inlineResource(yaml, resourceName);
    optionallySetFormat(visit.getPath(), yaml, resourceName);
  }

  private void inlineResource(YamlMap yaml, String resourceName) {
    yaml.replace(RESOURCE, TEXT, resolver.apply(resourceName));
  }

  private void optionallySetFormat(String path, YamlMap yaml, String resourceName) {
    String segment = getLastMeaningfulSegment(path);
    if (SEGMENTS_WITH_FORMAT.contains(segment) && !yaml.containsKey(FORMAT)) {
      yaml.put(FORMAT, guessFormat(resourceName));
    }
  }

  private String getLastMeaningfulSegment(String path) {
    String[] segments = path.split("/");
    for (int i = segments.length - 1; i >= 0; i--) {
      String result = segments[i];
      if (!isInteger(result)) {
        return result;
      }
    }
    throw new IllegalStateException("No meaningful segments in path: " + path);
  }

  private boolean isInteger(String text) {
    try {
      Integer.parseInt(text);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private String guessFormat(String resourceName) {
    int index = resourceName.lastIndexOf('.');
    if (index < 0) {
      return null;
    }
    String extension = resourceName.substring(index + 1);
    return formatByExtension.getOrDefault(extension, extension);
  }

}
