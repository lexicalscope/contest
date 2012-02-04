package com.lexicalscope.contest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/*
 * Copyright 2011 Tim Wood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

public class ConcurrentTestRunner extends Runner {
    private Object underlyingRunner;
    private Class<?> underlyingRunnerClass;

    private final Loader classloader;

    private final ClassPool classpool;

    private ProxyFactory proxyFactory;

    public ConcurrentTestRunner(final Class<?> test) throws Throwable {
        classpool = ClassPool.getDefault();
        final Unfinaliser myTrans = new Unfinaliser();
        classloader = new Loader(Thread.currentThread().getContextClassLoader(), classpool);
        classloader.delegateLoadingOf("org.junit.");
        classloader.delegateLoadingOf("javassist.");

        try {
            classloader.addTranslator(classpool, myTrans);

            proxyFactory = new ProxyFactory();
            proxyFactory.setSuperclass(classloader.loadClass("org.junit.runners.BlockJUnit4ClassRunner"));
            underlyingRunnerClass = proxyFactory.createClass(new MethodFilter() {
                public boolean isHandled(final Method m) {
                    return m.getName().equals("createTest");
                }
            });

            final Class<?> classToTest = classloader.loadClass(test.getName());
            underlyingRunner =
                    underlyingRunnerClass
                            .getConstructor(Class.class)
                            .newInstance(classToTest);
            ((ProxyObject) underlyingRunner).setHandler(new MethodHandler() {
                public Object invoke(
                        final Object self,
                        final Method thisMethod,
                        final Method proceed,
                        final Object[] args) throws Throwable {
                    return initalise(classToTest.newInstance());
                }
            });
        } catch (final NotFoundException e) {
            throw new RuntimeException(e);
        } catch (final CannotCompileException e) {
            throw new RuntimeException(e);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @Override public Description getDescription() {
        try {
            return (Description) underlyingRunnerClass.getMethod("getDescription").invoke(underlyingRunner);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void run(final RunNotifier notifier) {
        try {

            underlyingRunnerClass.getMethod("run", notifier.getClass()).invoke(underlyingRunner, notifier);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object initalise(final Object test)
    {
        final Field[] fields = test.getClass().getFields();
        for (final Field field : fields) {
            if (field.getType().getName().equals("com.lexicalscope.contest.ConcurrentTest")) {
                try {
                    field.set(
                            test,
                            classloader.loadClass("com.lexicalscope.contest.ConcurrentTest").getConstructor(
                                    javassist.util.proxy.ProxyFactory.class).newInstance(proxyFactory));
                } catch (final SecurityException e) {
                    throw new RuntimeException(e);
                } catch (final IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (final NoSuchMethodException e) {
                    throw new RuntimeException(e);
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (final InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (final InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (final ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return test;
    }
}
