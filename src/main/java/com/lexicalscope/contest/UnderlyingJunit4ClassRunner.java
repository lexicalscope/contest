package com.lexicalscope.contest;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
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

public class UnderlyingJunit4ClassRunner extends BlockJUnit4ClassRunner {
    private final ConcurrentTestMethod concurrentTestMethod;
    private final Description description;

    public UnderlyingJunit4ClassRunner(
            final Class<?> klass,
            final ConcurrentTestMethod concurrentTestMethod,
            final Description description) throws InitializationError {
        super(klass);
        this.description = description;
        if (concurrentTestMethod == null) {
            throw new NullPointerException();
        }
        this.concurrentTestMethod = concurrentTestMethod;
    }

    @Override protected Object createTest() throws Exception {
        return super.createTest();
    }

    @Override protected void validateInstanceMethods(final List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);
    }

    @Override protected List<FrameworkMethod> computeTestMethods() {
        final List<FrameworkMethod> result = new ArrayList<FrameworkMethod>();
        final FrameworkMethod frameworkMethod = concurrentTestMethod.getFrameworkMethod();
        result.add(frameworkMethod);
        return result;
    }

    @Override protected Description describeChild(final FrameworkMethod method) {
        return description;
    }
}
