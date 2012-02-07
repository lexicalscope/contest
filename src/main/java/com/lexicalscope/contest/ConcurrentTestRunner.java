package com.lexicalscope.contest;

import java.lang.reflect.InvocationTargetException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;

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

    public ConcurrentTestRunner(final Class<?> test) throws Throwable {
        classpool = ClassPool.getDefault();
        final Unfinaliser myTrans = new Unfinaliser();
        classloader = new Loader(Thread.currentThread().getContextClassLoader(), classpool);
        classloader.delegateLoadingOf("org.junit.");
        classloader.delegateLoadingOf("javassist.");

        try {
            classloader.addTranslator(classpool, myTrans);

            underlyingRunnerClass = classloader.loadClass("com.lexicalscope.contest.ConcurrentTestScheduleRunner");

            final Class<?> classToTest = classloader.loadClass(test.getName());
            underlyingRunner =
                    underlyingRunnerClass
                    .getConstructor(Class.class)
                    .newInstance(classToTest);
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
}
