package com.lexicalscope.contest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyFactory.ClassLoaderProvider;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

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

@SuppressWarnings("deprecation") public class ConcurrentTest implements MethodRule {
    public static final ThreadLocal<ProxyFactory> proxyFactory = new ThreadLocal<ProxyFactory>();
    private TestRun testRun;

    public ConcurrentTest() {
        ProxyFactory.classLoaderProvider = new ClassLoaderProvider() {
            public ClassLoader get(final ProxyFactory pf) {
                return ConcurrentTest.class.getClassLoader();
            }
        };
        proxyFactory.set(new ProxyFactory());
    }

    public void executing(final TestRun testRun) {
        this.testRun = testRun;
    }

    public <T> T testing(final T classUnderTest) {
        return new TestingStub<T>(classUnderTest).create();
    }

    static ProxyFactory proxyFactory()
    {
        return proxyFactory.get();
    }

    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override public void evaluate() throws Throwable {
                base.evaluate();
                final ConcurrentTestMethod currentTestMethod = ConcurrentTestScheduleRunner.currentTestMethod.get();

                testRun.execute(instanciate(target, currentTestMethod.getSchedule()));

                instanciate(target, currentTestMethod.getTheory()).execute();
            }

            private <T> T instanciate(final Object target, final Class<? extends T> scheduleClass)
                    throws Throwable {
                if (scheduleClass.isMemberClass() && !Modifier.isStatic(scheduleClass.getModifiers()))
                {
                    final Constructor<? extends T> constructor =
                            scheduleClass.getDeclaredConstructor(target.getClass());
                    constructor.setAccessible(true);
                    return constructor.newInstance(target);
                }
                else
                {
                    return scheduleClass.newInstance();
                }
            }
        };
    }

    public <T> Channel<T> channel(final Class<T> klass) {
        return new ChannelImpl<T>();
    }
}
