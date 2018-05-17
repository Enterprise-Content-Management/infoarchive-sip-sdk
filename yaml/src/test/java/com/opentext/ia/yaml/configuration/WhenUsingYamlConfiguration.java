/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.atteo.evo.inflector.English;
import org.junit.Test;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


public class WhenUsingYamlConfiguration extends TestCase { // NOPMD

  private static final String VERSION = "version";
  private static final String VERSION_1 = "1.0.0";
  private static final String STORES = "stores";
  private static final String TRANSFORMATION = "transformation";
  private static final String NAME = "name";
  private static final String CONFIGURE = "configure";
  private static final String TYPE = "type";
  private static final String DEFAULT = "default";
  private static final String CONTENT = "content";
  private static final String FORMAT = "format";
  private static final String XML = "xml";
  private static final String TEXT = "text";
  private static final String TENANTS = "tenants";
  private static final String TENANT = "tenant";
  private static final String APPLICATION = "application";
  private static final String APPLICATIONS = "applications";
  private static final String SPACES = "spaces";
  private static final String HOLDINGS = "holdings";
  private static final String CONFIRMATIONS = "confirmations";
  private static final String NAMESPACES = "namespaces";
  private static final String NAMESPACE = "namespace";
  private static final String PREFIX = "prefix";
  private static final String URI = "uri";
  private static final String XQUERIES = "xqueries";
  private static final String QUERY = "query";
  private static final String QUERIES = "queries";
  private static final String XDB_PDI_CONFIGS = "xdbPdiConfigs";
  private static final String OPERANDS = "operands";
  private static final String INGESTS = "ingests";
  private static final String INGEST = "ingest";
  private static final String START_PROCESSOR = "  <processor>%n";
  private static final String END_PROCESSOR = "  </processor>%n";
  private static final String PDIS = "pdis";
  private static final String DATA = "data";
  private static final String INDEXES = "indexes";
  private static final String PATH_VALUE_INDEX = "path.value.index";
  private static final String PATH = "path";
  private static final String FILE_SYSTEM_FOLDERS = "fileSystemFolders";
  private static final String DATABASES = "databases";
  private static final String EXPORT_TRANSFORMATION = "exportTransformation";
  private static final String EXPORT_PIPELINE = "exportPipeline";

  private final YamlMap yaml = new YamlMap();
  private ResourceResolver resourceResolver = ResourceResolver.none();

  private String someName() {
    return randomString(5);
  }

  private String someType() {
    return randomString(8);
  }

  private void normalizeYaml() {
    normalizeYaml(yaml);
  }

  private YamlConfiguration normalizeYaml(YamlMap map) {
    return new YamlConfiguration(map, resourceResolver);
  }

  private void assertValue(String message, String expected, Value actual) {
    assertEquals(message, expected, actual.toString());
  }

  @Test
  public void shouldAddDefaultVersionWhenNotSpecified() {
    normalizeYaml();

    assertValue("Default version", VERSION_1, yaml.get(VERSION));
  }

  @Test
  public void shouldNotOverwriteSpecifiedVersion() {
    YamlConfiguration configuration = new YamlConfiguration("version: 2.0.0");

    assertValue("Version", "2.0.0", configuration.getMap().get(VERSION));
  }

  @Test
  public void shouldReplaceSingularTopLevelObjectWithSequence() throws IOException {
    String name = someName();
    String type = APPLICATION;
    String otherType = someType();
    String value = someName();
    yaml.put(type, new YamlMap().put(NAME, name))
        .put(otherType, Arrays.asList(value));

    normalizeYaml();

    assertValue(NAME, name, yaml.get(English.plural(type), 0, NAME));
    assertValue("Should not be changed", value, yaml.get(otherType, 0));
  }

  @Test
  public void shouldReplaceTopLevelMapOfMapsWithSequence() {
    String name = someName();
    yaml.put(APPLICATIONS, new YamlMap().put(name, new YamlMap().put(TYPE, "ACTIVE_ARCHIVING")));

    normalizeYaml();

    assertValue("Application", name, yaml.get(APPLICATIONS, 0, NAME));
  }

