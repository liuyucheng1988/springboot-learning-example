package org.spring.springboot.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2Config {
   /* @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.spring.springboot"))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .build())
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }*/
   @Bean
   public Docket createRestApi() {
       return new Docket(DocumentationType.SWAGGER_2)
               .pathMapping("/")
               .select()
               .apis(RequestHandlerSelectors.basePackage("org.spring.springboot"))
               .paths(PathSelectors.any())
               .build().apiInfo(new ApiInfoBuilder()
                       .build());
   }

   /* private List<ApiKey> securitySchemes() {
        ArrayList<ApiKey> apiKeyList = Lists.newArrayList();
        apiKeyList.add(new ApiKey("JSON WEB TOKEN(秘钥)", "Authorization", "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }*/

    //配置swagger的实例
   /* @Bean
    public Docket docket(Environment environment){
        return new Docket(DocumentationType.SWAGGER_2)
                //这个可以自定义页面出现的标题，描述等信息
                .apiInfo(this.apiInfo())
                //是否开启swagger2，可以用作环境判断使用
                .enable(true)
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
    }*/
}