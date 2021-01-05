package com.huawei.ocr;

import com.baidu.aip.ocr.AipOcr;
import com.huawei.ocr.base.OcrClient;
import org.json.JSONObject;

import java.util.HashMap;

public class Sample {
    //设置APPID/AK/SK
    public static final String APP_ID = "23488332";
    public static final String API_KEY = "nKwlN7rGWdO4GIhqQyF2VvfB";
    public static final String SECRET_KEY = "2fiSY4fUpNOgxSptn5Fgn2GLgDj6TSnL";

    public static void main(String[] args) {
        // 初始化一个AipOcr
//        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
//        client.setConnectionTimeoutInMillis(2000);
//        client.setSocketTimeoutInMillis(60000);
        AipOcr client = OcrClient.getAipOcr();
        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "log4j.properties");

        // 调用接口
//        String path = "test.jpg";
        String path="C:\\Users\\liuyucheng02\\Downloads\\b1.JPG";
        JSONObject res = client.basicGeneral(path, new HashMap<>());
        System.out.println(res.toString(2));

    }
}
