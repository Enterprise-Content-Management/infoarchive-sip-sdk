/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.emc.ia.sdk.support.test.TestUtil;


public abstract class WhenListeningForFilesInDirectories {

  @Rule
  public final TemporaryFolder temporaryFolder = new TemporaryFolder();
  private final DirectoryListener listener;

  public WhenListeningForFilesInDirectories(DirectoryListener listener) {
    this.listener = listener;
  }

  protected DirectoryListener getListener() {
    return listener;
  }

  @Test
  public void shouldReportAddedFilesExactlyOnce() throws IOException {
    startListening();
    File file1 = addFile();
    File file2 = addFile();
    assertReportedFiles(file1, file2);

    File file3 = addFile();
    assertReportedFiles(file3);

    assertReportedFiles();
  }

  protected void startListening() {
    listener.listenIn(temporaryFolder.getRoot());
  }

  private File addFile() throws IOException {
    return temporaryFolder.newFile();
  }

  private void assertReportedFiles(File... expected) {
    waitAWhile(); // Give listening thread a chance to process changes
    TestUtil.assertEquals("Added files", toSet(expected), reportedFiles());
  }

  private Set<File> toSet(File... values) {
    TreeSet<File> result = new TreeSet<>(fileComparator());
    result.addAll(Arrays.asList(values));
    return result;
  }

  private Comparator<? super File> fileComparator() {
    return (a, b) -> a.getName().compareTo(b.getName());
  }

  private void waitAWhile() {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      // Ignore
    }
  }

  private Set<File> reportedFiles() {
    Set<File> result = new TreeSet<>(fileComparator());
    Iterator<File> files = listener.addedFiles();
    while (files.hasNext()) {
      result.add(files.next());
    }
    return result;
  }

  @Test
  public void shouldReportExistingFilesOnStartup() throws IOException {
    File file = addFile();

    startListening();

    assertReportedFiles(file);
  }

}
