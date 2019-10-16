package com.niek125.tokenservice.tokenservice;

import com.niek125.tokenservice.tokenservice.TokenGenerator.ITokenGenerator;
import com.niek125.tokenservice.tokenservice.TokenGenerator.TokenGenerator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("Token")
@RestController
public class TokenController {
    private ITokenGenerator generator;

    TokenController(){
        generator = new TokenGenerator("SHA-512", "testkeyisnowlonger");
    }

    @RequestMapping("GetToken")
    public String getToken(){
        return generator.getNewToken();
    }
}
