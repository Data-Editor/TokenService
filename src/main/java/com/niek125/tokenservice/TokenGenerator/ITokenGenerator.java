package com.niek125.tokenservice.TokenGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.niek125.tokenservice.models.Role;
import com.niek125.tokenservice.models.UserData;

public interface ITokenGenerator {
    String getNewToken(UserData userData, Role[] permissions) throws JsonProcessingException;
}
