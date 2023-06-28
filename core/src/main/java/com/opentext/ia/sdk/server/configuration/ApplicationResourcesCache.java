/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.dto.Application;
import com.opentext.ia.sdk.dto.CryptoObject;
import com.opentext.ia.sdk.dto.JobDefinition;
import com.opentext.ia.sdk.dto.PdiSchema;
import com.opentext.ia.sdk.dto.RdbDataNode;
import com.opentext.ia.sdk.dto.Search;
import com.opentext.ia.sdk.dto.SearchComposition;
import com.opentext.ia.sdk.dto.Services;
import com.opentext.ia.sdk.dto.Space;
import com.opentext.ia.sdk.dto.SpaceRootFolder;
import com.opentext.ia.sdk.dto.SpaceRootRdbDatabase;
import com.opentext.ia.sdk.dto.Tenant;
import com.opentext.ia.sdk.dto.XForm;

@SuppressWarnings({ "PMD.TooManyFields", "PMD.ExcessivePublicCount" })
public class ApplicationResourcesCache {

  private Services services;
  private Tenant tenant;
  private Application application;
  private String ingestUri;
  private RdbDataNode rdbDataNode;
  private String fileSystemRootUri;
  private Space space;
  private String databaseUri;
  private SpaceRootFolder spaceRootFolder;
  private String fileSystemFolderUri;
  private String receptionFolderUri;
  private String storeUri;
  private SpaceRootRdbDatabase spaceRootRdbDatabase;
  private String libraryUri;
  private String pdiUri;
  private String ingestNodeUri;
  private JobDefinition jobDefinition;
  private String aicUri;
  private List<String> queryUris;
  private String quotaUri;
  private String resultConfigHelperUri;
  private Search search;
  private SearchComposition searchComposition;
  private XForm xform;
  private String holdingUri;
  private CryptoObject cryptoObject;
  private String pdiCryptoUri;
  private PdiSchema pdiSchema;

  private final Map<String, String> aicUriByName = new HashMap<>();
  private final Map<String, String> queryUriByName = new HashMap<>();
  private final Map<String, String> objectUriByTypeAndName = new HashMap<>();

  public void setObjectUri(String type, String name, String uri) {
    objectUriByTypeAndName.put(type + "." + name, uri);
  }

  public String getObjectUri(String type, String name) {
    return objectUriByTypeAndName.get(type + "." + name);
  }

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

  public RdbDataNode getRdbDataNode() {
    return rdbDataNode;
  }

  public void setRdbDataNode(RdbDataNode rdbDataNode) {
    this.rdbDataNode = rdbDataNode;
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

  public SpaceRootRdbDatabase getSpaceRootRdbDatabase() {
    return spaceRootRdbDatabase;
  }

  public void setSpaceRootRdbDatabase(SpaceRootRdbDatabase spaceRootRdbDatabase) {
    this.spaceRootRdbDatabase = spaceRootRdbDatabase;
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

  public String getResultConfigHelperUri() {
    return resultConfigHelperUri;
  }

  public void setResultConfigHelperUri(String resultConfigHelperUri) {
    this.resultConfigHelperUri = resultConfigHelperUri;
  }

  public Search getSearch() {
    return search;
  }

  public void setSearch(Search search) {
    this.search = search;
  }

  public XForm getXform() {
    return xform;
  }

  public void setXform(XForm xform) {
    this.xform = xform;
  }

  public List<String> getQueryUris() {
    return queryUris;
  }

  public void setQueryUris(List<String> queryUris) {
    this.queryUris = new ArrayList<>(queryUris.size());
    this.queryUris.addAll(queryUris);
  }

  public SearchComposition getSearchComposition() {
    return searchComposition;
  }

  public void setSearchComposition(SearchComposition searchComposition) {
    this.searchComposition = searchComposition;
  }

  public String getHoldingUri() {
    return holdingUri;
  }

  public void setHoldingUri(String holdingUri) {
    this.holdingUri = holdingUri;
  }

  public String getAicUriByName(String name) {
    return aicUriByName.get(name);
  }

  public String getQueryUriByName(String name) {
    return queryUriByName.get(name);
  }

  public void setQueryUriByName(String name, String uri) {
    queryUriByName.put(name, uri);
  }

  public void setAicUriByName(String name, String uri) {
    aicUriByName.put(name, uri);
  }

  public String getContentUri() {
    if (application == null) {
      throw new IllegalStateException("Missing application");
    }
    return application.getUri(InfoArchiveLinkRelations.LINK_CI);
  }

  public CryptoObject getCryptoObject() {
    return cryptoObject;
  }

  public void setCryptoObject(CryptoObject cryptoObject) {
    this.cryptoObject = cryptoObject;
  }

  public String getPdiCryptoUri() {
    return pdiCryptoUri;
  }

  public void setPdiCryptoUri(String pdiCryptoUri) {
    this.pdiCryptoUri = pdiCryptoUri;
  }

  public PdiSchema getPdiSchema() {
    return pdiSchema;
  }

  public void setPdiSchema(PdiSchema pdiSchema) {
    this.pdiSchema = pdiSchema;
  }

}
