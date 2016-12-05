/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

import com.emc.ia.sdk.support.rest.StandardLinkRelations;


public interface InfoArchiveLinkRelations extends StandardLinkRelations {

  String LINK_PREFIX = "http://identifiers.emc.com/";

  String LINK_ADD = LINK_PREFIX + "add";
  String LINK_AICS = LINK_PREFIX + "aics";
  String LINK_AIPS = LINK_PREFIX + "aips";
  String LINK_ALLCOMPONENTS = LINK_PREFIX + "all-components";
  String LINK_APPLICATIONS = LINK_PREFIX + "applications";
  String LINK_CI = LINK_PREFIX + "ci";
  String LINK_CONTENTS = LINK_PREFIX + "contents";
  String LINK_DATABASES = LINK_PREFIX + "xdb-databases";
  String LINK_DIP = LINK_PREFIX + "dip";
  String LINK_DOWNLOAD = LINK_PREFIX + "content-download";
  String LINK_EXPORT = LINK_PREFIX + "export";
  String LINK_EXPORT_CONFIG = LINK_PREFIX + "export-configurations";
  String LINK_EXPORT_PIPELINE = LINK_PREFIX + "export-pipelines";
  String LINK_EXPORT_TRANSFORMATION = LINK_PREFIX + "export-transformations";
  String LINK_EXPORT_TRANSFORMATION_ZIP = LINK_PREFIX + "zip";
  String LINK_FEDERATIONS = LINK_PREFIX + "federations";
  String LINK_FILE_SYSTEM_FOLDERS = LINK_PREFIX + "file-system-folders";
  String LINK_FILE_SYSTEM_ROOTS = LINK_PREFIX + "file-system-roots";
  String LINK_HOLDINGS = LINK_PREFIX + "holdings";
  String LINK_INGEST = LINK_PREFIX + "ingest";
  String LINK_INGEST_DIRECT = LINK_PREFIX + "ingest-direct";
  String LINK_INGEST_NODES = LINK_PREFIX + "ingest-nodes";
  String LINK_INGESTS = LINK_INGEST + "s";
  String LINK_JOB_CONFIRMATION = "?spel=?[name=='Confirmation']";
  String LINK_JOB_DEFINITIONS = LINK_PREFIX + "job-definitions";
  String LINK_JOB_INSTANCES = LINK_PREFIX + "job-instances";
  String LINK_LIBRARIES = LINK_PREFIX + "xdb-libraries";
  String LINK_PDI_SCHEMAS = LINK_PREFIX + "pdi-schemas";
  String LINK_PDIS = LINK_PREFIX + "pdis";
  String LINK_QUERIES = LINK_PREFIX + "queries";
  String LINK_QUERY_QUOTAS = LINK_PREFIX + "query-quotas";
  String LINK_RECEIVER_NODES = LINK_PREFIX + "receiver-nodes";
  String LINK_RESULT_CONFIGURATION_HELPERS = LINK_PREFIX + "result-configuration-helpers";
  String LINK_RETENTION_POLICIES = LINK_PREFIX + "retention-policies";
  String LINK_SEARCH_COMPOSITIONS = LINK_PREFIX + "search-compositions";
  String LINK_SEARCHES = LINK_PREFIX + "searches";
  String LINK_SPACE_ROOT_FOLDERS = LINK_PREFIX + "space-root-folders";
  String LINK_SPACE_ROOT_LIBRARIES = LINK_PREFIX + "space-root-xdb-libraries";
  String LINK_SPACES = LINK_PREFIX + "spaces";
  String LINK_STORES = LINK_PREFIX + "stores";
  String LINK_TENANT = LINK_PREFIX + "tenant";
  String LINK_XFORMS = LINK_PREFIX + "xforms";

}
