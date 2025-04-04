Related to bug opened https://github.com/quarkusio/quarkus/issues/47032

## Summary

From Quarkus 3.19, the tls registry `javax.net.ssl` (
see https://quarkus.io/guides/tls-registry-reference#referencing-the-default-truststore-of-sunjsse) is crashing the
quarkus application if the cacerts contains a
certificate without a CN or OU.  
The following exception is raised :

```
...
Caused by: java.lang.IllegalStateException: No CN or OU in O=Govxxxxx Authority,C=xxx
	at io.quarkus.tls.runtime.JavaxNetSslTrustStoreProvider$JavaNetSslTrustOptions.lambda$new$4(JavaxNetSslTrustStoreProvider.java:74)
	at java.base/java.util.Optional.orElseThrow(Optional.java:403)
	at io.quarkus.tls.runtime.JavaxNetSslTrustStoreProvider$JavaNetSslTrustOptions.lambda$new$5(JavaxNetSslTrustStoreProvider.java:74)
	at java.base/java.util.Optional.orElseGet(Optional.java:364)
	at io.quarkus.tls.runtime.JavaxNetSslTrustStoreProvider$JavaNetSslTrustOptions.<init>(JavaxNetSslTrustStoreProvider.java:70)
	at io.quarkus.tls.runtime.JavaxNetSslTrustStoreProvider.getTrustStore(JavaxNetSslTrustStoreProvider.java:45)
	at io.quarkus.tls.runtime.CertificateRecorder.lambda$get$0(CertificateRecorder.java:181)
	at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1708)
	at io.quarkus.tls.runtime.CertificateRecorder.get(CertificateRecorder.java:180)
	at io.quarkiverse.cxf.CXFClientInfo.tlsConfiguration(CXFClientInfo.java:344)
	at io.quarkiverse.cxf.CXFClientInfo.<init>(CXFClientInfo.java:269)
	at io.quarkiverse.cxf.CxfClientProducer.selectorCXFClientInfo(CxfClientProducer.java:298)
	at io.quarkiverse.cxf.CxfClientProducer.selectorCXFClientInfo(CxfClientProducer.java:[274
...
```

## The code

The code here demonstrates the cacerts loading in quarkus 3.19+.  
The cacerts `cacerts_bad` is a trust store containing a certificate without a CN or OU.  
Depending how the cacerts is loaded in quarkus, the application can crash at startup.

There are 3 unit tests :

* ✅ CaseCacertsLoadedInRestClientTest  
  Creates a rest web client and load the cacerts using the `quarkus.rest.*.trust-store` property

```properties
quarkus.rest-client.my-client.url=https://code.quarkus.io/
quarkus.rest-client.my-client.trust-store=./cacerts_bad
quarkus.rest-client.my-client.trust-store-password=changeit
```

The cacerts loading is working although the cacerts contains a certificate without a CN or OU

* ✅ CaseCustomTlsRegistryTest  
  Creates a tls configuration loading the cacerts and creates a rest web client referencing this tls configuration

```properties
quarkus.rest-client.my-client.url=https://code.quarkus.io/
quarkus.rest-client.my-client.tls-configuration-name=mytls
quarkus.tls.mytls.trust-store.p12.path=./cacerts_bad
quarkus.tls.mytls.trust-store.p12.password=changeit
```

The cacerts loading is working although the cacerts contains a certificate without a CN or OU

* ❗ CaseJavaxNetSslTlsRegistryTest  
  Creates a rest web client referencing the tls configuration `javax.net.ssl` created by default by quarkus 3.19+

```properties
quarkus.rest-client.my-client.url=https://code.quarkus.io/
quarkus.rest-client.my-client.tls-configuration-name=javax.net.ssl
```

```java
    static {
    System.setProperty("javax.net.ssl.trustStore", "./cacerts_bad");
    System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
}
```