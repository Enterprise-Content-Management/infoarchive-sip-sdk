/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.opentext.ia.configuration.json.JsonConfiguration;
import com.opentext.ia.configuration.json.JsonConfigurationProducer;

class WhenBuildingConfigurations {

  private static final String POSTGRES = "postgres";
  private static final Pattern NAME_PATTERN = Pattern.compile("[a-z]{1,3}_(?<uuid>.*)");
  private static final String NAME = "name";
  private static final String TYPE = "type";
  private static final String DESCRIPTION = "description";
  private static final String DEFAULT_TENANT_NAME = "INFOARCHIVE";
  private static final String TENANT_NAME = "myTenant";
  private static final String APPLICATION = "application";
  private static final String APPLICATION_NAME = "myApplication";
  private static final String SEARCH_NAME = "mySearch";
  private static final String DESCRIPTIVE_TEXT = "myDescription";
  private static final String CATEGORY = "myCategory";
  private static final String STATE = "state";
  private static final String DRAFT = "DRAFT";
  private static final String FILE_SYSTEM_ROOT_NAME = "myFileSystemRoot";
  private static final String PATH = "path";
  private static final String SOME_PATH = "/path/to/some/place";
  private static final String SPACE_NAME = "mySpace";
  private static final String SPACE_ROOT_RDB_DATABASE_NAME = "mySpaceRootRdbDatabase";

  private static final String PDI_SCHEMA_NAME = "myPdiSchema";
  private static final String PDI_SCHEMA_FORMAT = "rnc";
  private static final String PDI_SCHEMA_TEXT = "element addressBook {\n" + "  element card {\n"
      + "    element name { text },\n" + "    element email { text }\n" + "  }*\n" + "}";
  private static final String PDI_NAME = "myPdi";
  private static final String SPACE_ROOT_FOLDER_NAME = "mySpaceRootFolder";
  private static final String HOLDING_NAME = "myHolding";
  private static final String LIBRARY_MODE = "libraryMode";
  private static final String DEFAULT_LIBRARY_MODE = "PRIVATE";
  private static final String CRYPTO_OBJECT_NAME = "myCryptoObject";
  private static final String RDB_DATANODE_NAME = "myRdbDataNodeName";

  private static final String SUPER_USER_PASSWORD = "superUserPassword";
  private static final String SOME_PASSWORD = "super_secret";
  private static final String RDB_BOOTSTRAP = "jdbc:postgresql://localhost:5432";

  private static final String RDB_DATABASE_NAME = "myRdbDatabase";


  private final ConfigurationProducer<JsonConfiguration> producer = new JsonConfigurationProducer();
  private final ConfigurationBuilder<JsonConfiguration> builder = new ConfigurationBuilder<>(producer);

  @Test
  void shouldUseDefaultPropertiesForTenant() {
    Configuration<ConfigurationObject> configuration = builder.withTenant().build();

    assertEquals(DEFAULT_TENANT_NAME, configuration.getTenant().getProperties().getString(NAME), NAME);
  }

  @Test
  void shouldSetPropertiesOfTenant() {
    Configuration<ConfigurationObject> configuration = builder.withTenant().named(TENANT_NAME).build();

    assertEquals(TENANT_NAME, nameOf(configuration.getTenant()), NAME);
  }

  private String nameOf(ConfigurationObject object) {
    return object.getProperties().getString(NAME);
  }

  @Test
  void shouldUseDefaultPropertiesForApplication() {
    Configuration<ConfigurationObject> configuration = builder.withApplication().build();
    ConfigurationObject application = configuration.getApplication();

    assertRandomName(application);
    assertProperties(application, "tenant", DEFAULT_TENANT_NAME, TYPE, "ACTIVE_ARCHIVING", "archiveType", "SIP", STATE,
        "IN_TEST", "retentionEnabled", Boolean.TRUE, "configure", "");
  }

  private void assertRandomName(ConfigurationObject actual) {
    Matcher matcher = NAME_PATTERN.matcher(nameOf(actual));
    if (matcher.matches()) {
      assertUuid(NAME, matcher.group("uuid"));
    } else {
      fail("Not a random name");
    }
  }

