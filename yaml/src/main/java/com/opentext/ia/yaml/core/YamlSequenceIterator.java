/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.util.List;
import java.util.ListIterator;


class YamlSequenceIterator implements ListIterator<Value> {

  private int index;
  private final List<Object> data;

  YamlSequenceIterator(List<Object> data, int index) {
    this.data = data;
    this.index = index;
  }

  @Override
  public boolean hasNext() {
    return index < data.size();
  }

  @Override
  public Value next() {
    return new Value(data.get(index++));
  }

  @Override
  public boolean hasPrevious() {
    return index > 0;
  }

  @Override
  public Value previous() {
    return new Value(data.get(--index));
  }

  @Override
  public int nextIndex() {
    return index + 1;
  }

  @Override
  public int previousIndex() {
    return index - 1;
  }

  @Override
  public void remove() {
    data.remove(index);
  }

  @Override
  public void set(Value item) {
    data.set(index, item);
  }

  @Override
  public void add(Value item) {
    data.add(index, item);
  }

}
