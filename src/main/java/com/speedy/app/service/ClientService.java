package com.speedy.app.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speedy.app.model.ClientResponse;
import com.speedy.app.model.LoginRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
  private final CloseableHttpClient httpClient;

  private final ObjectMapper mapper = new ObjectMapper();

  public ClientService(CloseableHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public ClientResponse executeRequest(String url) throws IOException {

    var request = new HttpPost(url);
    var loginBody = new LoginRequest("demo@financialhouse.io", "cjaiU8CV");
    request.setEntity(new StringEntity(mapper.writeValueAsString(loginBody), ContentType.APPLICATION_JSON));

    HttpResponse response = httpClient.execute(request);

    var responseBody = mapper.readTree(response.getEntity().getContent()).toPrettyString();
    int statusCode = response.getStatusLine().getStatusCode();
    return new ClientResponse(responseBody, statusCode);
  }
}
