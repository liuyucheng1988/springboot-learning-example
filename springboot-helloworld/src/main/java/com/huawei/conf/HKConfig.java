package com.huawei.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
//@PropertySource(value = "config/application.yaml")
public class HKConfig {
    @Value("${digitalhuman.name:#{null}}")
    private String name;
    @Value("${digitalhuman.age:1000}")
    private String age;
}