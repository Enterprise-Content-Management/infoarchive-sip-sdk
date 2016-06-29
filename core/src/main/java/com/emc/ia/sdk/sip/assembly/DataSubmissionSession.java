/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.util.Date;
import java.util.UUID;

import com.emc.ia.sdk.sip.assembly.PackagingInformation.PackagingInformationBuilder;


/**
 * <a href="http://public.ccsds.org/publications/archive/650x0m2.pdf">Data Submission Session</a>. A DSS is probably
 * easiest created using a {@linkplain DataSubmissionSession#builder() builder}.
 */
public class DataSubmissionSession {

  private final String id;
  private final String holding;
  private final String schema;
  private final Date productionDate;
  private final Date baseRetentionDate;
  private final String producer;
  private final String entity;
  private final int priority;
  private final String application;
  private final String retentionClass;

  public DataSubmissionSession(String id, String holding, String schema, Date productionDate, Date baseRetentionDate, // NOPMD ExcessiveParameterList
      String producer, String entity, int priority, String application, String retentionClass) {
    this.id = id;
    this.holding = holding;
    this.schema = schema;
    this.productionDate = productionDate;
    this.baseRetentionDate = baseRetentionDate;
    this.producer = producer;
    this.entity = entity;
    this.priority = priority;
    this.application = application;
    this.retentionClass = retentionClass;
  }

  public String getId() {
    return id;
  }

  public String getHolding() {
    return holding;
  }

  public String getSchema() {
    return schema;
  }

  public Date getProductionDate() {
    return productionDate;
  }

  public Date getBaseRetentionDate() {
    return baseRetentionDate;
  }

  public String getProducer() {
    return producer;
  }

  public String getEntity() {
    return entity;
  }

  public int getPriority() {
    return priority;
  }

  public String getApplication() {
    return application;
  }

  public String getRetentionClass() {
    return retentionClass;
  }

  /**
   * @return A <a href="http://c2.com/cgi/wiki?BuilderPattern">builder</a> object to construct a DSS
   */
  public static DataSubmissionSessionBuilder builder() {
    return new DataSubmissionSessionBuilder();
  }


  /**
   * <a href="http://c2.com/cgi/wiki?BuilderPattern">Builder</a> object to construct a
   * {@linkplain DataSubmissionSession DSS}.
   */
  public static class DataSubmissionSessionBuilder {

    private final PackagingInformationBuilder packagingInformationBuilder;
    private String id = UUID.randomUUID().toString();
    private String holding;
    private String schema;
    private Date productionDate = new Date();
    private Date baseRetentionDate = new Date();
    private String producer;
    private String entity;
    private int priority;
    private String application;
    private String retentionClass;

    public DataSubmissionSessionBuilder() {
      this(null);
    }

    public DataSubmissionSessionBuilder(PackagingInformationBuilder owner) {
      this.packagingInformationBuilder = owner;
    }

    public DataSubmissionSessionBuilder from(DataSubmissionSession dss) {
      id(dss.getId());
      holding(dss.getHolding());
      schema(dss.getSchema());
      productionDate(dss.getProductionDate());
      baseRetentionDate(dss.getBaseRetentionDate());
      producer(dss.getProducer());
      entity(dss.getEntity());
      priority(dss.getPriority());
      application(dss.getApplication());
      retentionClass(dss.getRetentionClass());
      return this;
    }

    public DataSubmissionSessionBuilder id(String dssId) {
      this.id = dssId;
      return this;
    }

    public DataSubmissionSessionBuilder holding(String archiveHolding) {
      this.holding = archiveHolding;
      return this;
    }

    public DataSubmissionSessionBuilder schema(String pdiSchema) {
      this.schema = pdiSchema;
      return this;
    }

    public DataSubmissionSessionBuilder productionDate(Date date) {
      this.productionDate = date;
      return this;
    }

    public DataSubmissionSessionBuilder baseRetentionDate(Date date) {
      this.baseRetentionDate = date;
      return this;
    }

    public DataSubmissionSessionBuilder producer(String source) {
      this.producer = source;
      if (this.application == null) {
        this.application = source;
      }
      return this;
    }

    public DataSubmissionSessionBuilder entity(String owner) {
      this.entity = owner;
      return this;
    }

    public DataSubmissionSessionBuilder priority(int prio) {
      this.priority = prio;
      return this;
    }

    public DataSubmissionSessionBuilder application(String source) {
      this.application = source;
      if (this.producer == null) {
        this.producer = source;
      }
      return this;
    }

    public DataSubmissionSessionBuilder retentionClass(String retention) {
      this.retentionClass = retention;
      return this;
    }

    public DataSubmissionSession build() {
      return new DataSubmissionSession(id, holding, schema, productionDate, baseRetentionDate, producer, entity,
          priority, application, retentionClass);
    }

    public PackagingInformationBuilder end() {
      return packagingInformationBuilder;
    }

  }

}
