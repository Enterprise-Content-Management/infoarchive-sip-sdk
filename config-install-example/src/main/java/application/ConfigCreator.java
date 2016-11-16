package application;

import com.emc.ia.sdk.configuration.IAConfigurer;
import com.emc.ia.sdk.configuration.IAYamlConfigurer;
import com.emc.ia.sdk.configuration.SnakeYamlConfigurationFile;
import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.rest.NonExpiringTokenAuthentication;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.File;
import java.io.IOException;

public class ConfigCreator {
  private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjM2MDU4MjIxMzQsInVzZXJfbmFtZSI6InN1ZUBpYWN1c3R" +
                                          "vbWVyLmNvbSIsImF1dGhvcml0aWVzIjpbIkdST1VQX0FETUlOSVNUUkFUT1IiLCJHUk9VUF9" +
                                          "SRVRFTlRJT05fTUFOQUdFUiIsIkdST1VQX0JVU0lORVNTX09XTkVSIiwiR1JPVVBfRU5EX1V" +
                                          "TRVIiLCJHUk9VUF9ERVZFTE9QRVIiLCJHUk9VUF9JVF9PV05FUiIsIlJPTEVfQURNSU5JU1R" +
                                          "SQVRPUiIsIlJPTEVfUkVURU5USU9OX01BTkFHRVIiLCJST0xFX0JVU0lORVNTX09XTkVSIiw" +
                                          "iUk9MRV9FTkRfVVNFUiIsIlJPTEVfREVWRUxPUEVSIiwiUk9MRV9JVF9PV05FUiJdLCJqdGk" +
                                          "iOiJiNmNiODI0NS1iNTI1LTRjYmQtOGM3Yy1kNTI0OWNlN2RiYjgiLCJjbGllbnRfaWQiOiJ" +
                                          "pbmZvYXJjaGl2ZS5pYXdhIiwic2NvcGUiOlsiYWRtaW5pc3RyYXRpb24iLCJjb21wbGlhbmN" +
                                          "lIiwic2VhcmNoIl19.WJXBExaxsxwEtQdXTbzQRJslhHuGwqhqYtix4wsCykU";
  private static final String CONFIG_FILE = "config-install-example/src/main/resources/PhoneCalls.yml";
  private static final String SERVICES_URI = "http://localhost:8765/services";

  public static void main(String... args) throws IOException {
    RestClient client = new RestClient(new ApacheHttpClient());
    client.init(new NonExpiringTokenAuthentication(TOKEN));
    IAConfigurer configurer = new IAYamlConfigurer(client, SERVICES_URI, new SnakeYamlConfigurationFile(new File(CONFIG_FILE)));
    configurer.configure();
//    configurer.getSnaplshot();
//    configurer.install???
//    configurer.setConfgurationStrategy(ConfigurationStrategy.OVERWRITE)
//    configurer.setConfigurationStrategy(ConfigurationStrategy.READ)
//    ArchiveClient archiveClient = configurer.createArchiveClient();
  }
}
