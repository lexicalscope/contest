package com.lexicalscope.contest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

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

    protected ThreadRecord inThread(final Enum thread) {
        final ThreadRecord threadRecord = new ThreadRecord(thread);
        threads.put(thread, threadRecord);
        return threadRecord;
    }

    public void execute(final BaseSchedule schedule) throws Throwable {
        final List<Thread> threadList = new ArrayList<Thread>();
        for (final Entry<Enum, Collection<ThreadRecord>> threadEntry : threads.asMap().entrySet()) {
            final List<ThreadRecord> recordsForThread = new ArrayList<ThreadRecord>(threadEntry.getValue());
            threadList.add(new Thread() {
                @Override public void run() {
                    for (final ThreadRecord threadRecord : recordsForThread) {
                        try {
                            schedule.enforceSchedule_beforeAction(threadRecord.action);
                            threadRecord.actionRecord.execute();
                            schedule.enforceSchedule_afterAction(threadRecord.action);
                        } catch (final Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }

        for (final Thread thread : threadList) {
            thread.start();
        }

        for (final Thread thread : threadList) {
            thread.join();
        }
    }
}
