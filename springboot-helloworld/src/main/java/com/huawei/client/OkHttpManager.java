package com.huawei.client;

import com.alibaba.fastjson.JSON;
import com.huawei.domain.User;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OkHttpManager  {
    public void post(String url, String name, int age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
//        Request request = new Request.Builder().url(url).post(formBody).build();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String sr1 = JSON.toJSONString(user);
        RequestBody requestBody = FormBody.create(mediaType, sr1);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

//        RequestBody formBody = new FormBody.Builder()
//                .add("name", name)
//                .add("age", age)
//                .build();


        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                log.error("onFailure,",e);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String str = response.body().string();
                log.info("Response:"+ str);
//                handler.sendEmptyMessage(10);

            }
        });

    }

    public void asynchronousGet(String url) throws Exception {
//        Request request = new Request.Builder()
//                .url("http://publicobject.com/helloworld.txt")
//                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        client.newCall(request).enqueue(new Callback() {//异步请求
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    log.info("【" + responseHeaders.name(i) + "】" + responseHeaders.value(i));
                }

                log.info("【响应结果】" + response.body().string());
            }
        });
    }
   /* public void get(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(activity, "Get 失败");
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(activity, responseStr);
                    }
                });
            }
        });
    }*/
}