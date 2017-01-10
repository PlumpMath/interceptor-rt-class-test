package com.a.eye.skywalking.test.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 执行类
 */
public class InvokerClass {
    private static Class clazz;
    private static Method method;

    /**
     *
     *
     * @param classLoader
     */
    public static void addClassloader(ClassLoader classLoader) {
        try {
            clazz = classLoader.loadClass("com.a.eye.skywalking.test.ToBeInvokerClass");
            method = clazz.getDeclaredMethod("invoke");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     */
    public static void doCallInvokerMethod(){
        try {
            method.invoke(clazz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