  private void assertUuid(String message, String actual) {
    String uuid = actual.contains("-") ? actual
        : String.format("%s-%s-%s-%s-%s", actual.substring(0, 8), actual.substring(8, 12), actual.substring(12, 16),
            actual.substring(16, 20), actual.substring(20));
    try {
      UUID.fromString(uuid);
    } catch (IllegalArgumentException e) {
      fail(message + " is not a UUID: " + actual);
    }
  }

  private void assertProperties(ConfigurationObject actual, Object... expectedPropertyValues) {
    JSONObject properties = actual.getProperties();
    for (int i = 0; i < expectedPropertyValues.length; i += 2) {
      String property = expectedPropertyValues[i].toString();
      assertProperty(expectedPropertyValues[i + 1].toString(), properties, property);
    }
  }

  private void assertProperty(String expected, JSONObject object, String name) {
    assertEquals(expected, object.optString(name), name);
  }

  @Test
  void shouldSetPropertiesOfApplication() {
    Configuration<ConfigurationObject> configuration =
        builder.withTenant().named(TENANT_NAME).withApplication().named(APPLICATION_NAME).configure("create")
            .forAppDecom().forTables().activated().withDescription(DESCRIPTIVE_TEXT).withCategory(CATEGORY).build();

    ConfigurationObject application = configuration.getApplication();
    assertProperties(application, "tenant", TENANT_NAME, NAME, APPLICATION_NAME, TYPE, "APP_DECOMM", "archiveType",
        "TABLE", STATE, "ACTIVE", DESCRIPTION, DESCRIPTIVE_TEXT, "category", CATEGORY, "configure", "create");
  }

  @Test
  void shouldThrowExceptionWhenAskedForMissingItem() {
    assertThrows(IllegalArgumentException.class, () -> builder.withTenant().build().getApplication());
  }

  @Test
  void shouldUseDefaultPropertiesForSearch() {
    Configuration<ConfigurationObject> configuration = builder.withSearch().build();
    ConfigurationObject search = configuration.getSearch();

    assertRandomName(search);
    assertProperties(search, STATE, DRAFT);
  }

  @Test
  void shouldSetPropertiesForSearch() {
    Configuration<ConfigurationObject> configuration = builder.withApplication().named(APPLICATION_NAME).withSearch()
        .named(SEARCH_NAME).withDescription(DESCRIPTIVE_TEXT).published().build();
    ConfigurationObject search = configuration.getSearch();

    assertProperties(search, APPLICATION, APPLICATION_NAME, NAME, SEARCH_NAME, STATE, "PUBLISHED", DESCRIPTION,
        DESCRIPTIVE_TEXT);
  }

  @Test
  void shouldUseDefaultPropertiesForFileSystemRoot() {
    Configuration<ConfigurationObject> configuration = builder.withFileSystemRoot().build();
    ConfigurationObject fileSystemRoot = configuration.getFileSystemRoot();

    assertRandomName(fileSystemRoot);
    assertProperties(fileSystemRoot, PATH, "/data/root", TYPE, "FILESYSTEM");
  }

  @Test
  void shouldSetPropertiesForFileSystemRoot() {
    Configuration<ConfigurationObject> configuration = builder.withFileSystemRoot().named(FILE_SYSTEM_ROOT_NAME)
        .withDescription(DESCRIPTIVE_TEXT).at(SOME_PATH).onIsilon().build();
    ConfigurationObject fileSystemRoot = configuration.getFileSystemRoot();

    assertProperties(fileSystemRoot, NAME, FILE_SYSTEM_ROOT_NAME, DESCRIPTION, DESCRIPTIVE_TEXT, PATH, SOME_PATH, TYPE,
        "ISILON");
  }

  @Test
  void shouldUseDefaultPropertiesForSpace() {
    Configuration<ConfigurationObject> configuration = builder.withSpace().build();
    ConfigurationObject space = configuration.getSpace();

    assertRandomName(space);
  }

  @Test
  void shouldSetPropertiesForSpace() {
    Configuration<ConfigurationObject> configuration =
        builder.withApplication().named(APPLICATION_NAME).withSpace().named(SPACE_NAME).build();
    ConfigurationObject space = configuration.getSpace();

    assertProperties(space, APPLICATION, APPLICATION_NAME, NAME, SPACE_NAME);
  }

