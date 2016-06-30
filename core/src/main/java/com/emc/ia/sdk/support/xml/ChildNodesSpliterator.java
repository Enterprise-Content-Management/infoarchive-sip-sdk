/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.xml;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Iterate over child nodes of a given parent node in an XML document.
 */
class ChildNodesSpliterator extends AbstractSpliterator<Node> {

  private Node next;

  ChildNodesSpliterator(Element parent) {
    super(getSizeEstimate(parent), NONNULL | ORDERED | SIZED);
    next = parent == null ? null : parent.getFirstChild();
  }

  private static int getSizeEstimate(Element parent) {
    return parent == null ? 0 : parent.getChildNodes().getLength();
  }

  @Override
  public boolean tryAdvance(Consumer<? super Node> action) {
    boolean result = next != null;
    if (result) {
      action.accept(next);
      next = next.getNextSibling();
    }
    return result;
  }

}
