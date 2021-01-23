package org.spring.springboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Slf4j
public class JWTUtils {
    /**
     * 过期时间12小时
     */
//    private static final long EXPIRE_TIME = 12*60*60*1000;
    private static final long EXPIRE_TIME = 10*60 * 1000;
  /*  public static String sign(String username, String secret) throws Exception {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create().withHeader(map)// header
                .withClaim("username", username)// payload
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(secret));// 加密
        return token;
    }
  */
    /**
     * 生成签名
     *
     * @param username 用户名
     * @param secret   用户的密码
     * @return 加密的token
     */
    public static String sign(String username, String secret) throws UnsupportedEncodingException {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带username信息
        return JWT.create()
                .withClaim("username", username)
                .withExpiresAt(date)
                .sign(algorithm);
    }
    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static void verify(String token, String username, String secret) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withClaim("username", username)
                .build();
       verifier.verify(token);
    }
    /*public static boolean verify(String token, String username, String secret) throws Exception {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT jwt = verifier.verify(token);
        Map<String, Claim> claims = jwt.getClaims();
        String usernameDecode = claims.get("username").asString();
        return true;
    }
*/
    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }


    /**
     * 判断过期
     *
     * @param token
     * @return
     */
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return System.currentTimeMillis() > jwt.getExpiresAt().getTime();
    }
}

