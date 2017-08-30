/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;


public class YamlSequence implements List<Value> {

  private final List<Object> data;

  public YamlSequence(List<Object> data) {
    this.data = data;
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public boolean contains(Object object) {
    return data.contains(object);
  }

  @Override
  public Iterator<Value> iterator() {
    return listIterator();
  }

  @Override
  public Object[] toArray() {
    return data.stream()
        .map(Value::new)
        .toArray();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    Class<?> type = a.getClass().getComponentType();
    T[] result = a.length < data.size() ? (T[])Array.newInstance(type, data.size()) : a;
    int index = 0;
    while (index < data.size()) {
      result[index] = (T)type.cast(unpack(data.get(index)));
      index++;
    }
    if (index < result.length) {
      result[index] = null;
    }
    return result;
  }

  private Object unpack(Object item) {
    return item instanceof Value ? ((Value)item).getRawData() : item;
  }

  @Override
  public boolean add(Value item) {
    return data.add(unpack(item));
  }

  @Override
  public boolean remove(Object item) {
    return data.remove(unpack(item));
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return data.containsAll(unpack(collection));
  }

  private Collection<?> unpack(Collection<?> collection) {
    return collection.stream()
        .map(this::unpack)
        .collect(Collectors.toList());
  }

  @Override
  public boolean addAll(Collection<? extends Value> collection) {
    return data.addAll(unpack(collection));
  }

  @Override
  public boolean addAll(int index, Collection<? extends Value> collection) {
    return data.addAll(index, unpack(collection));
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return data.removeAll(unpack(collection));
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return data.retainAll(unpack(collection));
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public Value get(int index) {
    return new Value(data.get(index));
  }

  @Override
  public Value set(int index, Value item) {
    return new Value(data.set(index, unpack(item)));
  }

  @Override
  public void add(int index, Value item) {
    data.add(index, unpack(item));
  }

  @Override
  public Value remove(int index) {
    return new Value(data.remove(index));
  }

  @Override
  public int indexOf(Object item) {
    return data.indexOf(unpack(item));
  }

  @Override
  public int lastIndexOf(Object item) {
    return data.lastIndexOf(unpack(item));
  }

  @Override
  public ListIterator<Value> listIterator() {
    return listIterator(0);
  }

  @Override
  public ListIterator<Value> listIterator(int index) {
    return new YamlSequenceIterator(data, index);
  }

  @Override
  public List<Value> subList(int fromIndex, int toIndex) {
    return new YamlSequence(data.subList(fromIndex, toIndex));
  }

  @Override
  public String toString() {
    return data == null ? "[]" : data.toString();
  }

}
