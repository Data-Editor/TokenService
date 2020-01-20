package com.niek125.tokenservice.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.events.UserLoggedInEvent;
import com.niek125.tokenservice.kafka.KafkaProducer;
import com.niek125.tokenservice.models.Role;
import com.niek125.tokenservice.models.User;
import com.niek125.tokenservice.tokengenerator.ITokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RequestMapping("/token")
@CrossOrigin
@RestController
public class TokenController {
    private final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private final RestTemplate restTemplate;
    private final ITokenGenerator generator;
    private final FirebaseAuth firebaseAuth;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public TokenController(RestTemplate restTemplate, ITokenGenerator generator, FirebaseAuth firebaseAuth, KafkaProducer kafkaProducer) {
        this.restTemplate = restTemplate;
        this.generator = generator;
        this.firebaseAuth = firebaseAuth;
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/token")
    public String getToken(@RequestHeader("gtoken") String gtoken) throws IOException, FirebaseAuthException {
        logger.info("getting firebase token");
        final FirebaseToken decodedToken = firebaseAuth.verifyIdToken(gtoken);
        final User user = new User(decodedToken.getUid(), decodedToken.getPicture(),decodedToken.getName(), decodedToken.getEmail());
        logger.info("sending UserLoggedInEvent");
        kafkaProducer.dispatch("user", new UserLoggedInEvent(user));
        logger.info("getting roles");
        final Role[] permissions = restTemplate.getForObject("https://role-management-service/role/getroles/" + decodedToken.getUid(), Role[].class);
        logger.info("generating token");
        final String token = generator.getNewToken(decodedToken, permissions);
        logger.info("returning token");
        return token;
    }
}
