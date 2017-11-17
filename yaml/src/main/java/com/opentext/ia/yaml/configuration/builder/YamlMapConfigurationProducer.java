/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.opentext.ia.configuration.Configuration;
import com.opentext.ia.configuration.ConfigurationObject;
import com.opentext.ia.configuration.ConfigurationProducer;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;


/**
 * Produce an InfoArchive configuration in YAML format.
 * @author Ray Sinnema
 * @since 9.4.0
 */
public class YamlMapConfigurationProducer implements ConfigurationProducer<YamlMap> {

  @Override
  public Configuration<YamlMap> produce(ConfigurationObject container) {
    return new YamlMapConfiguration(toYaml(container));
  }

  private YamlMap toYaml(ConfigurationObject container) {
    YamlMap result = new YamlMap().put("version", "1.0.0");
    addChildObjects(container, result);
    return result;
  }

  private void addChildObjects(ConfigurationObject container, YamlMap yaml) {
    container.getChildObjects().forEach((collection, items) -> {
      yaml.put(collection, items.stream().map(this::propertiesToYaml).collect(Collectors.toList()));
      items.forEach(item -> addChildObjects(item, yaml));
    });
  }

  private YamlMap propertiesToYaml(ConfigurationObject object) {
    YamlMap result = new YamlMap();
    copy(object.getProperties(), result);
    return result;
  }

  private void copy(JSONObject source, YamlMap destination) {
    source.keySet().forEach(key -> {
      destination.put(key, jsonToYaml(source.get(key)));
    });
  }

  private Object jsonToYaml(Object value) {
    Object result;
    if (value instanceof JSONObject) {
      YamlMap subObject = new YamlMap();
      copy((JSONObject)value, subObject);
      result = subObject;
    } else if (value instanceof JSONArray) {
      List<Value> sequence = new ArrayList<>();
      JSONArray array = (JSONArray)value;
      for (Object item : array) {
        sequence.add(new Value(jsonToYaml(item)));
      }
      result = sequence;
    } else {
      result = value;
    }
    return result;
  }

}
