package com.lexicalscope.contest;

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

    public Statement apply(final Statement statement, final FrameworkMethod method, final Object object) {
        return new Statement() {
            @Override public void evaluate() throws Throwable {
                statement.evaluate();
                testRun.launch();
            }
        };
    }

    public static <T> TestingStub<T> testing(final T classUnderTest) {
        return new TestingStub<T>(classUnderTest);
    }
}
