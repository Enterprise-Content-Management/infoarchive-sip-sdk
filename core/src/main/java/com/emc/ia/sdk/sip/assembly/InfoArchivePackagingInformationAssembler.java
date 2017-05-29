/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.PrintWriter;

import com.emc.ia.sdk.support.datetime.Dates;
import com.emc.ia.sdk.support.xml.XmlBuilder;


/**
 * Convert {@linkplain PackagingInformation} into a format accepted by InfoArchive.
 */
public class InfoArchivePackagingInformationAssembler extends PrintWriterAssembler<PackagingInformation> {

  public InfoArchivePackagingInformationAssembler() {
    super(new XmlSchemaValidator(InfoArchivePackagingInformationAssembler.class.getResourceAsStream("/sip.xsd")));
  }

  @Override
  protected void start(PrintWriter writer) {
    // Nothing to do
  }

  @Override
  protected void add(PackagingInformation packagingInformation, PrintWriter writer) {
    XmlBuilder<?> builder = XmlBuilder.newDocument(writer)
        .namespace("urn:x-emc:ia:schema:sip:1.0")
        .element("sip");
    DataSubmissionSession dss = packagingInformation.getDss();
    builder.element("dss")
        .element("holding", dss.getHolding())
        .element("id", dss.getId())
        .element("pdi_schema", dss.getSchema())
        .element("production_date", Dates.toIso(dss.getProductionDate()))
        .element("base_retention_date", Dates.toIso(dss.getBaseRetentionDate()))
        .element("producer", dss.getProducer())
        .element("entity", dss.getEntity())
        .element("priority", Integer.toString(dss.getPriority()))
        .element("application", dss.getApplication())
        .element("retention_class", dss.getRetentionClass())
    .end()
    .element("production_date", Dates.toIso(packagingInformation.getProductionDate()))
    .element("seqno", String.valueOf(packagingInformation.getSequenceNumber()))
    .element("is_last", String.valueOf(packagingInformation.isLast()))
    .element("aiu_count", String.valueOf(packagingInformation.getAiuCount()))
    .element("page_count", String.valueOf(packagingInformation.getPageCount()));
    packagingInformation.pdiHash().ifPresent(hash -> {
      builder.element("pdi_hash")
        .attribute("algorithm", hash.getHashFunction())
        .attribute("encoding", hash.getEncoding())
        .text(hash.getValue())
        .end();
    });
    builder.build();
  }

  @Override
  protected void end(PrintWriter writer) {
    // Nothing to do
  }

}
