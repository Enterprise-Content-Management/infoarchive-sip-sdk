/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


class WatchingDirectoryListener implements DirectoryListener {

  private final WatchService watcher;
  private final Map<WatchKey, Path> watchedPaths = Collections.synchronizedMap(new HashMap<>());
  private boolean isListening;
  private final Thread thread;
  private final Set<File> addedFiles = Collections.synchronizedSet(new HashSet<>());

  WatchingDirectoryListener() {
    try {
      watcher = FileSystems.getDefault().newWatchService();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    thread = new Thread(() -> watchForDirectoryChanges());
    thread.setName("Listening for directory changes");
    thread.setDaemon(true);
  }

  private void watchForDirectoryChanges() {
    while (isListening) {
      processDirectoryChangeEvents();
    }
  }

  private void processDirectoryChangeEvents() {
    WatchKey key;
    try {
      key = watcher.take();
    } catch (InterruptedException x) {
      return;
    }
    Path path = watchedPaths.get(key);
    if (path == null) {
      return;
    }
    processDirectoryChangeEvents(key, path);
    if (!key.reset()) {
      watchedPaths.remove(key);
      if (watchedPaths.isEmpty()) {
        isListening = false;
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void processDirectoryChangeEvents(WatchKey key, Path path) {
    for (WatchEvent<?> event : key.pollEvents()) {
      if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
        fileAdded(path, (WatchEvent<Path>)event);
      }
    }
  }

  private void fileAdded(Path dir, WatchEvent<Path> event) {
    Path name = event.context();
    Path file = dir.resolve(name);
    addedFiles.add(file.toFile());
  }

  @Override
  public void listenIn(File dir) {
    if (!isListening) {
      isListening = true;
      thread.start();
    }
    collectCurrentFiles(dir);
    listenForAdditions(dir);
  }

  private void collectCurrentFiles(File dir) {
    File[] files = dir.listFiles();
    if (files == null) {
      return;
    }
    for (File file : files) {
      addedFiles.add(file);
    }
  }

  private void listenForAdditions(File dir) {
    Path path = dir.toPath();
    WatchKey key;
    try {
      key = path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    watchedPaths.put(key, path);
  }

  @Override
  public Iterator<File> addedFiles() {
    synchronized (addedFiles) {
      List<File> result = new ArrayList<>(addedFiles);
      addedFiles.clear();
      Collections.sort(result, (a, b) -> a.getName().compareTo(b.getName()));
      return result.iterator();
    }
  }

  @Override
  public void stopListening() {
    isListening = false;
    thread.interrupt();
    try {
      thread.join();
    } catch (InterruptedException e) {
      // Ignored
    }
  }

  /**
   * Return the thread that watches directory additions.
   * @return The thread that watches directory additions.
   */
  Thread watchThread() {
    return thread;
  }

}
