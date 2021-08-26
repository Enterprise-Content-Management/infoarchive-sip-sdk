/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class WhenTransferringDataUsingCollections extends AbstractDtoTestCase {

  public static Object[] source() {
    return classesInDtoPackage()
        .filter(WhenTransferringDataUsingCollections::isDtoCollection)
        .toArray();
  }

  private static boolean isDtoCollection(Class<?> type) {
    return ItemContainer.class.equals(type.getSuperclass());
  }

  @ParameterizedTest
  @MethodSource("source")
  public void shouldBeDtoCollection(Class<?> type) throws ReflectiveOperationException {
    assertHasOnlyPublicNoArgConstructor(type);
    assertNameIsPluralOfParameterizedType(type);
    assertHasNoMethods(type);
  }

  private void assertNameIsPluralOfParameterizedType(Class<?> type) {
    if (Contents.class.equals(type)) {
      return;
    }
    ParameterizedType superClass = (ParameterizedType)type.getAnnotatedSuperclass().getType();
    String singularType = superClass.getActualTypeArguments()[0].getTypeName();
    assertEquals(English.plural(singularType), type.getName(), "Name");
  }

  private void assertHasNoMethods(Class<?> type) {
    assertEquals(0, Arrays.stream(type.getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .count(), "# methods");
  }

}
