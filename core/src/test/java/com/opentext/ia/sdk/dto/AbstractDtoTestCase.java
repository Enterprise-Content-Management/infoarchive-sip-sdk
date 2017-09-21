/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


abstract class AbstractDtoTestCase {

  protected static Stream<Class<?>> classesInDtoPackage() {
    Stream<Class<?>> classes = relativePathsIn("src/main/java", AbstractDtoTestCase::isJava)
        .map(AbstractDtoTestCase::toClass);
    return classes.filter(AbstractDtoTestCase::isInDtoPackage);
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

  private static boolean isInDtoPackage(Class<?> type) {
    return type != null && type.getName().contains(".dto.");
  }

  protected boolean hasOnlyPublicNoArgConstructor(Class<?> bean) throws ReflectiveOperationException {
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
    constructor.setAccessible(true);
    constructor.newInstance(); // Check for exceptions
    return true;
  }

  protected void assertHasOnlyPublicNoArgConstructor(Class<?> clazz) throws ReflectiveOperationException {
    assertTrue("Constructor should be public", hasOnlyPublicNoArgConstructor(clazz));
  }

}
