package com.niek125.tokenservice.tokengenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.models.Role;

public interface ITokenGenerator {
    String getNewToken(FirebaseToken userData, Role[] permissions) throws JsonProcessingException;
}
