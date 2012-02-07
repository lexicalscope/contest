package com.lexicalscope.contest;

import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

public class ProxiedAssertion implements Action {
    @SuppressWarnings("rawtypes") private final Matcher matcher;
    private final Object target;
    private final Method method;
    private final Object[] args;

    public ProxiedAssertion(final Object target, final Method method, final Object[] args, final Matcher<?> matcher) {
        this.target = target;
        this.method = method;
        this.args = args;
        this.matcher = matcher;
    }

    @SuppressWarnings("unchecked") public void execute() throws Throwable {
        try {
            assertThat(method.invoke(target, args), matcher);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
