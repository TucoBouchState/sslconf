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
@TestProfile(CaseCacertsLoadedInRestClientTest.TestConf.class)
class CaseCacertsLoadedInRestClientTest {

    @RestClient
    private MyClient myClient;

    @Test
    @DisplayName("when using the cacerts loaded via the configuration of the rest client")
    void restWebClientUsingCacertsLoadedViaRestClientConfig() {

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
                    "quarkus.rest-client.my-client.trust-store", "./cacerts_bad",
                    "quarkus.rest-client.my-client.trust-store-password", "changeit"
            );
        }
    }
}
