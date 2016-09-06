/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.sip.client.dto.result.Column.DataType;
import com.emc.ia.sdk.sip.client.dto.result.Column.DefaultSort;
import com.emc.ia.sdk.sip.client.dto.result.Column.Type;
import com.emc.ia.sdk.support.test.RandomData;

public class WhenWorkingWithColumn {

  private RandomData data;

  @Before
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
