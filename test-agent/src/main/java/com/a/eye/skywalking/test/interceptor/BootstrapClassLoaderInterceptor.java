package com.a.eye.skywalking.test.interceptor;

import com.a.eye.skywalking.test.invoker.InvokerClass;

import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.util.concurrent.Callable;

/**
 * Bootstrap classloader加载的类拦截器
 */
public class BootstrapClassLoaderInterceptor {

    public static String interceptor(@SuperCall Callable<String> zuper, @This Object o) throws
            Exception {
        System.out.println("AAA");
        InvokerClass.doCallInvokerMethod();
        return zuper.call();
    }
}