  @Test
  void shouldUseDefaultPropertiesForSpaceRootFolder() {
    Configuration<ConfigurationObject> configuration = builder.withFileSystemRoot().named(FILE_SYSTEM_ROOT_NAME).end()
        .withSpace().withSpaceRootFolder(FILE_SYSTEM_ROOT_NAME).build();
    ConfigurationObject spaceRootFolder = configuration.getSpaceRootFolder(configuration.getSpace());

    assertRandomName(spaceRootFolder);
    assertProperties(spaceRootFolder, "fileSystemRoot", FILE_SYSTEM_ROOT_NAME);
  }

  @Test
  void shouldSetPropertiesForSpaceRootFolder() {
    Configuration<ConfigurationObject> configuration = builder.withFileSystemRoot().named(FILE_SYSTEM_ROOT_NAME).end()
        .withSpace().named(SPACE_NAME).withSpaceRootFolder(FILE_SYSTEM_ROOT_NAME).named(SPACE_ROOT_FOLDER_NAME).build();
    ConfigurationObject spaceRootFolder = configuration.getSpaceRootFolder(configuration.getSpace());

    assertProperties(spaceRootFolder, "space", SPACE_NAME, NAME, SPACE_ROOT_FOLDER_NAME);
  }

  @Test
  void shouldUseDefaultPropertiesForSpaceRootRdbDatabase() {
    Configuration<ConfigurationObject> configuration = builder.withSpace().withSpaceRootRdbDatabase().build();
    ConfigurationObject spaceRootRdbDatabase = configuration.getSpaceRootRdbDatabase(configuration.getSpace());

    assertRandomName(spaceRootRdbDatabase);
  }

  @Test
  void shouldSetPropertiesForSpaceRootRdbDatabase() {
    Configuration<ConfigurationObject> configuration =
        builder.withSpace().named(SPACE_NAME).withSpaceRootRdbDatabase().named(SPACE_ROOT_RDB_DATABASE_NAME).build();
    ConfigurationObject spaceRootRdbDatabase = configuration.getSpaceRootRdbDatabase(configuration.getSpace());

    assertProperties(spaceRootRdbDatabase, "space", SPACE_NAME, NAME, SPACE_ROOT_RDB_DATABASE_NAME);
  }

  @Test
  void shouldUseDefaultPropertiesForPdiSchema() {
    Configuration<ConfigurationObject> configuration = builder.withApplication().withPdiSchema().build();
    ConfigurationObject pdiSchema = configuration.getPdiSchema(configuration.getApplication());

    assertRandomName(pdiSchema);
  }

  @Test
  void shouldSetPropertiesForPdiSchema() {
    Configuration<ConfigurationObject> configuration = builder.withApplication().named(APPLICATION_NAME).withPdiSchema()
        .named(PDI_SCHEMA_NAME).withContent().ofType(PDI_SCHEMA_FORMAT).as(PDI_SCHEMA_TEXT).end().end().build();
    ConfigurationObject pdiSchema = configuration.getPdiSchema(configuration.getApplication());

    assertProperties(pdiSchema, APPLICATION, APPLICATION_NAME, NAME, PDI_SCHEMA_NAME, "format", PDI_SCHEMA_FORMAT,
        "content", PDI_SCHEMA_TEXT);
  }

  @Test
  void shouldUseDefaultPropertiesForHolding() {
    Configuration<ConfigurationObject> configuration = builder.withHolding().build();
    ConfigurationObject holding = configuration.getHolding();

    assertRandomName(holding);
    assertProperties(holding, LIBRARY_MODE, DEFAULT_LIBRARY_MODE);
  }

  @Test
  void shouldSetPropertiesForHolding() {
    Configuration<ConfigurationObject> configuration =
        builder.withApplication().named(APPLICATION_NAME).withHolding().named(HOLDING_NAME).inPool().build();
    ConfigurationObject holding = configuration.getHolding();

    assertProperties(holding, APPLICATION, APPLICATION_NAME, NAME, HOLDING_NAME, LIBRARY_MODE, "POOLED");
  }

