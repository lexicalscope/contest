package com.lexicalscope.contest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

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

public class ConcurrentTestScheduleRunner extends ParentRunner<ConcurrentTestMethod> {
    static final class DefaultSchedule extends BaseSchedule {}

    static final class DefaultTheory extends BaseTheory {}

    static final ThreadLocal<ConcurrentTestMethod> currentTestMethod = new ThreadLocal<ConcurrentTestMethod>();

    public ConcurrentTestScheduleRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override protected List<ConcurrentTestMethod> getChildren() {
        final List<ConcurrentTestMethod> result = new ArrayList<ConcurrentTestMethod>();

        for (final FrameworkMethod frameworkMethod : getTestClass().getAnnotatedMethods(Test.class)) {
            if (frameworkMethod.getAnnotation(Schedule.class) != null)
            {
                result.add(new ConcurrentTestMethod(frameworkMethod, frameworkMethod.getAnnotation(Schedule.class)));
            }
            else if (frameworkMethod.getAnnotation(Schedules.class) != null)
            {
                for (final Schedule schedule : frameworkMethod.getAnnotation(Schedules.class).value()) {
                    result.add(new ConcurrentTestMethod(frameworkMethod, schedule));
                }
            }
            else
            {
                result.add(new ConcurrentTestMethod(frameworkMethod, DefaultTheory.class, DefaultSchedule.class));
            }
        }
        return result;
    }

    @Override protected Description describeChild(final ConcurrentTestMethod child) {
        return Description
                .createTestDescription(getTestClass().getJavaClass(), child.getName(), child.getAnnotations());
    }

    @Override protected void runChild(final ConcurrentTestMethod child, final RunNotifier notifier) {
        try {
            currentTestMethod.set(child);
            new UnderlyingJunit4ClassRunner(getTestClass().getJavaClass(), child, describeChild(child)).run(notifier);
        } catch (final InitializationError e) {
            throw new RuntimeException(e);
        }
    }
}