  @Test
  public void shouldConvertEnumValue() {
    yaml.put(APPLICATIONS, Arrays.asList(new YamlMap().put(TYPE, "active archiving")));
    yaml.put(CONFIRMATIONS, Arrays.asList(new YamlMap().put("types", Arrays.asList("receipt", "invalid"))));

    normalizeYaml();

    assertValue(TYPE, "ACTIVE_ARCHIVING", yaml.get(APPLICATIONS, 0, TYPE));
    assertValue("Types", "RECEIPT", yaml.get(CONFIRMATIONS, 0, "types", 0));
  }

  @Test
  public void shouldInsertDefaultReferences() {
    String tenant = someName();
    String application = someName();
    String space = someName();
    String spaceRootFolder = someName();
    String fileSystemRoot = someName();
    String fileSystemFolder = someName();
    String store = someName();
    String cryptoObject = someName();
    String xdbFederation = someName();
    String xdbDatabase = someName();
    yaml.put("cryptoObjects", Arrays.asList(new YamlMap().put(NAME, cryptoObject)));
    yaml.put(TENANTS, Arrays.asList(new YamlMap().put(NAME, tenant)));
    yaml.put(APPLICATIONS, Arrays.asList(
        new YamlMap().put(NAME, someName()),
        new YamlMap().put(NAME, application)
            .put(DEFAULT, true)));
    yaml.put(SPACES, Arrays.asList(new YamlMap().put(NAME, space)));
    yaml.put("spaceRootFolders", Arrays.asList(new YamlMap().put(NAME, spaceRootFolder)));
    yaml.put("fileSystemRoots", Arrays.asList(new YamlMap().put(NAME, fileSystemRoot)));
    yaml.put(FILE_SYSTEM_FOLDERS, Arrays.asList(new YamlMap().put(NAME, fileSystemFolder)));
    yaml.put(STORES, Arrays.asList(new YamlMap().put(NAME, store)));
    yaml.put(DATABASES, Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("holdingCryptoes", Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("xdbFederations", Arrays.asList(new YamlMap().put(NAME, xdbFederation)));
    yaml.put("xdbDatabases", Arrays.asList(new YamlMap().put(NAME, xdbDatabase)));

    normalizeYaml();

    assertValue("Tenant", tenant, yaml.get(APPLICATIONS, 0, TENANT));
    assertValue("Application", application, yaml.get(SPACES, 0, APPLICATION));
    assertValue("Space root folder", spaceRootFolder, yaml.get(FILE_SYSTEM_FOLDERS, 0, "parentSpaceRootFolder"));
    assertValue("xDB federation", xdbFederation, yaml.get("xdbDatabases", 0, "xdbFederation"));
    assertDatabaseStore("xdb", store);
    assertDatabaseStore("ci", store);
    assertDatabaseStore("managedItem", store);
    assertFalse("Should NOT insert CryptoObject",
        yaml.get("holdingCryptoes", 0, "ci").toMap().containsKey("cryptoObject"));
  }

  private void assertDatabaseStore(String storeType, String expected) {
    assertValue(storeType + "Store", expected, yaml.get(DATABASES, 0, storeType + "Store"));
  }

  @Test
  public void shouldNotInsertDefaultReferenceForExplicitNull() {
    yaml.put(TENANTS, Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put(APPLICATIONS, Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(TENANT, null)));

    normalizeYaml();

    assertTrue("Explicit null is overridden with default", yaml.get(APPLICATIONS, 0, TENANT).isEmpty());
  }

  @Test
  public void shouldNotInsertDefaultReferenceForExceptionalCases() {
    // The following are usually null. To prevent having to specify null (almost) everywhere, we simply don't insert
    // references by default for these exceptional cases.
    yaml.put("searchGroups", Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("customPresentationConfigurations", Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("searches", Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("searchCompositions", Arrays.asList(new YamlMap().put(NAME, someName())));

    normalizeYaml();

    assertTrue("Search group should not be inserted", yaml.get("searches", 0, "searchGroup").isEmpty());
    assertTrue("Custom presentation configuration should not be inserted",
        yaml.get("searchCompositions", 0, "customPresentationConfiguration").isEmpty());
  }

  @Test
  public void shouldInsertDefaultValues() {
    yaml.put("exportPipelines", Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put(HOLDINGS, Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put(INGESTS, Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("receiverNodes", Arrays.asList(new YamlMap().put(NAME, someName())));

    normalizeYaml();

    assertTrue("exportPipeline.includesContent", yaml.get("exportPipelines", 0, "includesContent").toBoolean());
    assertValue("holding.xdbMode", "PRIVATE", yaml.get(HOLDINGS, 0, "xdbMode"));
    assertValue("ingest.processors.format", XML, yaml.get(INGESTS, 0, "content", FORMAT));
    assertTrue("ingest.processors.xml", yaml.get(INGESTS, 0, "content", TEXT).toString().contains("sip.download"));
  }

  @Test
  public void shouldReplaceSingularObjectReferenceWithSequenceForReferenceCollections() throws IOException {
    String name = someName();
    yaml.put(HOLDINGS, Arrays.asList(new YamlMap().put(NAME, name)))
        .put(CONFIRMATIONS, Arrays.asList(new YamlMap().put("holding", name)));

    normalizeYaml();

    assertValue("Sequence of references not created", name, yaml.get(CONFIRMATIONS, 0, HOLDINGS, 0));
    assertFalse("Singular reference not removed", yaml.get(CONFIRMATIONS, 0).toMap().containsKey("holding"));
  }

  @Test
  public void shouldReplaceNestedMapOfMapsWithSequence() {
    String query = someName();
    String operand = someName();
    String path = someName();
    yaml.put(QUERIES, Arrays.asList(new YamlMap()
        .put(NAME, query)
        .put(XDB_PDI_CONFIGS, new YamlMap()
            .put(OPERANDS, new YamlMap()
                .put(operand, new YamlMap()
                    .put(PATH, path))))));

    normalizeYaml();

    assertValue(NAME, query, yaml.get(QUERIES, 0, NAME));
    assertValue("Operand", operand, yaml.get(QUERIES, 0, XDB_PDI_CONFIGS, OPERANDS, 0, NAME));
    assertValue("Path", path, yaml.get(QUERIES, 0, XDB_PDI_CONFIGS, OPERANDS, 0, PATH));
  }

  @Test
  public void shouldAddNamespaceDeclarationsToXqueryFields() {
    String prefix = "n";
    String uri = randomUri();
    String text = "current-dateTime()";
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix)
            .put(URI, uri)))
        .put("xdbLibraryPolicies", Arrays.asList(new YamlMap()
            .put(NAME, someName())
            .put("closeHintDateQuery", new YamlMap()
                .put(TEXT, text))));

    normalizeYaml();

    assertValue("Query", String.format("declare namespace %s = \"%s\";%n%s", prefix, uri, text),
        yaml.get("xdbLibraryPolicies", 0, "closeHintDateQuery"));
  }

  @Test
  public void shouldAddNamespaceDeclarationsToXqueryObjects() {
    String prefix = "n";
    String uri = randomUri();
    String text = "current-dateTime()";
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix)
            .put(URI, uri)))
        .put(XQUERIES, Arrays.asList(new YamlMap()
            .put(NAME, someName())
            .put(QUERY, new YamlMap()
                .put(TEXT, text))));

    normalizeYaml();

    assertValue("Query", String.format("declare namespace %s = \"%s\";%n%s", prefix, uri, text),
        yaml.get(XQUERIES, 0, QUERY));
  }

  @Test
  public void shouldReplaceXqueryQueryObjectWithText() {
    String text = "current-dateTime()";
    yaml.put(XQUERIES, Arrays.asList(new YamlMap()
            .put(NAME, someName())
            .put(QUERY, new YamlMap()
                .put(TEXT, text))));

    normalizeYaml();

    assertValue("Query", text, yaml.get(XQUERIES, 0, QUERY));
  }

  @Test
  public void shouldReplacePdiSchemaNamespaceWithName() {
    String prefix = "n";
    String uri = randomUri();
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix)
            .put(URI, uri)))
        .put("pdiSchemas", Arrays.asList(new YamlMap()
            .put(CONTENT, new YamlMap()
                .put(FORMAT, XML))));

