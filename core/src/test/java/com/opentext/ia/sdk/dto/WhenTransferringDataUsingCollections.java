/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import org.atteo.evo.inflector.English;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class WhenTransferringDataUsingCollections extends AbstractDtoTestCase {

  @Parameters(name = "{0}")
  public static Object[] getParameters() {
    return classesInDtoPackage()
        .filter(WhenTransferringDataUsingCollections::isDtoCollection)
        .toArray();
  }

  private static boolean isDtoCollection(Class<?> type) {
    return ItemContainer.class.equals(type.getSuperclass());
  }

  @Parameter
  public Class<?> type;

  @Test
  public void shouldBeDtoCollection() throws ReflectiveOperationException {
    assertHasOnlyPublicNoArgConstructor(type);
    assertNameIsPluralOfParameterizedType();
    assertHasNoMethods();
  }

  private void assertNameIsPluralOfParameterizedType() {
    if (Contents.class.equals(type)) {
      return;
    }
    ParameterizedType superClass = (ParameterizedType)type.getAnnotatedSuperclass().getType();
    String singularType = superClass.getActualTypeArguments()[0].getTypeName();
    assertEquals("Name", English.plural(singularType), type.getName());
  }

  private void assertHasNoMethods() {
    assertEquals("# methods", 0, Arrays.stream(type.getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .count());
  }

}
