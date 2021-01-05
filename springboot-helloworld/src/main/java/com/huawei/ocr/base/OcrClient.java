package com.huawei.ocr.base;

import com.baidu.aip.ocr.AipOcr;

public class OcrClient {
    private static final String APP_ID = "23488332";
    private static final String API_KEY = "nKwlN7rGWdO4GIhqQyF2VvfB";
    private static final String SECRET_KEY = "2fiSY4fUpNOgxSptn5Fgn2GLgDj6TSnL";
    private static AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
    static {
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "log4j.properties");
    }

    private OcrClient() {
    }

    public static AipOcr getAipOcr() {
        return client;
    }
}
