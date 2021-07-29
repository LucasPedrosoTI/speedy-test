package com.speedy.app.config;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class ClientConfig {

  @Value("${client.ssl.key-store}")
  String KEYSTOREPATH;

  @Value("${client.ssl.key-store-password}")
  String KEYSTOREPASS;

  @Bean
  @Scope("prototype")
  public CloseableHttpClient apacheHttpClient() throws Exception {

    SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(readStore(), KEYSTOREPASS.toCharArray())
        .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

    return HttpClients.custom().setSSLContext(sslContext).build();

  }

  KeyStore readStore() throws Exception {
    try (InputStream keyStoreStream = this.getClass().getResourceAsStream(KEYSTOREPATH)) {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(keyStoreStream, KEYSTOREPASS.toCharArray());
      return keyStore;
    }
  }
}
