package com.speedy.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientResponse {
  private String responseBody;
  private int statusCode;
}
