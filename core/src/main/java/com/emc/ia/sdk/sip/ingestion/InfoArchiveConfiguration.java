/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;


public interface InfoArchiveConfiguration {

  String PREFIX = "ia.";
  String NAME = "name";

  String SERVER_PREFIX = PREFIX + "server.";
  String SERVER_AUTENTICATON_TOKEN = SERVER_PREFIX + "authentication.token";
  String SERVER_URI = SERVER_PREFIX + "uri";

  String TENANT_NAME = PREFIX + "tenant." + NAME;

  String XDB_PREFIX = PREFIX + "xdb.";
  String FEDERATION_PREFIX = XDB_PREFIX + ".federation";
  String FEDERATION_NAME = FEDERATION_PREFIX + NAME;
  String FEDERATION_SUPERUSER_PASSWORD = FEDERATION_PREFIX + "superuser.password";
  String FEDERATION_BOOTSTRAP = FEDERATION_PREFIX + "bootstrap.uri";

  String XDB_DATABASE = XDB_PREFIX + "database";
  String XDB_DATABASE_NAME = XDB_DATABASE + NAME;
  String XDB_DATABASE_ADMIN_PASSWORD = XDB_DATABASE + "admin.password";

  String APPLICATION_PREFIX = PREFIX + "applicaton.";
  String APPLICATION_NAME = APPLICATION_PREFIX + NAME;

  String HOLDING_PREFIX = PREFIX + "holding";
  String HOLDING_NAME = HOLDING_PREFIX + NAME;

}
