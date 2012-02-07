package com.lexicalscope.contest;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyFactory.ClassLoaderProvider;

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

public class ConcurrentTest {
    public static final ThreadLocal<ProxyFactory> proxyFactory = new ThreadLocal<ProxyFactory>();
    TestRun testRun;

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

    public <T> Channel<T> channel(final Class<T> klass) {
        return new ChannelImpl<T>();
    }
}