    normalizeYaml();

    assertValue(String.format("Name%n%s", yaml), uri, yaml.get("pdiSchemas", 0, NAME));
    assertTrue(String.format("Leaves namespace:%n%s", yaml), yaml.get("pdiSchemas", 0, NAMESPACE).isEmpty());
  }

  @Test
  public void shouldTranslatePdiYamlToXml() {
    String prefix1 = "n";
    String uri1 = randomUri();
    String prefix2 = "ex";
    String uri2 = randomUri();
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix1)
            .put(URI, uri1),
        new YamlMap()
            .put(PREFIX, prefix2)
            .put(URI, uri2)))
        .put(PDIS, Arrays.asList(new YamlMap()
            .put(NAME, someName())
            .put(CONTENT, new YamlMap()
                .put(FORMAT, "yaml")
                .put(DATA, Arrays.asList(new YamlMap()
                    .put("id", "pdi.index.creator")
                    .put("key.document.name", "xdb.pdi.name")
                    .put(INDEXES, Arrays.asList(new YamlMap()
                        .put(someName(), new YamlMap()
                            .put(TYPE, PATH_VALUE_INDEX)
                            .put(PATH, "/n:gnu/n:gnat")), new YamlMap()
                        .put(someName(), new YamlMap()
                            .put(TYPE, PATH_VALUE_INDEX)
                            .put(PATH, "/n:foo/n:bar[n:baz]")), new YamlMap()
                        .put(someName(), new YamlMap()
                            .put(TYPE, "full.text.index")))),
                new YamlMap()
                    .put("id", "pdi.transformer")
                    .put("result.schema", prefix2)
                    .put("level", 2))))));

    normalizeYaml();

    String xml = yaml.get(PDIS, 0, CONTENT, TEXT).toString();
    assertTrue("path #1", xml.contains(String.format("/{%1$s}gnu/{%1$s}gnat", uri1)));
    assertTrue("path #2", xml.contains(String.format("/{%1$s}foo/{%1$s}bar[{%1$s}baz]", uri1)));
    assertTrue("Default compressed", xml.contains("<compressed>false</compressed>"));
    assertTrue("Default filter.english.stop.words", xml.contains("<filter.english.stop.words>false</filter.english.stop.words>"));
    assertTrue("Schema", xml.contains(String.format("<result.schema>%s</result.schema>", uri2)));
  }

  @Test
  public void shouldTranslateResultConfigurationHelperYamlToXml() {
    yaml.put(NAMESPACES, Arrays.asList(
        new YamlMap()
            .put(PREFIX, "n")
            .put(URI, "urn:eas-samples:en:xsd:phonecalls.1.0")
            .put(DEFAULT, true),
        new YamlMap()
            .put(PREFIX, "pdi")
            .put(URI, "urn:x-emc:ia:schema:pdi")))
        .put("resultConfigurationHelper", new YamlMap()
            .put(NAME, "PhoneCalls-result-configuration-helper")
            .put("propagateChanges", false)
            .put(CONTENT, new YamlMap()
                .put(FORMAT, "yaml")
                .put(NAMESPACES, Arrays.asList("n", "pdi"))
                .put(DATA, Arrays.asList(
                    new YamlMap()
                        .put("id", new YamlMap()
                            .put("label", "ID")
                            .put(PATH, "@pdi:id")
                            .put(TYPE, "id")),
                    new YamlMap()
                        .put("SentToArchiveDate", new YamlMap()
                            .put("label", "Sent to")
                            .put(PATH, "n:SentToArchiveDate")
                            .put(TYPE, "date time"))))));

    normalizeYaml();

    YamlMap content = yaml.get("resultConfigurationHelpers", 0, CONTENT).toMap();
    assertValue("Format", XML, content.get(FORMAT));
    assertTrue("Namespaces are still there", content.get(NAMESPACES).isEmpty());
    String xml = content.get(TEXT).toString();
    assertEquals("XML", String.format(
        "<resultConfigurationHelper xmlns:n=\"urn:eas-samples:en:xsd:phonecalls.1.0\" xmlns:pdi=\"urn:x-emc:ia:schema:pdi\">%n"
        + "  <element>%n"
        + "    <label>ID</label>%n"
        + "    <name>id</name>%n"
        + "    <path>@pdi:id</path>%n"
        + "    <type>ID</type>%n"
        + "  </element>%n"
        + "  <element>%n"
        + "    <label>Sent to</label>%n"
        + "    <name>SentToArchiveDate</name>%n"
        + "    <path>n:SentToArchiveDate</path>%n"
        + "    <type>DATE_TIME</type>%n"
        + "  </element>%n"
        + "</resultConfigurationHelper>"), xml);
  }


  @Test
  public void shouldTranslateIngestYamlToXml() {
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, "ri")
            .put(URI, "urn:x-emc:ia:schema:ri")))
        .put(INGEST, new YamlMap()
            .put(NAME, "PhoneCalls-ingest")
            .put(CONTENT, new YamlMap()
                .put(FORMAT, "yaml")
                .put("processors", Arrays.asList(
                    new YamlMap()
                        .put("id", "sip.download"),
                    new YamlMap()
                        .put("id", "pdi.index.creator")
                        .put(DATA, new YamlMap()
                            .put("key.document.name", "xdb.pdi.name")
                            .put(INDEXES, null)),
                    new YamlMap()
                        .put("id", "ri.index")
                        .put(DATA, new YamlMap()
                            .put("key.document.name", "xdb.ri.name")
                            .put(INDEXES, new YamlMap()
                                .put("key", new YamlMap()
                                    .put(TYPE, PATH_VALUE_INDEX)
                                    .put(PATH, "/ri:ris/ri:ri[@key<STRING>]")))),
                    new YamlMap()
                        .put("id", "ci.hash")
                        .put(DATA, new YamlMap()
                            .put("select.query", new YamlMap()
                                .put(NAMESPACE, "ri")
                                .put(TEXT, String.format("let $uri := replace(document-uri(.), '\\.pdi$', '.ri')%n"
                                    + "for $c in doc($uri)/ri:ris/ri:ri%n"
                                    + "return <content filename=\"{ $c/@key }\">%n"
                                    + "  <hash encoding=\"hex\" algorithm=\"SHA-1\" provided=\"false\" />%n"
                                    + "</content>"))))))));

    normalizeYaml();

    YamlMap content = yaml.get(INGESTS, 0, CONTENT).toMap();
    assertValue("Format", XML, content.get(FORMAT));
    String xml = content.get(TEXT).toString();
    assertEquals("XML", String.format("<processors>%n"
        + START_PROCESSOR
        + "    <class>com.emc.ia.ingestion.processor.downloader.SipContentDownloader</class>%n"
        + "    <id>sip.download</id>%n"
        + "    <name>SIP downloader processor</name>%n"
        + END_PROCESSOR
        + START_PROCESSOR
        + "    <class>com.emc.ia.ingestion.processor.index.IndexesCreator</class>%n"
        + "    <data>%n"
        + "      <indexes/>%n"
        + "      <key.document.name>xdb.pdi.name</key.document.name>%n"
        + "    </data>%n"
        + "    <id>pdi.index.creator</id>%n"
        + "    <name>XDB PDI index processor</name>%n"
        + END_PROCESSOR
        + START_PROCESSOR
        + "    <class>com.emc.ia.ingestion.processor.index.IndexesCreator</class>%n"
        + "    <data>%n"
        + "      <indexes>%n"
        + "        <path.value.index>%n"
        + "          <build.without.logging>false</build.without.logging>%n"
        + "          <compressed>false</compressed>%n"
        + "          <concurrent>false</concurrent>%n"
        + "          <name>key</name>%n"
        + "          <path>/{urn:x-emc:ia:schema:ri}ris/{urn:x-emc:ia:schema:ri}ri[@key&lt;STRING>]</path>%n"
        + "          <unique.keys>true</unique.keys>%n"
        + "        </path.value.index>%n"
        + "      </indexes>%n"
        + "      <key.document.name>xdb.ri.name</key.document.name>%n"
        + "    </data>%n"
        + "    <id>ri.index</id>%n"
        + "    <name>RI XDB indexes</name>%n"
        + END_PROCESSOR
        + START_PROCESSOR
        + "    <class>com.emc.ia.ingestion.processor.content.CiHashProcessor</class>%n"
        + "    <data>%n"
        + "      <select.query><![CDATA[%n"
        + "        declare namespace ri = \"urn:x-emc:ia:schema:ri\";%n"
        + "        let $uri := replace(document-uri(.), '\\.pdi$', '.ri')%n"
        + "        for $c in doc($uri)/ri:ris/ri:ri%n"
        + "        return <content filename=\"{ $c/@key }\">%n"
        + "          <hash encoding=\"hex\" algorithm=\"SHA-1\" provided=\"false\" />%n"
        + "        </content>%n"
        + "      ]]></select.query>%n"
        + "    </data>%n"
        + "    <id>ci.hash</id>%n"
        + "    <name>CI hash generator and validator</name>%n"
        + END_PROCESSOR
        + "</processors>"), xml);
  }

  @Test
  public void shouldExpandNamespaceInResultMaster() {
    String prefix = "n";
    String uri = randomUri();
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix)
            .put(URI, uri)))
        .put("resultMasters", Arrays.asList(new YamlMap()
            .put(NAME, someName())
            .put(NAMESPACES, Arrays.asList(prefix))));

    normalizeYaml();

    YamlMap namespace = yaml.get("resultMasters", 0, NAMESPACES, 0).toMap();
    assertValue("Namespace prefix", prefix, namespace.get("prefix"));
    assertValue("Namespace URI", uri, namespace.get("uri"));
  }

  @Test
  public void shouldExpandNamespaceInQuery() {
    String prefix = "n";
    String uri = randomUri();
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix)
            .put(URI, uri)))
        .put("queries", Arrays.asList(new YamlMap()
            .put(NAME, someName())
            .put(NAMESPACES, Arrays.asList(prefix))));

    normalizeYaml();

    YamlMap namespace = yaml.get("queries", 0, NAMESPACES, 0).toMap();
    assertValue("Namespace prefix", prefix, namespace.get("prefix"));
    assertValue("Namespace URI", uri, namespace.get("uri"));
  }

  @Test
  public void shouldNotAddReferencesToPseudoContent() {
    addNamedObjectsFor(APPLICATION, "store");
    addPseudoContent(EXPORT_PIPELINE, EXPORT_TRANSFORMATION, "valueList");

    normalizeYaml();

    Arrays.asList(APPLICATION, "store").forEach(ref -> {
      Arrays.asList(EXPORT_PIPELINE, EXPORT_TRANSFORMATION, "valueList").forEach(type -> {
        assertTrue("Incorrect: " + ref + " in " + type + "'s content",
            yaml.get(English.plural(type), 0, CONTENT, ref).isEmpty());
      });
    });
  }

  private void addNamedObjectsFor(String... types) {
    for (String type : types) {
      yaml.put(English.plural(type), Arrays.asList(new YamlMap().put(NAME, someName())));
    }
  }

  private void addPseudoContent(String... types) {
    for (String type : types) {
      yaml.put(English.plural(type), Arrays.asList(new YamlMap().put(CONTENT, new YamlMap().put(TEXT, someName()))));
    }
  }

  @Test
  public void shouldInsertDefaultPipelineAndTransformationIntoExportConfiguration() {
    addNamedObjectsFor(TENANT, APPLICATION, EXPORT_PIPELINE, EXPORT_TRANSFORMATION, "exportConfiguration");

    normalizeYaml();

    Arrays.asList("pipeline", TRANSFORMATION).forEach(property ->
          assertFalse("Missing " + property, yaml.get("exportConfigurations", 0, property).isEmpty()));
  }

  @Test
  public void shouldEnableAuditEventByDefault() {
    yaml.put("auditEvents", Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(TYPE, someName()), new YamlMap()
        .put(NAME, someName())
        .put(TYPE, someName())
        .put("enabled", false)));

    normalizeYaml();

    assertTrue("Not enabled by default", yaml.get("auditEvents", 0, "enabled").toBoolean());
    assertFalse("Overridden specified value", yaml.get("auditEvents", 1, "enabled").toBoolean());
  }

  @Test
  public void shouldNotInsertDefaultValueWhenNotConfiguring() {
    yaml.put("fileSystemRoots", Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(CONFIGURE, "use existing")));

    normalizeYaml();

    assertEquals("Keys", Arrays.asList(CONFIGURE, NAME), yaml.get("fileSystemRoots", 0).toMap().entries()
        .sorted()
        .map(Entry::getKey)
        .collect(Collectors.toList()));
  }

  @Test
  public void shouldSubstituteProperties() {
    String collection = English.plural(someName());
    String intProperty = someName();
    int intValue = randomInt(13, 42);
    resourceResolver = ResourceResolver.fromClasspath();
    yaml.put(collection, Arrays.asList(new YamlMap().put(NAME, "${qux}").put(intProperty, intValue)));

    normalizeYaml();

    assertValue("Substituted value", "thud", yaml.get(collection, 0, NAME));
    assertEquals("Ignored value", intValue, yaml.get(collection, 0, intProperty).toInt());
  }

  @Test
  public void shouldResolvePropertiesToDefaultsWhenNoValuesProvided() {
    resourceResolver = ResourceResolver.none();
    yaml.put("gnus", Arrays.asList(new YamlMap().put("gnat", "${waldo:fred}")));

    normalizeYaml(yaml);

    assertValue("Resolved value", "fred", yaml.get("gnus", 0, "gnat"));
  }

}
