package com.lexicalscope.contest;

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

public class ConcurrentTest implements MethodRule {
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
                final Schedule schedule = method.getMethod().getAnnotation(Schedule.class);

                final Class<? extends BaseSchedule> scheduleClass = schedule.value();

                final BaseSchedule scheduleInstance;
                if (scheduleClass.isMemberClass())
                {
                    scheduleInstance = scheduleClass.getDeclaredConstructor(target.getClass()).newInstance(target);
                }
                else
                {
                    scheduleInstance = scheduleClass.newInstance();
                }

                testRun.execute(scheduleInstance);
            }
        };
    }
}
