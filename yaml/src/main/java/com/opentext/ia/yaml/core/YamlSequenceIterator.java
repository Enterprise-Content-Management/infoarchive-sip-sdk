/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.util.List;
import java.util.ListIterator;


class YamlSequenceIterator implements ListIterator<Value> {

  private final ListIterator<Object> wrapped;

  YamlSequenceIterator(List<Object> data, int index) {
    this.wrapped = data.listIterator(index);
  }

  @Override
  public int nextIndex() {
    return wrapped.nextIndex();
  }

  @Override
  public boolean hasNext() {
    return wrapped.hasNext();
  }

  @Override
  public Value next() {
    return new Value(wrapped.next());
  }

  @Override
  public int previousIndex() {
    return wrapped.previousIndex();
  }

  @Override
  public boolean hasPrevious() {
    return wrapped.hasPrevious();
  }

  @Override
  public Value previous() {
    return new Value(wrapped.previous());
  }

  @Override
  public void remove() {
    wrapped.remove();
  }

  @Override
  public void set(Value item) {
    wrapped.set(item.getRawData());
  }

  @Override
  public void add(Value item) {
    wrapped.add(item.getRawData());
  }

}
