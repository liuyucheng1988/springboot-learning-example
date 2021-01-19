package org.spring.springboot.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Response<T> {
    private Integer code;
    private String msg;
    private T data;
    public static Response SUCCESS = new Response(200, "success", null);
    public static Response FAIL = new Response(500, "fail", null);
    public static <T> Response SUCCESSDATA(T data) {
        return new Response(200, "success", data);
    }
    public static <T> Response FAILMSG(String msg) {
        return new Response(500, msg, null);
    }
}
