package com.lexicalscope.contest;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.google.common.base.Defaults;

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

public class TestingStub<S> {
    private final ProxyFactory proxyFactory = ConcurrentTest.proxyFactory();
    private final S classUnderTest;

    public TestingStub(final S classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    @SuppressWarnings("unchecked") public S create() {
        final Class<? extends Object> klass = classUnderTest.getClass();

        try {
            proxyFactory.setSuperclass(klass);
            proxyFactory.setInterfaces(new Class[] { ContestObjectUnderTest.class });

            final Objenesis o = new ObjenesisStd();
            final Object proxyInstance = o.newInstance(proxyFactory.createClass());
            ((ProxyObject) proxyInstance).setHandler(new MethodHandler() {
                CallRecord callRecord;

                public Object invoke(final Object arg0, final Method arg1, final Method arg2, final Object[] arg3)
                        throws Throwable {
                    if (arg1.getName().equals("contest_tellMeAboutTheNextInvokation"))
                    {
                        callRecord = (CallRecord) arg3[0];
                    }
                    else
                    {
                        callRecord.callOn(classUnderTest, arg1, arg3);
                        callRecord = null;
                    }
                    if (arg1.getReturnType().isPrimitive())
                    {
                        return Defaults.defaultValue(arg1.getReturnType());
                    }
                    return null;
                }
            });
            return (S) proxyInstance;
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
