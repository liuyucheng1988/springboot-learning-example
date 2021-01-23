package org.spring.springboot;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.spring.springboot.util.MD5Utils;

import javax.crypto.KeyGenerator;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Main {
    private static Algorithm algorithm;

    static {
        try {
//            algorithm = Algorithm.HMAC512(KeyGenerator.getInstance("HmacSHA512").generateKey().getEncoded());
            algorithm = Algorithm.HMAC512("123456");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String create(String username) {
        Date now = new Date();
        return JWT.create()
                .withIssuedAt(now)
                .withClaim("username", username)
                .withExpiresAt(new Date(now.getTime() + 60000*60))
                .sign(algorithm);
    }

    public static String verify(String token) {
        return JWT.require(algorithm).build()
                .verify(token)
                .getClaim("username")
                .asString();
    }


    public static void main(String[] args) {
        String target = MD5Utils.md5Encryption("123456", "a1b2c3d4e5f");
        System.out.println(target);
//        String token = create("admin");
//        System.out.println(token);
//        System.out.println(verify(token));
//        System.out.println(verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2MTE0MjAyMjYsImlhdCI6MTYxMTQxNjYyNiwidXNlcm5hbWUiOiJhZG1pbiJ9.soByDNNszMjlOdsYFSXENKt5QD1S9CgFDI0t-4KNJu7T3CJA4QM0fLjxaXRf8FqBbMWxi7BAW1n3-Ven8dwUOA1"));
//        System.out.println(verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1NzM4MTU2MzYsImlhdCI6MTU3MzgxMjAzNiwidXNlcm5hbWUiOiIxMjMifQ.4xIgdDx44A_4vdebICw-FmzxMqw5R48OODJ7HPst4rEe-4k2ob1cKat2CKqw7VSTsKAEBl1abxv6EpBU5AhIwg"));
    }
}
