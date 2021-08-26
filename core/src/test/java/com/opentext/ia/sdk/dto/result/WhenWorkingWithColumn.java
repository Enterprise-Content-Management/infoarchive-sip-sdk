/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.dto.result.Column.DataType;
import com.opentext.ia.sdk.dto.result.Column.DefaultSort;
import com.opentext.ia.sdk.dto.result.Column.Type;
import com.opentext.ia.test.RandomData;


public class WhenWorkingWithColumn {

  private RandomData data;

  @BeforeEach
  public void before() {
    data = new RandomData();
  }

  @Test
  public void shouldHaveNoDefaults() {
    Column column = new Column();
    assertNull(column.getName());
    assertNull(column.getLabel());
    assertNull(column.getXdbElementName());
    assertFalse(column.isHidden());
    assertFalse(column.isEncrypt());
    assertFalse(column.isMasked());
    assertTrue(column.isExportable());
    assertNull(column.getDataType());
    assertNull(column.getType());
    assertEquals(0, column.getOrder());
    assertFalse(column.isSortable());
    assertNull(column.getDefaultSort());
    assertNull(column.getNestedSearchName());
    assertNull(column.getGroupName());
    assertNull(column.getGroupPath());
    assertNull(column.getPath());
  }

  @Test
  public void shouldCreateSchemaColumn() {
    String name = data.string();
    String label = data.string();
    String path = data.string();
    DataType dataType = DataType.STRING;
    DefaultSort sort = DefaultSort.ASCENDING;
    Column column = Column.fromSchema(name, label, path, dataType, sort);
    assertEquals(name, column.getName());
    assertEquals(label, column.getLabel());
    assertEquals(path, column.getPath());
    assertEquals(dataType, column.getDataType());
    assertEquals(Type.SCHEMA_COLUMN_NAME, column.getType());
    assertEquals(sort, column.getDefaultSort());
  }
}
