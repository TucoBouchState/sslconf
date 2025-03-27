package org.gbo;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TrustBug {


    static {
        System.setProperty("javax.net.ssl.trustStore", "C:\\dev\\Project\\reproduce-trust-\\bad.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

    @Inject
    private Vertx vertx;

    @RestClient
    private MyClient myClient;

    @Test
    void test() {
        //   JavaxNetSslTrustStoreProvider.getTrustStore(vertx);
    }

    @Test
    void test3() {
        System.out.println(myClient.get());
    }

    @Test
    void test2() throws Exception {
        //   JavaxNetSslTrustStoreProvider.getTrustStore(vertx);
        WebClient client = WebClient.create(vertx);
        var f = client.get(443, "code.quarkus.io", "/")
                .ssl(true)
                .send()
                .onSuccess(response -> System.out
                        .println("Received response with status code " + response.statusCode()))
                .onFailure(err -> err.printStackTrace());

        while (!f.isComplete()) {
            Thread.sleep(100);
        }

        System.out.println(f.result().bodyAsString());
        //System.out.println("Something went wrong " + err.getMessage()));
    }

    @RegisterRestClient(configKey = "my-client")
    @Path("/")
    public interface MyClient {

        @GET
        String get();
    }

}
