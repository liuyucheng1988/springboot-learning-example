package com.huawei.web;

import com.huawei.client.OkHttpManager;
import com.huawei.conf.AuthConfigure;
import com.huawei.conf.HKConfig;
import com.huawei.domain.User;
import com.huawei.ocr.base.OcrClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Spring Boot HelloWorld 案例
 * <p>
 * Created by bysocket on 16/4/26.
 */
@RestController
@RequestMapping("/server")
@Slf4j
//@PropertySource("classpath:config/mail.properties")
//@Configuration
public class ServerApiController {

    @Autowired
    private AuthConfigure authConfigure;
    @Value("${digitalhuman.name:#{null}}")
//    @Value("${name}")
    private String cfgName;
    @Value("${digitalhuman.age:10000}")
    private String cfgAge;
    @Autowired
    private HKConfig hKConfig;
    @Autowired
    private OkHttpManager okHttpManager;


    @GetMapping("/{age}")
    public String sayHello(@RequestParam(defaultValue = "world ", name = "name", required = true) String name,
                           @PathVariable String age) throws Exception {


        String str = "Hello," + name + "!my age=" + age + ",config=" + authConfigure.spdbAuthConfig().getBaseUrl() + ",cf.name=" + cfgName + ",cf.age=" + cfgAge + ".hg=" + hKConfig.toString();
        log.info("get," + str);
//        String path="C:\\Users\\liuyucheng02\\Downloads\\b1.JPG";
        String path = "/home/springboot/config/pic/" + age + ".JPG";

        JSONObject res = OcrClient.getAipOcr().basicGeneral(path, new HashMap<>());
        String response = res.toString(2);
        log.info(response);
        okHttpManager.asynchronousGet("http://106.12.46.238:8888/server/12?name=liuyc");
        okHttpManager.post("http://106.12.46.238:8888/server/adduser", "liubaolin", 68);
        return str + "\n" + response;
    }

    @PostMapping("/adduser")
    public String createUser(@RequestBody User user) {
        log.info("post adduser req=" + user.toString());
        return "HTTP POST was called：" + user.toString();
    }

    @DeleteMapping("/{name}")
    public String delUser(@PathVariable(name = "name") String nam1) {
        log.info("delete =" + nam1);
        return "delete:" + nam1;
    }

    @PutMapping("/{name}")
    public String updateUser(@PathVariable String name, @RequestBody User user) {
        log.info("updateUser name=" + name + ", req=" + user.toString());
        return "updateUser name=" + name + ", req=" + user.toString();
    }
}
