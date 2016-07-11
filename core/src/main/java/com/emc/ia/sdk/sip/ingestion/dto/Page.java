/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Page {
  private int size;
  private int totalElements;
  private int totalPages;
  private int number;

  public Page() {
    setSize(20);
    setTotalElements(1);
    setTotalPages(1);
    setNumber(0);
  }

  public int getSize() {
    return size;
  }

  public final void setSize(int size) {
    this.size = size;
  }

  public int getTotalElements() {
    return totalElements;
  }

  public final void setTotalElements(int totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public final void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getNumber() {
    return number;
  }

  public final void setNumber(int number) {
    this.number = number;
  }

}
