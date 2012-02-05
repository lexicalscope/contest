package com.lexicalscope.contest;

import java.lang.reflect.Modifier;

import javassist.util.proxy.ProxyFactory;

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
    private TestRun testRun;

    public void checking(final TestRun testRun) {
        this.testRun = testRun;
    }

    public <T> T testing(final T classUnderTest) {
        return new TestingStub<T>(classUnderTest).create();
    }

    static ProxyFactory proxyFactory()
    {
        return ConcurrentTestRunner.proxyFactory.get();
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
                    return scheduleClass.getDeclaredConstructor(target.getClass()).newInstance(target);
                }
                else
                {
                    return scheduleClass.newInstance();
                }
            }
        };
    }
}
