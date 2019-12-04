package com.niek125.tokenservice.controllers;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niek125.tokenservice.TokenGenerator.ITokenGenerator;
import com.niek125.tokenservice.TokenGenerator.TokenGenerator;
import com.niek125.tokenservice.models.Role;
import com.niek125.tokenservice.models.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.niek125.tokenservice.TokenGenerator.PemUtils.readPrivateKeyFromFile;
import static com.niek125.tokenservice.TokenGenerator.PemUtils.readPublicKeyFromFile;

@RequestMapping("/token")
@RestController
public class TokenController {
    @Autowired
    private RestTemplate restTemplate;
    private ITokenGenerator generator;
    private ObjectMapper objectMapper;

    public TokenController() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        this.objectMapper = new ObjectMapper();
        this.generator = new TokenGenerator(
                Algorithm.RSA512(
                        null,
                        (RSAPrivateKey) readPrivateKeyFromFile("src/main/resources/PrivateKey.pem", "RSA")));
    }

    @RequestMapping("/token/{gtoken}")
    public String getToken(@PathVariable("gtoken") String gtoken) throws IOException {
        UserData userData = objectMapper.readValue(new String(Base64.getUrlDecoder().decode(gtoken)), UserData.class);
        Role[] permissions = restTemplate.getForObject("http://role-management-service/role/getroles/" + userData.getUid(), Role[].class);
        return generator.getNewToken(userData, permissions);
    }
}
