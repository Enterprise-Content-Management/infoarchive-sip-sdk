/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class YamlDiff {

  static final String NO_VALUE = "<<no value>>";
  static final String SAME_VALUE = "...";

  private final YamlMap left;
  private final YamlMap right;

  public YamlDiff(YamlMap left, YamlMap right) {
    this.left = YamlMap.from(left).sort();
    this.right = YamlMap.from(right).sort();
    diffMaps(this.left, this.right);
  }

  private void diffMaps(YamlMap yaml1, YamlMap yaml2) {
    Set<String> keys1 = getKeys(yaml1);
    Set<String> keys2 = getKeys(yaml2);
    addMissing(keys1, keys2, yaml2);
    addMissing(keys2, keys1, yaml1);
    keys1.retainAll(keys2);
    for (String key : keys1) {
      diffValue(yaml1, yaml1.get(key), yaml2, yaml2.get(key), owner -> owner.remove(key), owner -> owner.remove(key));
    }
  }

  private Set<String> getKeys(YamlMap yaml) {
    return yaml.entries().map(Entry::getKey).sorted().collect(Collectors.toSet());
  }

  private void addMissing(Set<String> keys1, Set<String> keys2, YamlMap yaml2) {
    keys1.stream()
        .filter(key -> !keys2.contains(key))
        .forEach(key -> yaml2.put(key, NO_VALUE));
  }

  private <T> void diffValue(T owner1, Value value1, T owner2, Value value2, Consumer<T> removeFromOwner,
      Consumer<T> removeText) {
    if (value1.isMap() && value2.isMap()) {
      diffMapValues(owner1, value1, owner2, value2, removeFromOwner);
    } else if (value1.isList() && value2.isList()) {
      diffListValues(owner1, value1, owner2, value2, removeFromOwner);
    } else if (value1.isScalar() && value2.isScalar()) {
      diffScalarValues(owner1, value1, owner2, value2, removeText);
    }
  }

  private <T> void diffMapValues(T owner1, Value value1, T owner2, Value value2, Consumer<T> removeFromOwner) {
    YamlMap subMap1 = value1.toMap();
    YamlMap subMap2 = value2.toMap();
    diffMaps(subMap1, subMap2);
    if (isEmpty(subMap1)) {
      removeFromOwner.accept(owner1);
      removeFromOwner.accept(owner2);
    }
  }

  private <T> void diffListValues(T owner1, Value value1, T owner2, Value value2, Consumer<T> removeFromOwner) {
    List<Value> list1 = value1.toList();
    List<Value> list2 = value2.toList();
    diffLists(list1, list2);
    if (list1.isEmpty()) {
      removeFromOwner.accept(owner1);
      removeFromOwner.accept(owner2);
    }
  }

  private <T> void diffScalarValues(T owner1, Value value1, T owner2, Value value2, Consumer<T> removeScalar) {
    String text1 = normalize(value1);
    String text2 = normalize(value2);
    if (Objects.equals(text1, text2)) {
      removeScalar.accept(owner1);
      removeScalar.accept(owner2);
    }
  }

  private boolean isEmpty(YamlMap map) {
    return map.entries()
        .map(Entry::getValue)
        .map(Value::isEmpty)
        .reduce(true, (a, b) -> a && b);
  }

  private void diffLists(List<Value> list1, List<Value> list2) {
    ListIterator<Value> iterator1 = list1.listIterator();
    ListIterator<Value> iterator2 = list2.listIterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      Value value1 = iterator1.next();
      Value value2 = iterator2.next();
      diffValue(iterator1, value1, iterator2, value2, ListIterator::remove,
          iterator -> iterator.set(new Value(SAME_VALUE)));
    }
    addMissing(iterator1, list2);
    addMissing(iterator2, list1);
    removeEndingMarkedItems(list1, list2);
  }

  private String normalize(Value value) {
    return value.isEmpty() ? null : value.toString().trim();
  }

  private void addMissing(ListIterator<Value> iterator, List<Value> list) {
    while (iterator.hasNext()) {
      iterator.next();
      list.add(new Value(NO_VALUE));
    }
  }

  private void removeEndingMarkedItems(List<Value> list1, List<Value> list2) {
    for (int i = list1.size() - 1; i >= 0; i--) {
      if (list1.get(i).equals(list2.get(i))) {
        list1.remove(i);
        list2.remove(i);
      } else {
        break;
      }
    }
  }

  public YamlMap left() {
    return left;
  }

  public YamlMap right() {
    return right;
  }

  @Override
  public String toString() {
    return String.format("%s%n---%n%s", left.toString().trim(), right.toString().trim());
  }

}
