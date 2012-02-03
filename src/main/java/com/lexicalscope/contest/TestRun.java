package com.lexicalscope.contest;

import org.hamcrest.Matcher;

import com.google.common.collect.LinkedHashMultimap;

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

public class TestRun {
    private final LinkedHashMultimap<Enum, ThreadRecord> threads = LinkedHashMultimap.<Enum, ThreadRecord>create();

    protected ActionRecord action(final Enum action) {
        // TODO Auto-generated method stub
        return null;
    }

    protected <T> T that(final T object) {
        // TODO Auto-generated method stub
        return null;
    }

    protected <T> void asserting(final T size, final Matcher<T> equalTo) {
        // TODO Auto-generated method stub
    }

    protected ThreadRecord inThread(final Enum thread) {
        final ThreadRecord threadRecord = new ThreadRecord(thread);
        threads.put(thread, threadRecord);
        return threadRecord;
    }

    public void launch() {
        // TODO Auto-generated method stub

    }
}
