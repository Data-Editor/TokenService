package com.niek125.tokenservice.tokenservice.TokenGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

public class TokenGenerator implements ITokenGenerator{
    private String alg;
    private String key;

    public TokenGenerator(String alg, String key){
        this.alg = alg;
        this.key = key;
    }

    private String makeHeader(){
        JSONObject header = new JSONObject();
        header.put("alg", alg);
        header.put("typ", "JWT");
        return Base64.getUrlEncoder().encodeToString(header.toString().getBytes());
    }

    private String makePay(){
        UUID uuid = UUID.randomUUID();
        JSONObject pay = new JSONObject();
        pay.put("iss", Pattern.quote("http://localhost:8080"));
        pay.put("jti", uuid.toString());
        pay.put("iat", System.currentTimeMillis());
        pay.put("exp", System.currentTimeMillis() + (1000 * 60 * 60));
        pay.put("uid", "auserid");
        pay.put("unm", "ausername");
        pay.put("pfp", "apfp");
        JSONArray permissions = new JSONArray();
        for (int i = 0; i < 2; i++) {
            JSONObject perm = new JSONObject();
            perm.put("pid", "aprojectid" + i);
            perm.put("rln", "guest");
            permissions.put(perm);
        }
        pay.put("pms", permissions);
        return Base64.getUrlEncoder().encodeToString(pay.toString().getBytes());
    }

    private String makeSig(String header, String pay) {
        try {
            MessageDigest digest = MessageDigest.getInstance(alg);
            digest.update(key.getBytes());
            return Base64.getUrlEncoder().encodeToString(digest.digest((header + pay).getBytes()));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Choose a valid algorithm for signatures");
        }
        return new String();
    }

    @Override
    public String getNewToken() {
        String header = makeHeader();
        String pay = makePay();
        String sig = makeSig(header, pay);
        return  header + Pattern.quote(".") + pay + Pattern.quote(".") + sig;
    }
}
