package com.emc.ia.sip.sample.hello;

//Sample Ingest

import com.emc.ia.sdk.configurer.ArchiveClients;
import com.emc.ia.sdk.configurer.PropertyBasedConfigurer;
import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.rest.RestCache;
import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.DefaultClock;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.rest.RestClient;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.*;

public class IngestSample {

    //Change this Authentication token as required. InfoArchive local set up is needed with default ports.
    public static final String TOKEN = "SampleAuthenticationToken-please generate your authentication token and replace this text";
    private static final String BILLBOARD_URI = "http://localhost:8765/services";
    HttpClient httpClient = new ApacheHttpClient();
    private final RestCache configurationState = new RestCache();
    private static Map<String, String> configuration = new HashMap<>();
    private RestClient restClient;
    private static Clock clock = new DefaultClock();
    PropertyBasedConfigurer propertyBasedConfigurer;
    private static ArchiveClient archiveClient;
    private static final String SOURCE = "/sample.zip";
    public void setup() throws IOException {
        propertyBasedConfigurer = new PropertyBasedConfigurer(restClient, clock, configuration);
        propertyBasedConfigurer.configure();
        archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
        InputStream is = getClass().getResourceAsStream(SOURCE);
        String response = archiveClient.ingest(is);
        System.out.println("response is " + response);
    }

