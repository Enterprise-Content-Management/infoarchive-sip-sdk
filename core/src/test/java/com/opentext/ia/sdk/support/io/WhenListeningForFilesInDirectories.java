/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.test.TestUtil;


public class WhenListeningForFilesInDirectories extends TestCase {

  @TempDir
  public Path temporaryFolder;

  @Test
  public void shouldReportAddedFilesExactlyOnce() throws IOException {
    DirectoryListener listener = new DefaultDirectoryListener(0);
    startListening(listener);
    File file1 = addFile();
    File file2 = addFile();
    assertReportedFiles(listener, file1, file2);

    File file3 = addFile();
    assertReportedFiles(listener, file3);

    assertReportedFiles(listener);
  }

  protected void startListening(DirectoryListener listener) {
    listener.listenIn(temporaryFolder.toFile());
  }

  private File addFile() throws IOException {
    return newFile(temporaryFolder);
  }

  private void assertReportedFiles(DirectoryListener listener, File... expected) {
    Set<File> reportedFiles = new TreeSet<>(fileComparator());
    int numExpectedFiles = expected.length;
    await()
        .atMost(500, TimeUnit.MILLISECONDS)
        .with().pollInterval(10, TimeUnit.MILLISECONDS)
        .until(() -> updateReportedFiles(listener, reportedFiles), hasSize(numExpectedFiles));
    TestUtil.assertEquals(toSet(expected), updateReportedFiles(listener, reportedFiles),
        "Added files");
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

  private Collection<File> updateReportedFiles(DirectoryListener listener,
      Set<File> reportedFiles) {
    Iterator<File> files = listener.addedFiles();
    while (files.hasNext()) {
      reportedFiles.add(files.next());
    }
    return reportedFiles;
  }

  @Test
  public void shouldReportExistingFilesOnStartup() throws IOException {
    DirectoryListener listener = new DefaultDirectoryListener(0);
    File file = addFile();
    startListening(listener);
    assertReportedFiles(listener, file);
  }

}
