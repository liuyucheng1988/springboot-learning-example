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
    private String msg;//前端展示
    private String debugMsg;
    private T data;

    public Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Response SUCCESS = new Response(200, "success");
    public static Response FAIL = new Response(500, "fail");

    public static <T> Response SUCCESSDATA(T data) {
        return new Response(200, "success", data);
    }
    public static <T> Response FAILMSG(String msg, String debugMsg) {
        return new Response(500, msg, debugMsg,null);
    }
    public static <T> Response FAILMSG(String msg) {
        return new Response(500, msg,null);
    }
}
