/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Federation;
import com.emc.ia.sdk.sip.ingestion.dto.JobDefinition;
import com.emc.ia.sdk.sip.ingestion.dto.Services;
import com.emc.ia.sdk.sip.ingestion.dto.Space;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootFolder;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootLibrary;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;

class RestCache { // NOPMD TooManyFields

  private Services services;
  private Tenant tenant;
  private Application application;
  private String ingestUri;
  private Federation federation;
  private String fileSystemRootUri;
  private Space space;
  private String databaseUri;
  private SpaceRootFolder spaceRootFolder;
  private String fileSystemFolderUri;
  private String receptionFolderUri;
  private String storeUri;
  private SpaceRootLibrary spaceRootLibrary;
  private String libraryUri;
  private String pdiUri;
  private String ingestNodeUri;
  private JobDefinition jobDefinition;
  private String aicUri;
  private String quotaUri;

  public String getAicUri() {
    return aicUri;
  }

  public void setAicUri(String aicUri) {
    this.aicUri = aicUri;
  }

  public String getQuotaUri() {
    return quotaUri;
  }

  public void setQuotaUri(String quotaUri) {
    this.quotaUri = quotaUri;
  }

  public Services getServices() {
    return services;
  }

  public void setServices(Services services) {
    this.services = services;
  }

  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }

  public Federation getFederation() {
    return federation;
  }

  public void setFederation(Federation federation) {
    this.federation = federation;
  }

  public String getDatabaseUri() {
    return databaseUri;
  }

  public void setDatabaseUri(String databaseUri) {
    this.databaseUri = databaseUri;
  }

  public String getFileSystemRootUri() {
    return fileSystemRootUri;
  }

  public void setFileSystemRootUri(String fileSystemRootUri) {
    this.fileSystemRootUri = fileSystemRootUri;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public Space getSpace() {
    return space;
  }

  public void setSpace(Space space) {
    this.space = space;
  }

  public SpaceRootLibrary getSpaceRootLibrary() {
    return spaceRootLibrary;
  }

  public void setSpaceRootLibrary(SpaceRootLibrary spaceRootLibrary) {
    this.spaceRootLibrary = spaceRootLibrary;
  }

  public SpaceRootFolder getSpaceRootFolder() {
    return spaceRootFolder;
  }

  public void setSpaceRootFolder(SpaceRootFolder spaceRootFolder) {
    this.spaceRootFolder = spaceRootFolder;
  }

  public String getFileSystemFolderUri() {
    return fileSystemFolderUri;
  }

  public void setFileSystemFolderUri(String fileSystemFolderUri) {
    this.fileSystemFolderUri = fileSystemFolderUri;
  }

  public String getStoreUri() {
    return storeUri;
  }

  public void setStoreUri(String storeUri) {
    this.storeUri = storeUri;
  }

  public String getReceptionFolderUri() {
    return receptionFolderUri;
  }

  public void setReceptionFolderUri(String receptionFolderUri) {
    this.receptionFolderUri = receptionFolderUri;
  }

  public String getIngestNodeUri() {
    return ingestNodeUri;
  }

  public void setIngestNodeUri(String ingestNodeUri) {
    this.ingestNodeUri = ingestNodeUri;
  }

  public String getPdiUri() {
    return pdiUri;
  }

  public void setPdiUri(String pdiUri) {
    this.pdiUri = pdiUri;
  }

  public String getIngestUri() {
    return ingestUri;
  }

  public void setIngestUri(String ingestUri) {
    this.ingestUri = ingestUri;
  }

  public String getLibraryUri() {
    return libraryUri;
  }

  public void setLibraryUri(String libraryUri) {
    this.libraryUri = libraryUri;
  }

  public JobDefinition getJobDefinition() {
    return jobDefinition;
  }

  public void setJobDefinition(JobDefinition jobDefinition) {
    this.jobDefinition = jobDefinition;
  }

}
