package com.niek125.tokenservice.controllers;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.TokenGenerator.ITokenGenerator;
import com.niek125.tokenservice.TokenGenerator.TokenGenerator;
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
import java.security.interfaces.RSAPrivateKey;

import static com.niek125.tokenservice.TokenGenerator.PemUtils.readPrivateKeyFromFile;

@RequestMapping("/token")
@RestController
public class TokenController {
    @Autowired
    private RestTemplate restTemplate;
    private ITokenGenerator generator;
    private ObjectMapper objectMapper;

    public TokenController() throws IOException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        this.objectMapper = new ObjectMapper();
        this.generator = new TokenGenerator(
                Algorithm.RSA512(
                        null,
                        (RSAPrivateKey) readPrivateKeyFromFile("src/main/resources/PrivateKey.pem", "RSA")));
    }

    @RequestMapping("/token")
    public String getToken(@RequestHeader("gtoken") String gtoken) throws IOException, FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(gtoken);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForObject("http://role-management-service/user/save", new HttpEntity<String>(
                            "{\"userid\":\"" + decodedToken.getUid() +
                        "\",\"profilepicture\":\"" + decodedToken.getPicture() +
                              "\",\"username\":\"" + decodedToken.getName() +
                                 "\",\"email\":\"" + decodedToken.getEmail() + "\"}",
                headers), String.class);
        Role[] permissions = restTemplate.getForObject("http://role-management-service/role/getroles/" + decodedToken.getUid(), Role[].class);
        return generator.getNewToken(decodedToken, permissions);
    }
}
