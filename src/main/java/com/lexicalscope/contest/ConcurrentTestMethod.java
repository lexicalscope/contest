package com.lexicalscope.contest;

import java.lang.annotation.Annotation;

import org.junit.runners.model.FrameworkMethod;

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

public class ConcurrentTestMethod {
    private final FrameworkMethod frameworkMethod;
    private final Class<? extends BaseTheory> then;
    private final Class<? extends BaseSchedule> when;

    public ConcurrentTestMethod(
            final FrameworkMethod frameworkMethod,
            final Schedule annotation) {
        this(frameworkMethod, annotation.then(), annotation.when());
    }

    public ConcurrentTestMethod(
            final FrameworkMethod frameworkMethod,
            final Class<? extends BaseTheory> then,
            final Class<? extends BaseSchedule> when) {
        this.frameworkMethod = frameworkMethod;
        this.then = then;
        this.when = when;
    }

    public String getName() {
        return frameworkMethod.getName() + "[" + when.getSimpleName() + "]";
    }

    public Annotation[] getAnnotations() {
        return null;
    }

    public FrameworkMethod getFrameworkMethod() {
        return frameworkMethod;
    }

    public Class<? extends BaseSchedule> getSchedule() {
        return when;
    }

    public Class<? extends BaseTheory> getTheory() {
        return then;
    }
}
