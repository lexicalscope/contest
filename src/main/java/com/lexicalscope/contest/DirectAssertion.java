package com.lexicalscope.contest;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

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

public class DirectAssertion implements Action {
    @SuppressWarnings("rawtypes") private final Matcher matcher;
    private final Object target;

    public DirectAssertion(final Object target, final Matcher<?> matcher) {
        this.target = target;
        this.matcher = matcher;
    }

    @SuppressWarnings("unchecked") public void execute() throws Throwable {
        MatcherAssert.assertThat(target, matcher);
    }
}