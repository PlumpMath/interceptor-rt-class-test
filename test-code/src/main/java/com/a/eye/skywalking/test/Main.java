package com.a.eye.skywalking.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 测试工程的入口
 *
 * 该工程主要拦截由bootstrap classloader 加载的类。并且在拦截的过程中
 * 调用由App classloader加载上来的类的方法。
 *
 * 使用方式：
 * 在运行过程中加入-javaagent:test-agent.jar=test-code.jar
 * <br/>
 * 执行结果：
 * It works.
 * GET
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ToBeInterceptorClass toBeInterceptorClass = new ToBeInterceptorClass();
        toBeInterceptorClass.call();
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://www.google.com")
                .openConnection();
        System.out.println(urlConnection.getRequestMethod());
    }
}
