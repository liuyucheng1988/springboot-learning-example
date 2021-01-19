package com.huawei.controller;

import com.huawei.client.OkHttpManager;
import com.huawei.conf.AuthConfigure;
import com.huawei.conf.HKConfig;
import com.huawei.domain.User;
import com.huawei.domain.vo.CallResultReq;
import com.huawei.domain.vo.CallResultRsp;
import com.huawei.ocr.base.OcrClient;
import com.huawei.service.CallResultService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Spring Boot HelloWorld 案例
 * <p>
 * Created by bysocket on 16/4/26.
 */
@RestController
@RequestMapping("/call")
@Slf4j
public class CallResultController {

    @Autowired
    private CallResultService callResultService;


    @PostMapping("/monitor")
    public List<CallResultRsp> queryMonitor(@RequestBody CallResultReq req)   {


        log.info("CallResultReq," + req);
        return callResultService.findByCondition(req);
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
