/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class ConvertIngestProcessors extends YamlContentVisitor {

  private static final String PROCESSOR_PACKAGE = "com.emc.ia.ingestion.processor.";
  private static final String INDEX_CREATOR = "index.IndexesCreator";
  private static final Map<String, List<String>> NAME_AND_CLASS_BY_ID = nameAndClassById();

  ConvertIngestProcessors() {
    super("ingest");
  }

  private static Map<String, List<String>> nameAndClassById() {
    Map<String, List<String>> result = new HashMap<>(32 * 4 / 3);
    result.put("sip.download", Arrays.asList("SIP downloader processor", "downloader.SipContentDownloader"));
    result.put("xdb.lib.create", Arrays.asList("XDB Library creator processor", "library.LibraryCreatorProcessor"));
    result.put("sip.decrypt", Arrays.asList("SIP Decrypter processor", "crypto.SipDecryptProcessor"));
    result.put("sip.extractor", Arrays.asList("SIP extractor processor", "extractor.SipExtractor"));
    result.put("pdi.hash.validation", Arrays.asList("PDI hash validation processor", "hash.PdiHashValidatorProcessor"));
    result.put("pdi.validation", Arrays.asList("PDI schema validator processor", "validator.PdiSchemaValidator"));
    result.put("pdi.data.encrypt", Arrays.asList("PDI data encryption", "crypto.PdiDataEncryptProcessor"));
    result.put("sip.xdb.importer", Arrays.asList("XDB SIP importer processor", "importer.SipImporterProcessor"));
    result.put("pdi.xdb.importer", Arrays.asList("XDB PDI importer processor", "importer.PdiImporterProcessor"));
    result.put("pdi.index.creator", Arrays.asList("XDB PDI index processor", INDEX_CREATOR));
    result.put("pdi.aiu.cnt", Arrays.asList("PDI AIU count processor", "aiu.PdiAiuCountProcessor"));
    result.put("pdi.pkeys", Arrays.asList("PDI Partition Keys processor", "partition.PartitionValuesProcessor"));
    result.put("pdi.compress", Arrays.asList("PDI XML compression processor", "compress.PdiCompressionProcessor"));
    result.put("pdi.encrypt", Arrays.asList("PDI XML encryption processor", "crypto.PdiFileEncryptProcessor"));
    result.put("ri.init", Arrays.asList("RI creation", "content.InitRiProcessor"));
    result.put("ri.index", Arrays.asList("RI XDB indexes", INDEX_CREATOR));
    result.put("ci.hash", Arrays.asList("CI hash generator and validator", "content.CiHashProcessor"));
    result.put("ci.compress", Arrays.asList("CI compression", "content.CiCompressProcessor"));
    result.put("ci.encrypt", Arrays.asList("CI encryptor", "crypto.CiEncryptProcessor"));
    result.put("ci.aggregate", Arrays.asList("CI aggregator", "content.CiAggregateProcessor"));
    result.put("ri.content", Arrays.asList("RiContentProcessor", "content.RiContentProcessor"));
    result.put("aip.aggregate.update",
        Arrays.asList("Aggregate Updator processor", "library.AggregateUpdaterProcessor"));
    result.put("xdb.pdi.ci.id", Arrays.asList("Add CI IDs in PDI", "content.PdiContentIdProcessor"));
    result.put("xdb.pdi.ci.index", Arrays.asList("PDI CI indexes", INDEX_CREATOR));
    result.put("pdi.aiu.id", Arrays.asList("PDI AIU Ids processor", "aiu.PdiAiuIdProcessor"));
    result.put("xdb.pdi.aiu.index", Arrays.asList("XDB AIU index processor", INDEX_CREATOR));
    result.put("contents.upload", Arrays.asList("Contents uploader", "uploader.ContentsUploader"));
    result.put("xdb.lib.backup", Arrays.asList("XDB library backup", "backup.BackupProcessor"));
    result.put("pdi.transformer", Arrays.asList("PDI Transformer", "transformer.PdiTransformerProcessor"));
    result.put("xdb.pdi.aiu.apply.retention",
        Arrays.asList("Apply retention at AIU level processor", "retention.AiuRetentionProcessor"));
    result.put("aip.commit", Arrays.asList("Synchronous commit", "commit.CommitProcessor"));
    return result;
  }

  @Override
  void visitContent(Visit visit, YamlMap content) {
    content.get("processors").toList().stream()
        .map(Value::toMap)
        .forEach(this::addNameAndClass);
  }

  private void addNameAndClass(YamlMap processor) {
    String id = processor.get("id").toString();
    if (!NAME_AND_CLASS_BY_ID.containsKey(id)) {
      return;
    }
    List<String> nameAndClass = NAME_AND_CLASS_BY_ID.get(id);
    processor.put("name", nameAndClass.get(0));
    processor.put("class", PROCESSOR_PACKAGE + nameAndClass.get(1));
  }

}
