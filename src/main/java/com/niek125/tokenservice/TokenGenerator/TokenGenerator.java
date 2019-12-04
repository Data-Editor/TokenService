package com.niek125.tokenservice.TokenGenerator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niek125.tokenservice.models.Role;
import com.niek125.tokenservice.models.UserData;

import java.util.Date;
import java.util.UUID;

public class TokenGenerator implements ITokenGenerator{
    private ObjectMapper objectMapper;
    private Algorithm algorithm;

    public TokenGenerator(Algorithm algorithm){
        this.algorithm = algorithm;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String getNewToken(UserData userData, Role[] permissions) throws JsonProcessingException {
        String permissionsstr = objectMapper.writeValueAsString(permissions);
        return JWT.create()
                .withIssuer("data-editor-token-service")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .withClaim("uid", userData.getUid())
                .withClaim("unm", userData.getUsername())
                .withClaim("pfp", userData.getProfilePicture())
                .withClaim("pms", permissionsstr)
                .sign(algorithm);
    }
}
