package com.niek125.tokenservice.TokenGenerator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.models.Role;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

public class TokenGenerator implements ITokenGenerator{
    private final ObjectMapper objectMapper;
    private final Algorithm algorithm;

    @Autowired
    public TokenGenerator(ObjectMapper objectMapper, Algorithm algorithm) {
        this.objectMapper = objectMapper;
        this.algorithm = algorithm;
    }

    @Override
    public String getNewToken(FirebaseToken userData, Role[] permissions) throws JsonProcessingException {
        return JWT.create()
                .withIssuer("data-editor-token-service")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .withClaim("uid", userData.getUid())
                .withClaim("unm", userData.getName())
                .withClaim("pfp", userData.getPicture())
                .withClaim("pms", objectMapper.writeValueAsString(permissions))
                .sign(algorithm);
    }
}
