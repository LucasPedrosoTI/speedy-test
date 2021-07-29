package com.speedy.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speedy.app.model.LoginRequest;
import com.speedy.app.model.LoginResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpeedyTestApplicationTests {

	private static final String KEYSTOREPATH = "/truststore.jks";
	private static final String KEYSTOREPASS = "secret";
	// private static final String BASE_URL =
	// "https://sandbox-reporting.rpdpymnt.com/api/v3";
	private static final String BASE_URL = "https://reporting.rpdpymnt.com/api/v3";
	private CloseableHttpClient client;
	private ObjectMapper mapper = new ObjectMapper();
	private HttpPost post = new HttpPost();
	private static String token = "";

	@BeforeEach
	void setup() throws Exception {
		post.setHeader("Content-type", "application/json");

		SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(readStore(), KEYSTOREPASS.toCharArray())
				.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

		client = HttpClients.custom().setSSLContext(sslContext).build();
	}

	@Test
	void testLoginAPI() throws Exception {
		var loginBody = new LoginRequest("demo@financialhouse.io", "cjaiU8CV");
		post = new HttpPost(BASE_URL + "/merchant/user/login");
		post.setEntity(new StringEntity(mapper.writeValueAsString(loginBody), ContentType.APPLICATION_JSON));

		var response = client.execute(post);
		var result = mapper.readValue(response.getEntity().getContent(), LoginResponse.class);
		token = Optional.of(result.getToken()).orElse("");
		System.out.println(token);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertNotNull(token);
		assertEquals("APPROVED", result.getStatus());

	}

	@Test
	void testTransactionReport() throws Exception {
		var post = new HttpPost(BASE_URL + "/transactions/report");
		post.setHeader("Authorization", token);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("fromDate", "2015-01-01"));
		params.add(new BasicNameValuePair("toDate", "2021-01-01"));

		post.setEntity(new UrlEncodedFormEntity(params));

		var response = client.execute(post);

		var result = mapper.readTree(response.getEntity().getContent());
		System.out.println(result.toPrettyString());
	}

	@Test
	void testTransactionQuery() throws Exception {
		var post = new HttpPost(BASE_URL + "/transaction/list");
		post.setHeader("Authorization", token);

		var response = client.execute(post);

		var result = mapper.readTree(response.getEntity().getContent());
		System.out.println(result.toPrettyString());
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	KeyStore readStore() throws Exception {
		try (InputStream keyStoreStream = this.getClass().getResourceAsStream(KEYSTOREPATH)) {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(keyStoreStream, KEYSTOREPASS.toCharArray());
			return keyStore;
		}
	}

}
