package com.a.eye.skywalking.test.interceptor;


import com.a.eye.skywalking.test.invoker.InvokerClass;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 */
public class InvokerClassInterceptor {
    private static Map<String, ClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    public DynamicType.Builder<?> prepareToInterceptor(String className, ClassLoader classLoader, DynamicType.Builder<?> builder) {
        classLoaderMap.put(className, classLoader);
        return builder.method(named("call")).intercept(MethodDelegation.to(InvokerClassInterceptor
                .class));
    }

    public static String intercept(@SuperCall Callable<String> zuper, @This Object o) throws
            Exception {
        InvokerClass.addClassloader(classLoaderMap.get(o.getClass().getName()));
        return zuper.call();
    }
}
