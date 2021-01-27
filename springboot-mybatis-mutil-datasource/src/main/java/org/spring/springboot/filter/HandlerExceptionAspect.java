package org.spring.springboot.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.spring.springboot.exception.BusinessException;
import org.spring.springboot.util.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class HandlerExceptionAspect {
    /**
     * 统一处理JSON响应结果的错误结果 例如：{"code":500,msg:"入参异常",data:null}
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response process(Throwable cause, HttpServletRequest request, HttpServletResponse response) {
        Response result;
        if (cause instanceof BusinessException) {
            result = new Response(((BusinessException) cause).getCode(), ((BusinessException) cause).getMsg());

        } else  if (cause instanceof AuthorizationException) {
            result = Response.FAILMSG("该操作您没有权限，请联系管理员",cause.getMessage());
        } else {
            result = Response.FAILMSG("系统异常，请联系管理员",cause.getMessage());
        }
        log.error("error:{}", cause);
        return result;
    }
}