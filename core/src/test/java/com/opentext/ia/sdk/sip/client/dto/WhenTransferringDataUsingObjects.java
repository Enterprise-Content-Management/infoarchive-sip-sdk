/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ClassUtils;
import org.atteo.evo.inflector.English;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class WhenTransferringDataUsingObjects {

  private static final Collection<Class<?>> PRIMITIVE_WRAPPER_TYPES = Arrays.asList(String.class, Integer.class,
      Long.class, Byte.class, Boolean.class, Double.class, Float.class, Character.class);

  @Parameters(name = "{0}")
  public static Object[] getParameters() {
    return relativePathsIn("src/main/java", WhenTransferringDataUsingObjects::isJava)
        .map(WhenTransferringDataUsingObjects::toClass)
        .filter(WhenTransferringDataUsingObjects::isDto)
        .toArray();
  }

  private static boolean isJava(File file) {
    return file.isFile() && file.getName().endsWith(".java");
  }

  private static Stream<String> relativePathsIn(String root, Predicate<File> acceptableFiles) {
    Collection<File> result = new ArrayList<>();
    File rootDir = new File(root);
    collectFilesIn(rootDir, acceptableFiles, result);
    Path rootPath = rootDir.toPath();
    return result.stream()
        .map(file -> rootPath.relativize(file.toPath()).toString());
  }

  private static void collectFilesIn(File dir, Predicate<File> acceptableFiles, Collection<File> files) {
    Optional.ofNullable(dir.listFiles()).ifPresent(children -> {
      for (File child : children) {
        if (child.isDirectory()) {
          collectFilesIn(child, acceptableFiles, files);
        } else if (acceptableFiles.test(child)) {
          files.add(child);
        }
      }
    });
  }

  private static Class<?> toClass(String path) {
    try {
      return Class.forName(path.substring(0, path.lastIndexOf('.')).replace(File.separatorChar, '.'));
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  private static boolean isDto(Class<?> type) {
    return type != null && isInDtoPackage(type) && extendsDtoBaseClass(type);
  }

  private static boolean isInDtoPackage(Class<?> type) {
    return type.getName().contains(".dto.");
  }

  private static boolean extendsDtoBaseClass(Class<?> type) {
    return NamedLinkContainer.class.equals(type.getSuperclass());
  }


  @Parameter
  public Class<?> type;
  private final Collection<Class<?>> types = new LinkedHashSet<>();
  private final Collection<Class<?>> processedTypes = new LinkedHashSet<>();

  @Test
  public void shouldUseJavaBeans() throws ReflectiveOperationException {
    types.add(type);
    while (!types.isEmpty()) {
      Class<?> bean = types.iterator().next();
      processedTypes.add(bean);
      assertJavaBean(bean);
      types.removeAll(processedTypes);
    }
  }

  private void assertJavaBean(Class<?> bean) throws ReflectiveOperationException {
    assertTrue("Not a Java Bean: " + bean.getSimpleName(), hasOnlyPublicNoArgConstructor(bean));
    assertOnlyGettersAndSetters(bean);
  }

  private boolean hasOnlyPublicNoArgConstructor(Class<?> bean) throws ReflectiveOperationException {
    Constructor<?>[] constructors = bean.getDeclaredConstructors();
    if (constructors.length != 1) {
      return false;
    }
    Constructor<?> constructor = constructors[0];
    if (constructor.getModifiers() != Modifier.PUBLIC) {
      return false;
    }
    if (constructor.getParameterCount() != 0) {
      return false;
    }
    constructor.newInstance(); // Check for exceptions
    return true;
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
    assertTrue(String.format("Non-getter/setter public %s in %s: %s()", English.plural("method", allMethods.size()),
        bean, extraMethods), allMethods.isEmpty());
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
    assertNotNull("Missing method: " + methodName, result);
    assertEquals("Return type of " + methodName, returnType, result.getReturnType());
    assertEquals("Modifiers of " + methodName, Modifier.PUBLIC, result.getModifiers() & ~Modifier.FINAL);
    Object instance = bean.newInstance();
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

  private Object newPrimitiveOf(Class<?> clazz) {
    if (is(clazz, boolean.class)) {
      return false;
    }
    if (is(clazz, int.class, long.class, byte.class, short.class)) {
      return 0;
    }
    if (is(clazz, char.class)) {
      return 'a';
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
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException("Could not instantiate " + clazz, e);
    }
  }

}

