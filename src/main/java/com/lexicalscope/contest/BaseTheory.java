package com.lexicalscope.contest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

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

public class BaseTheory implements CallRecord {
    private final List<Action> assertions = new ArrayList<Action>();

    private Object target;
    private Method method;
    private Object[] args;

    private Mode mode = Mode.Direct;

    public void callOn(final Object target, final Method method, final Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    protected <T> T that(final T objectUnderTest) {
        mode = Mode.Record;
        ((ContestObjectUnderTest) objectUnderTest).contest_tellMeAboutTheNextInvokation(this);
        return objectUnderTest;
    }

    protected <T> void asserting(final T assertionTarget, final Matcher<T> matcher) {
        // TODO: detect if its proxied or not
        if (mode == Mode.Record)
        {
            assertions.add(new ProxiedAssertion(target, method, args, matcher));
        }
        else
        {
            assertions.add(new DirectAssertion(assertionTarget, matcher));
        }
    }

    public void execute() throws Throwable
    {
        for (final Action assertion : assertions) {
            assertion.execute();
        }
    }

    private enum Mode {
        Record, Direct
    }
}