    private void prepareConfiguration() throws IOException {
        configuration.put(SERVER_URI, BILLBOARD_URI);   //initRestClient
        System.out.println("passing server uri is is::" + configuration.get(SERVER_URI));
        configuration.put(SERVER_AUTENTICATON_TOKEN, TOKEN);
        System.out.println("Auth Token::" + configuration.get(SERVER_AUTENTICATON_TOKEN));
        configuration.put(FEDERATION_NAME, "mainFederation");    //ensureFederation
        configuration.put(FEDERATION_SUPERUSER_PASSWORD, "test");
        configuration.put(FEDERATION_BOOTSTRAP, "xhive://localhost:2910");
        configuration.put(DATABASE_NAME, "sample-xdb2");    //ensureDatabase
        configuration.put(DATABASE_ADMIN_PASSWORD, "secret");
        configuration.put(APPLICATION_NAME, "SampleSipSdkIngest"); //ensureApplication
        configuration.put(HOLDING_NAME, "SampleSipSdkIngest"); //ensureSpaceRootLibrary: HOLDING_NAME
        configuration.put(FILE_SYSTEM_FOLDER, "SampleSipSdkIngest-folder,SampleSipSdkIngest-result-folder,SampleSipSdkIngest-confirmation-folder");   //ensureFileSystemFolder: HOLDING_NAME,FILE_SYSTEM_FOLDER
        configuration.put(STORE_NAME, "result_store,confirmation_store_01");    //ensureStore: DEFAULT_STORE_NAME
        configuration.put(STORE_FOLDER, "result_store,confirmation_store_01");
        configuration.put(STORE_STORETYPE, "RESULT,REGULAR");
        configuration.put(RETENTION_POLICY_NAME, "SampleSipSdkIngest-xdb-library-policy"); //ensureRetentionPolicy
        configuration.put(PDI_XML, IOUtils.toString(getClass().getResourceAsStream("/content/pdi.xml"), StandardCharsets.UTF_8));
        configuration.put(PDI_SCHEMA_NAME, "urn:com.emc.ia.sip.sample.greeting:1.0");    //ensurePdiSchema
        configuration.put(PDI_SCHEMA, IOUtils.toString(getClass().getResourceAsStream("/content/pdi-schema.xsd"), StandardCharsets.UTF_8));
        configuration.put(INGEST_XML, IOUtils.toString(getClass().getResourceAsStream("/content/ingest.xml"), StandardCharsets.UTF_8));
        configuration.put(AIC_NAME, "SampleSipSdkIngest-aic");  //ensureAic
        configuration.put(CRITERIA_NAME, "Message,Content");
        configuration.put(CRITERIA_LABEL, "Message,Content");
        configuration.put(CRITERIA_TYPE, "STRING,STRING");
        configuration.put(CRITERIA_PKEYVALUESATTR, ",pkeys.values01");
        configuration.put(CRITERIA_INDEXED, "false,false");
        configuration.put(CRITERIA_PKEYMAXATTR, ",");
        configuration.put(CRITERIA_PKEYMINATTR, ",");
        configuration.put(QUOTA_NAME, "SampleSipSdkIngest-quota");  //ensureQuota
        configuration.put(QUERY_NAME, "SampleSipSdkIngest-query");  //ensureQuery
        configuration.put("ia.query.SampleSipSdkIngest-query.result.root.element", "result");
        configuration.put("ia.query.SampleSipSdkIngest-query.result.schema", "urn:com.emc.ia.sip.sample.greeting:1.0");
        configuration.put("ia.query.SampleSipSdkIngest-query.namespace.prefix", "n");
        configuration.put("ia.query.SampleSipSdkIngest-query.namespace.uri", "urn:com.emc.ia.sip.sample.greeting:1.0");
        configuration.put("ia.query.SampleSipSdkIngest-query.xdbpdi.entity.path", "/n:Greetings/n:Greeting");
        configuration.put("ia.query.SampleSipSdkIngest-query.xdbpdi.schema", "urn:com.emc.ia.sip.sample.greeting:1.0");
        configuration.put("ia.query.SampleSipSdkIngest-query.xdbpdi.template", "return $aiu");
        configuration.put("ia.query.SampleSipSdkIngest-query.xdbpdi[urn:com.emc.ia.sip.sample.greeting:1.0].operand.name", "Message,Content");
        configuration.put("ia.query.SampleSipSdkIngest-query.xdbpdi[urn:com.emc.ia.sip.sample.greeting:1.0].operand.path", "n:Message,n:Content");
        configuration.put("ia.query.SampleSipSdkIngest-query.xdbpdi[urn:com.emc.ia.sip.sample.greeting:1.0].operand.type", "STRING,STRING");
        configuration.put("ia.query.SampleSipSdkIngest-query.xdbpdi[urn:com.emc.ia.sip.sample.greeting:1.0].operand.index", "false,false");
        configuration.put(RESULT_HELPER_NAME, "SampleSipSdkIngest-result-configuration-helper");   //ensureResultConfigurationHelper
        configuration.put("ia.result.helper.ContentName-result-configuration-helper.xml", IOUtils.toString(getClass().getResourceAsStream("/content/result-configuration-helper.xml"), StandardCharsets.UTF_8));
        configuration.put("ia.result.helper.ContentName-result-configuration-helper.result.schema", "urn:com.emc.ia.sip.sample.greeting:1.0");
        configuration.put(SEARCH_NAME, "Search-By-ContentName");    //createSearch
        configuration.put("ia.search.name", "Search-By-ContentName");
        configuration.put("ia.search.Search-By-ContentName.description", "Search By Source Market");
        configuration.put("ia.search.Search-By-ContentName.nestedsearch", "false");
        configuration.put("ia.search.Search-By-ContentName.state", "DRAFT");
        configuration.put("ia.search.Search-By-ContentName.inuse", "false");
        configuration.put("ia.search.Search-By-ContentName.aic", "SampleSipSdkIngest-aic");
        configuration.put("ia.search.Search-By-ContentName.query", "SampleSipSdkIngest-query");
        configuration.put("ia.search.Search-By-ContentName.composition.name", "SampleSipSdkIngest-searchComposition");
        configuration.put("ia.search.Search-By-ContentName.composition.xform", IOUtils.toString(getClass().getResourceAsStream("/search/TextFieldFirstName/xform.resource.0fdf2de3-3c40-477a-855f-19f8df583e93"), StandardCharsets.UTF_8));
        configuration.put("ia.search.Search-By-ContentName.composition.xform.name", "SampleSipSdkIngest Search Form");
        configuration.put("ia.search.Search-By-ContentName.composition.SampleSipSdkIngest-searchComposition.result.main.name", "Message,Content");
        configuration.put("ia.search.Search-By-ContentName.composition.SampleSipSdkIngest-searchComposition.result.main.label", "Message,Content");
        configuration.put("ia.search.Search-By-ContentName.composition.SampleSipSdkIngest-searchComposition.result.main.path", "n:Message,n:Content");
        configuration.put("ia.search.Search-By-ContentName.composition.SampleSipSdkIngest-searchComposition.result.main.type", "STRING,STRING");
        configuration.put(EXPORT_TRANSFORMATION_NAME, "SampleSipSdkIngest-TransformationService");
        configuration.put(EXPORT_TRANSFORMATION_DESCRIPTION_TEMPLATE, "TestDescription");
        configuration.put(EXPORT_TRANSFORMATION_TYPE_TEMPLATE, "XSLT");
        configuration.put(EXPORT_TRANSFORMATION_MAIN_PATH_TEMPLATE, "search-results-csv.xsl");
    }
    public static void main(String[] args) throws IOException {
        IngestSample ingestSample = new IngestSample();
        ingestSample.prepareConfiguration();
        ingestSample.setup();
    }
}


