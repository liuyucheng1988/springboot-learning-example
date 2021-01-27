package org.spring.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

//@Configuration
//@EnableSwagger2
public class Swagger2Config {
    //配置swagger的实例
    @Bean
    public Docket docket(Environment environment){
        return new Docket(DocumentationType.SWAGGER_2)
                //这个可以自定义页面出现的标题，描述等信息
                .apiInfo(this.apiInfo())
                //是否开启swagger2，可以用作环境判断使用
                .enable(false)
                //筛选
                .select()
                //扫描的包
                .apis(RequestHandlerSelectors.basePackage("org.spring.springboot"))
                //构建项目
                .build();
    }


    public ApiInfo apiInfo(){
        Contact contact = new Contact("liuyucheng02","url","liuyucheng02@baidu.com");
        return new ApiInfo("标题",
                "描述",
                "v1.0",
                "urn:tos",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }
}