  @Test
  void shouldUseDefaultPropertiesForCryptoObject() {
    Configuration<ConfigurationObject> configuration = builder.withCryptoObject().build();
    ConfigurationObject cryptoObject = configuration.getCryptoObject();

    assertRandomName(cryptoObject);
    assertProperties(cryptoObject, "securityProvider", "Bouncy Castle", "keySize", "256", "encryptionMode", "CBC",
        "paddingScheme", "PKCS5PADDING", "encryptionAlgorithm", "AES");
  }

  @Test
  void shouldSetPropertiesForCryptoObject() {
    Configuration<ConfigurationObject> configuration = builder.withCryptoObject().named(CRYPTO_OBJECT_NAME)
        .providedBy("SunJCE").withKeysOfSize(192).combiningBlocksUsing("OFB").paddedBy("RSA/ECB/PKCS1Padding")

        .build();
    ConfigurationObject cryptoObject = configuration.getCryptoObject();

    assertProperties(cryptoObject, NAME, CRYPTO_OBJECT_NAME, "securityProvider", "SunJCE", "keySize", "192",
        "encryptionMode", "OFB", "paddingScheme", "RSA/ECB/PKCS1Padding");
  }

  @Test
  void shouldUseDefaultPropertiesForRdbDataNode() {
    Configuration<ConfigurationObject> configuration = builder.withRdbDataNode().build();
    ConfigurationObject rdbDataNode = configuration.getRdbDataNode();

    assertRandomName(rdbDataNode);
    assertProperties(rdbDataNode, "bootstrap", "jdbc:postgresql://localhost:5432",
        SUPER_USER_PASSWORD, "");
  }

  @Test
  void shouldSetPropertiesForRdbDataNode() {
    Configuration<ConfigurationObject> configuration = builder.withRdbDataNode().named(RDB_DATANODE_NAME)
        .runningAt(RDB_BOOTSTRAP).withUserName(POSTGRES).protectedWithPassword(SOME_PASSWORD).build();
    ConfigurationObject rdbDataNode = configuration.getRdbDataNode();

    assertProperties(rdbDataNode, NAME, RDB_DATANODE_NAME, "bootstrap", RDB_BOOTSTRAP, "userName", POSTGRES, SUPER_USER_PASSWORD,
        SOME_PASSWORD);
  }

  @Test
  void shouldSetCryptoObject() {
    Configuration<ConfigurationObject> configuration = builder.withCryptoObject().named(CRYPTO_OBJECT_NAME).end()
        .withRdbDataNode().encryptedBy(CRYPTO_OBJECT_NAME).end().build();
    ConfigurationObject rdbDataNode = configuration.getRdbDataNode();

    assertProperties(rdbDataNode, "cryptoObject", CRYPTO_OBJECT_NAME);
  }

  @Test
  void shouldUseDefaultPropertiesForRdbDatabase() {
    Configuration<ConfigurationObject> configuration = builder.withRdbDatabase().build();
    ConfigurationObject rdbDatabase = configuration.getRdbDatabase();

    assertRandomName(rdbDatabase);
    assertProperties(rdbDatabase, "adminPassword", "");

  }

  @Test
  void shouldSetPropertiesForRdbDatabase() {
    Configuration<ConfigurationObject> configuration =
        builder.withRdbDatabase().named(RDB_DATABASE_NAME).withAdminUser(POSTGRES).protectedWithAdminPassword(SOME_PASSWORD).build();
    ConfigurationObject rdbDatabase = configuration.getRdbDatabase();

    assertProperties(rdbDatabase, NAME, RDB_DATABASE_NAME, "adminUser", POSTGRES, "adminPassword", SOME_PASSWORD);
  }

  @Test
  void shouldUseDefaultPropertiesForPdi() {
    Configuration<ConfigurationObject> configuration = builder.withApplication().withPdi().build();
    ConfigurationObject pdi = configuration.getPdi(configuration.getApplication());

    assertRandomName(pdi);
  }

  @Test
  void shouldSetPropertiesForPdi() {
    Configuration<ConfigurationObject> configuration =
        builder.withApplication().named(APPLICATION_NAME).withPdi().named(PDI_NAME).build();
    ConfigurationObject pdi = configuration.getPdi(configuration.getApplication());

    assertProperties(pdi, APPLICATION, APPLICATION_NAME, NAME, PDI_NAME);
  }

}
