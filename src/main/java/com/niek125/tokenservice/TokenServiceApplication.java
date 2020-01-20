package com.niek125.tokenservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
@EnableEurekaClient
public class TokenServiceApplication {

    public static void main(String[] args) throws IOException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        FileInputStream serviceAccount =
                new FileInputStream("D:\\Semester3\\Software\\BigIdea\\Project\\backend\\TokenService\\src\\main\\resources\\dataeditor-firebase-adminsdk-fhb0o-c95d73760a.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://dataeditor.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        SpringApplication.run(TokenServiceApplication.class, args);
    }

}
