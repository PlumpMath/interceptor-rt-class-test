package com.a.eye.skywalking.test;

import com.a.eye.skywalking.test.interceptor.BootstrapClassLoaderInterceptor;
import com.a.eye.skywalking.test.interceptor.InvokerClassInterceptor;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.Collections;
import java.util.jar.JarFile;


public class AgentMain {

    /**
     * <code>-javaagent:xxx.jar=addClassloader jar path</code>
     *
     * 该方法主要拦截两个类，第一个类是由Bootstrap classloader加载的类{@link java.net.HttpURLConnection},
     * 第二个类则是由App classloader加载的类{@link com.a.eye.skywalking.test.ToBeInterceptorClass}.
     *
     * 执行流程：
     * 1. 执行{@link com.a.eye.skywalking.test.ToBeInterceptorClass}的call.
     * 2. 被{@link com.a.eye.skywalking.test.interceptor.InvokerClassInterceptor
     * }拦截，获取ToBeInterceptorClass的Classloader
     * 3. 执行{@link java.net.HttpURLConnection}的getRequestMethod的方法
     * {@link com.a.eye.skywalking.test.invoker.InvokerClass}，执行BootstrapClassLoaderInterceptor
     * 的interceptor方法，执行InvokerClass的doCallInvokerMethod，就调用到了App classloader加载上来的类的方法
     *
     * @param argumentString addClassloader jar包的位置
     */
    public static void premain(String argumentString, Instrumentation instrumentation) throws IOException {
        if (argumentString != null)
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(new File(argumentString)));

        File temp = Files.createTempDirectory("tmp").toFile();
        ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation).inject(Collections.singletonMap(
                new TypeDescription.ForLoadedType(BootstrapClassLoaderInterceptor.class),
                ClassFileLocator.ForClassLoader.read(BootstrapClassLoaderInterceptor.class).resolve()));

        new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .enableBootstrapInjection(instrumentation, temp)
                .type(ElementMatchers.nameEndsWith(".HttpURLConnection").or(ElementMatchers
                        .nameEndsWith(".ToBeInterceptorClass")))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
                        if (typeDescription.getActualName().endsWith(".ToBeInterceptorClass")) {
                            return new InvokerClassInterceptor().prepareToInterceptor
                                    (typeDescription.getActualName(), classLoader, builder);
                        } else {
                            return builder.method(ElementMatchers.named("getRequestMethod"))
                                    .intercept(MethodDelegation.to(BootstrapClassLoaderInterceptor.class));
                        }
                    }
                }).installOn(instrumentation);
    }

}
