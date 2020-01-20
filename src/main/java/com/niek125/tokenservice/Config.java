package com.niek125.tokenservice;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.niek125.tokenservice.tokengenerator.ITokenGenerator;
import com.niek125.tokenservice.tokengenerator.TokenGenerator;
import com.niek125.tokenservice.kafka.KafkaProducer;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

import static com.niek125.tokenservice.utils.PemUtils.readPrivateKeyFromFile;

@Configuration
public class Config {
    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        final TrustStrategy acceptingTrustStrategy = new TrustSelfSignedStrategy();

        final SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

        final SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        final CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Value("${com.niek125.privatekey}")

    @Bean
    public Algorithm algorithm() throws IOException {
        return Algorithm.RSA512(null, (RSAPrivateKey) readPrivateKeyFromFile("PrivateKey.pem", "RSA"));
    }

    @Bean
    @Autowired
    public KafkaProducer kafkaDispatcher(KafkaTemplate<String, String> template) {
        return new KafkaProducer(template, new ObjectMapper());
    }

    @Bean
    @Autowired
    public ITokenGenerator tokenGenerator(Algorithm algorithm, ObjectMapper objectMapper) {
        return new TokenGenerator(objectMapper, algorithm);
    }
}
