/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.sdk.test.TestUtil;


public class WhenListeningForFilesInDirectories {

  @Rule
  public final TemporaryFolder temporaryFolder = new TemporaryFolder();
  private final DirectoryListener listener = new DefaultDirectoryListener(0);
  private final Set<File> reportedFiles = new TreeSet<>(fileComparator());

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
    reportedFiles.clear();
    int numExpectedFiles = expected.length;
    await()
        .atMost(100, TimeUnit.MILLISECONDS)
        .with().pollInterval(10, TimeUnit.MILLISECONDS)
        .until(this::updateReportedFiles, hasSize(numExpectedFiles));
    TestUtil.assertEquals("Added files", toSet(expected), updateReportedFiles());
  }

  private Set<File> toSet(File... values) {
    TreeSet<File> result = new TreeSet<>(fileComparator());
    result.addAll(Arrays.asList(values));
    return result;
  }

  private Comparator<? super File> fileComparator() {
    return (a, b) -> a.getName()
      .compareTo(b.getName());
  }

  private Set<File> updateReportedFiles() {
    Iterator<File> files = listener.addedFiles();
    while (files.hasNext()) {
      reportedFiles.add(files.next());
    }
    return reportedFiles;
  }

  @Test
  public void shouldReportExistingFilesOnStartup() throws IOException {
    File file = addFile();

    startListening();

    assertReportedFiles(file);
  }

}
