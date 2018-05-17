/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourcesResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


class InlineExternalContent extends PathVisitor {

  static final List<String> RESOURCE_CONTAINER_PATHS = Arrays.asList(
      "/.+/content(/\\d+)?",
      "/customPresentationConfiguration(s/[^/]+)?/htmlTemplate",
      "/database(s/[^/]+)?/metadata/\\d+",
      "/transformation(s/[^/]+)?/xquery",
      "/transformation(s/[^/]+)?/xslt",
      "/xform(s/[^/]+)?/form",
      "/(xquery|xqueries/[^/]+)/query",
      "/xqueryModule(s/[^/]+)?/moduleContent");
  static final String TEXT = "text";
  private static final String FORMAT = "format";
  private static final String RESOURCE = "resource";
  private static final Collection<String> SEGMENTS_WITH_FORMAT = Arrays.asList("content");
  private static final Collection<String> BINARY_EXTENSIONS = Arrays.asList("custom", "pdf", "zip");

  private final ResourcesResolver resolver;
  private final Map<String, String> formatByExtension = new HashMap<>();

  InlineExternalContent(ResourcesResolver resolver) {
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
    String path = visit.getPath();
    Value resource = yaml.get(RESOURCE);
    if (resource.isScalar()) {
      processResource(yaml, path, resource.toString());
    } else if (resource.isList()) {
      resource.toList().forEach(item ->
          processResource(yaml, path, item.toString()));
    }
  }

  private void processResource(YamlMap yaml, String path, String resourceName) {
    if (isBinary(yaml, resourceName)) {
      return;
    }
    inlineResource(yaml, resourceName);
    optionallySetFormat(path, yaml, resourceName);
  }

  private boolean isBinary(YamlMap yaml, String resourceName) {
    if (yaml.get("binary").toBoolean()) {
      return true;
    }
    if (BINARY_EXTENSIONS.contains(yaml.get(FORMAT).toString())) {
      return true;
    }
    int index = resourceName.lastIndexOf('.');
    if (index < 0) {
      return false;
    }
    return BINARY_EXTENSIONS.contains(resourceName.substring(index + 1));
  }

  private void inlineResource(YamlMap yaml, String resourceName) {
    List<String> texts = resolver.resolve(resourceName);
    if (yaml.containsKey(RESOURCE)) {
      switch (texts.size()) {
        case 0:
          throw new UnknownResourceException(resourceName, null);
        case 1:
          yaml.replace(RESOURCE, TEXT, texts.get(0));
          break;
        default:
          yaml.replace(RESOURCE, TEXT, texts);
          break;
      }
    } else {
      Value current = yaml.get(TEXT);
      if (current.isList()) {
        current.toList().addAll(texts.stream().map(Value::new).collect(Collectors.toList()));
      } else {
        texts.add(0, current.toString());
        yaml.replace(TEXT, texts);
      }
    }
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
