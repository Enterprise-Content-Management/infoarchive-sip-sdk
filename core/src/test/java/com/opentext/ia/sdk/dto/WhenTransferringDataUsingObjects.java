/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;
import org.atteo.evo.inflector.English;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


public class WhenTransferringDataUsingObjects extends AbstractDtoTestCase {

  private static final Collection<Class<?>> PRIMITIVE_WRAPPER_TYPES = Arrays.asList(String.class, Integer.class,
      Long.class, Byte.class, Boolean.class, Double.class, Float.class, Character.class);

  public static Object[] source() {
    return classesInDtoPackage()
        .filter(WhenTransferringDataUsingObjects::isDto)
        .toArray();
  }

  private static boolean isDto(Class<?> type) {
    return NamedLinkContainer.class.equals(type.getSuperclass());
  }

  private final Collection<Class<?>> types = new LinkedHashSet<>();
  private final Collection<Class<?>> processedTypes = new LinkedHashSet<>();

  @ParameterizedTest
  @MethodSource("source")
  public void shouldUseJavaBeans(Class<?> type) throws ReflectiveOperationException {
    types.add(type);
    while (!types.isEmpty()) {
      Class<?> bean = types.iterator().next();
      processedTypes.add(bean);
      assertJavaBean(bean);
      types.removeAll(processedTypes);
    }
  }

  private void assertJavaBean(Class<?> bean) throws ReflectiveOperationException {
    assertHasOnlyPublicNoArgConstructor(bean);
    assertOnlyGettersAndSetters(bean);
  }

  private void assertOnlyGettersAndSetters(Class<?> bean) throws ReflectiveOperationException {
    Collection<Method> gettersAndSetters = new ArrayList<>();
    for (Field field : bean.getDeclaredFields()) {
      if (field.isSynthetic()) {
        continue;
      }
      Class<?> fieldType = field.getType();
      if (!isPrimitive(fieldType) && !isCollection(fieldType)) {
        types.add(fieldType);
      }
      String propertyName = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
      gettersAndSetters.add(assertMethod(bean, getterPrefixFor(fieldType) + propertyName, fieldType));
      gettersAndSetters.add(assertMethod(bean, "set" + propertyName, void.class, fieldType));
    }
    Collection<Method> allMethods = Arrays.stream(bean.getDeclaredMethods())
        .filter(method -> !"toString".equals(method.getName()))
        .filter(method -> Modifier.isPublic(method.getModifiers()))
        .collect(Collectors.toList());
    allMethods.removeAll(gettersAndSetters);
    String extraMethods = allMethods.stream()
        .map(method -> method.getName())
        .collect(Collectors.joining("(), "));
    assertTrue(allMethods.isEmpty(),
        String.format(
            "DTO should not have any logic, but found non-getter/setter public %s in %s: %s()",
            English.plural("method", allMethods.size()), bean, extraMethods));
  }

  private String getterPrefixFor(Class<?> fieldType) {
    return isBoolean(fieldType) ? "is" : "get";
  }

  private boolean isBoolean(Class<?> fieldType) {
    return fieldType.equals(Boolean.class) || fieldType.equals(boolean.class);
  }

  private boolean isPrimitive(Class<?> fieldType) {
    return fieldType.isPrimitive() || PRIMITIVE_WRAPPER_TYPES.contains(fieldType);
  }

  private boolean isCollection(Class<?> fieldType) {
    return Collection.class.isAssignableFrom(fieldType) || Map.class.isAssignableFrom(fieldType);
  }

  private Method assertMethod(Class<?> bean, String name, Class<?> returnType, Class<?>... parameterTypes)
      throws ReflectiveOperationException {
    String methodName = String.format("%s.%s()", bean.getName(), name);
    Method result = bean.getDeclaredMethod(name, parameterTypes);
    assertNotNull(result, "Missing method: " + methodName);
    assertEquals(returnType, result.getReturnType(), "Return type of " + methodName);
    assertEquals(Modifier.PUBLIC, result.getModifiers() & ~Modifier.FINAL,
        "Modifiers of " + methodName);
    Object instance = bean.getDeclaredConstructor().newInstance();
    Object[] parameters = Arrays.stream(parameterTypes)
        .map(this::newInstanceOf)
        .toArray();
    try {
      result.invoke(instance, parameters);
    } catch (IllegalArgumentException e) {
      fail("Could not invoke " + methodName);
    }
    return result;
  }

  private Object newInstanceOf(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      return newPrimitiveOf(clazz);
    }
    Class<?> wrappedPrimitive = ClassUtils.wrapperToPrimitive(clazz);
    if (wrappedPrimitive != null) {
      Object value = newPrimitiveOf(wrappedPrimitive);
      return newWrappedPrimitive(clazz, value instanceof Number ? value.toString() : value);
    }
    return newObjectOfType(clazz);
  }

  private Comparable<?> newPrimitiveOf(Class<?> clazz) {
    if (is(clazz, boolean.class)) {
      return Boolean.FALSE;
    }
    if (is(clazz, int.class, long.class, byte.class)) {
      return Integer.valueOf(0);
    }
    if (is(clazz, char.class)) {
      return Character.valueOf('a');
    }
    throw new IllegalStateException("Could not instantiate primitive of type " + clazz);
  }

  private boolean is(Class<?> clazz, Class<?>... possibilities) {
    return Arrays.stream(possibilities)
        .filter(c -> clazz.equals(c))
        .findAny()
        .isPresent();
  }

  private Object newWrappedPrimitive(Class<?> clazz, Object value) {
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
        try {
          return constructor.newInstance(value);
        } catch (ReflectiveOperationException e) {
          throw new IllegalStateException("Could not create primitive wrapper " + clazz, e);
        }
      }
    }
    throw new IllegalStateException("Could not find constructor for " + clazz + " that accepts " + value);
  }

  private Object newObjectOfType(Class<?> clazz) {
    if (List.class.equals(clazz)) {
      return new ArrayList<>();
    }
    if (Map.class.equals(clazz)) {
      return new HashMap<>();
    }
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException("Could not instantiate " + clazz, e);
    }
  }

}
