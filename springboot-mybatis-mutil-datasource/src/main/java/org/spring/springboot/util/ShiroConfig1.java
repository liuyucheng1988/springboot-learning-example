package org.spring.springboot.util;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

//@Configuration
public class ShiroConfig1 {
 /*   @Bean
    public DefaultWebSecurityManager getSecurityManager(UserRealm realm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager){
        //1、创建过滤器工厂
        ShiroFilterFactoryBean filterFactory = new ShiroFilterFactoryBean();
        //2、设置安全管理器
        filterFactory.setSecurityManager(securityManager);
        //3、通用配置（跳转登录页面，为授权跳转的页面）
        filterFactory.setLoginUrl("/autherror?code=1");//授权成功，跳转的url地址
        filterFactory.setUnauthorizedUrl("/autherror?code=2");//未授权的跳转页面
        //4、设置过滤器集合
        *//**
         * 设置所有过滤器，使用有顺序的map
         *  key->拦截url地址
         *  value-> 过滤器类型
         *//*
        Map<String,String> filterMap = new LinkedHashMap<>();
        filterMap.put("/api/login","anon");//无需认证即可访问
        filterMap.put("/api/**","authc");//当前请求地址必须认证后访问
        filterFactory.setFilterChainDefinitionMap(filterMap);
        return filterFactory;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }*/

}
