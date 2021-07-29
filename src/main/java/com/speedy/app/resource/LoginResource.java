package com.speedy.app.resource;

import java.io.IOException;

import com.speedy.app.model.ClientResponse;
import com.speedy.app.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginResource {

  @Autowired
  ClientService clientService;

  @Value("${base-url}")
  String BASE_URL;

  @PostMapping
  public ClientResponse login() throws IOException {
    return clientService.executeRequest(BASE_URL + "/merchant/user/login");
  }
}
