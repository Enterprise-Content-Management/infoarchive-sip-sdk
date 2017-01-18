/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.sample.assemble;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

import com.emc.ia.sdk.sip.assembly.ContentInfo;
import com.emc.ia.sdk.sip.assembly.DigitalObject;
import com.emc.ia.sdk.sip.assembly.DigitalObjectsExtraction;
import com.emc.ia.sdk.sip.assembly.PackagingInformation;
import com.emc.ia.sdk.sip.assembly.PdiAssembler;
import com.emc.ia.sdk.sip.assembly.SipAssembler;
import com.emc.ia.sdk.sip.assembly.XmlPdiAssembler;
import com.emc.ia.sdk.support.io.Encoding;
import com.emc.ia.sdk.support.io.FileBuffer;
import com.emc.ia.sdk.support.io.HashAssembler;
import com.emc.ia.sdk.support.io.HashFunction;
import com.emc.ia.sdk.support.io.SingleHashAssembler;


public class FileArchiver {

  /**
   * Build a SIP archive from all files in a given directory tree.
   * @param args The command line args:<ol>
   * <li>The path to the directory to be archived. The default value is the current directory.</li>
   * <li>The path to the SIP archive to be built. The default value is <code>build/files.zip</code></li>
   * </ol>
   */
  public static void main(String[] args) {
    try {
      Arguments arguments = new Arguments(args);
      String rootPath = new File(arguments.next(".")).getCanonicalPath();
      String sip = arguments.next("build/files.zip");
      new FileArchiver().run(rootPath, sip);
    } catch (IOException e) {
      e.printStackTrace(); // NOPMD
    }
  }

  private void run(String rootPath, String sip) throws IOException {
    // Tell InfoArchive where and how to archive the data
    URI entityUri = URI.create("urn:com.emc.ia.sip.sample.file:1.0");
    String entityName = "file";
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .application("fileApplication")
            .holding("fileHolding")
            .producer("SIP SDK")
            .entity(entityName)
            .schema(entityUri.toString())
        .end()
    .build();

    // Define a mapping from our domain object to the PDI XML
    PdiAssembler<File> pdiAssembler = new XmlPdiAssembler<File>(entityUri, entityName) {
      @Override
      protected void doAdd(File file, Map<String, ContentInfo> contentInfo) {
        try {
          String path = relativePath(file, rootPath);
          getBuilder()
              .element("path", path)
              .element("size", Long.toString(file.length()))
              .element("permissions", permissionsOf(file))
              .element("contentType", Files.probeContentType(file.toPath()))
              .elements("hashes", "hash", contentInfo.get(path).getContentHashes(), (hash, builder) -> {
                builder
                    .attribute("algorithm", hash.getHashFunction())
                    .attribute("encoding", hash.getEncoding())
                    .attribute("value", hash.getValue());
              });
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
    DigitalObjectsExtraction<File> contentAssembler = file -> Collections.singleton(
        DigitalObject.fromFile(relativePath(file, rootPath), file)
    ).iterator();
    HashAssembler contentHashAssembler = new SingleHashAssembler(HashFunction.SHA256, Encoding.BASE64);

    // Assemble the SIP
    SipAssembler<File> assembler = SipAssembler.forPdiAndContentWithContentHashing(prototype, pdiAssembler,
        contentAssembler, contentHashAssembler);
    assembler.start(new FileBuffer(new File(sip)));
    try {
      addFilesIn(new File(rootPath), rootPath, relativePath(new File(sip), rootPath), assembler);
    } finally {
      assembler.end();
    }
  }

  private String relativePath(File file, String rootPath) {
    return file.getAbsolutePath().substring(rootPath.length() + 1);
  }

  private String permissionsOf(File file) {
    return String.format("%c%c%c", file.canRead() ? 'r' : '-', file.canWrite() ? 'w' : '-',
        file.canExecute() ? 'x' : '-');
  }

  private void addFilesIn(File parent, String rootPath, String skipPath, SipAssembler<File> assembler) {
    File[] children = parent.listFiles();
    if (children != null) {
      for (File child : children) {
        if (child.isDirectory()) {
          addFilesIn(child, rootPath, skipPath, assembler);
        } else if (child.isFile() && !relativePath(child, rootPath).equals(skipPath)) {
          assembler.add(child);
        }
      }
    }
  }


  public static class Arguments {

    private final String[] args;
    private int index;

    public Arguments(String[] args) { // NOPMD ArrayIsStoredDirectly
      this.args = args;
    }

    public String next(String defaultValue) {
      return index < args.length ? args[index++] : defaultValue;
    }

  }

}
