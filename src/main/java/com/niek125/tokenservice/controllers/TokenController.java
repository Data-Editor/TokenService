package com.niek125.tokenservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.TokenGenerator.ITokenGenerator;
import com.niek125.tokenservice.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RequestMapping("/token")
@RestController
public class TokenController {
    private final RestTemplate restTemplate;
    private final ITokenGenerator generator;
    private final ObjectMapper objectMapper;

    @Autowired
    public TokenController(RestTemplate restTemplate, ITokenGenerator generator, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.generator = generator;
        this.objectMapper = objectMapper;
    }

    @RequestMapping("/token")
    public String getToken(@RequestHeader("gtoken") String gtoken) throws IOException, FirebaseAuthException {
        final FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(gtoken);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForObject("https://role-management-service/user/save", new HttpEntity<>(
                "{\"userid\":\"" + decodedToken.getUid() +
                        "\",\"profilepicture\":\"" + decodedToken.getPicture() +
                        "\",\"username\":\"" + decodedToken.getName() +
                        "\",\"email\":\"" + decodedToken.getEmail() + "\"}",
                headers), String.class);
        final Role[] permissions = restTemplate.getForObject("https://role-management-service/role/getroles/" + decodedToken.getUid(), Role[].class);
        return generator.getNewToken(decodedToken, permissions);
    }
}
