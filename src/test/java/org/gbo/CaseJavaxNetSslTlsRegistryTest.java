package org.gbo;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusTest
@TestProfile(CaseJavaxNetSslTlsRegistryTest.TestConf.class)
class CaseJavaxNetSslTlsRegistryTest {

    static {
//        System.setProperty("javax.net.ssl.trustStore", "./cacerts_bad");
        System.setProperty("javax.net.ssl.trustStore", "./cacerts_bad");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

    @RestClient
    private MyClient myClient;

    @Test
    @DisplayName("when using the javax.net.ssl tls registry loading the JVM cacerts")
    void restClientUsingCacertsLoadedViaJavaxNetSslTlsConfiguration() {

        //This test is failing because the check on the certificate : one of the certificate in the cacerts doesn't have a CN or OU

        //It should not,

        var get = myClient.get();

        System.out.printf("got : %s\n", get);

        Assertions.assertNotNull(get);
        Assertions.assertFalse(get.isEmpty());
    }

    @RegisterRestClient(configKey = "my-client")
    @Path("/")
    public interface MyClient {

        @GET
        String get();
    }

    public static class TestConf implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.rest-client.my-client.url", "https://code.quarkus.io/",
                    "quarkus.rest-client.my-client.tls-configuration-name", "javax.net.ssl"
            );
        }
    }
}
