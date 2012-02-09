package com.lexicalscope.contest;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private final LinkedHashMultimap<Object, ThreadRecord> threads = LinkedHashMultimap.<Object, ThreadRecord>create();

    protected ThreadRecord inThread(final Object thread) {
        final ThreadRecord threadRecord = new ThreadRecord(thread);
        threads.put(thread, threadRecord);
        return threadRecord;
    }

    protected ActionRecord action(final Object action) {
        return inThread(new Object()).action(action);
    }

    protected ChannelRecord<Object> receive(final Channel<Object> channel) {
        return inThread(new Object()).action(new Object()).take(channel);
    }

    void execute(final BaseSchedule schedule) throws Throwable {
        final CountDownLatch terminationBarrier = new CountDownLatch(threads.asMap().entrySet().size());
        final TestThreadState threadState = new TestThreadState(terminationBarrier);

        final List<Thread> threadList = new ArrayList<Thread>();
        for (final Entry<Object, Collection<ThreadRecord>> threadEntry : threads.asMap().entrySet()) {
            final List<ThreadRecord> recordsForThread = new ArrayList<ThreadRecord>(threadEntry.getValue());
            threadList.add(new Thread(threadEntry.getKey().toString()) {
                @Override public void run() {
                    try {
                        for (final ThreadRecord threadRecord : recordsForThread) {
                            schedule.enforceSchedule_beforeAction(threadState, threadRecord.action);
                            threadRecord.actionRecord.execute();
                            schedule.enforceSchedule_afterAction(threadState, threadRecord.action);
                            if(threadState.anyFailedThreads())
                            {
                                break;
                            }
                        }
                    } catch (final Throwable e) {
                        threadState.threadFailed(e);
                    }
                    finally
                    {
                        terminationBarrier.countDown();
                    }
                }
            });
        }

        for (final Thread thread : threadList) {
            thread.start();
        }

        while(terminationBarrier.getCount() != 0)
        {
            terminationBarrier.await(200, TimeUnit.MILLISECONDS);
            deadlockDetection(threadState);
        }

        if(threadState.anyFailedThreads())
        {
            throw new FailedThreadsException(threadState.threadFailures());
        }
    }

    private void deadlockDetection(final TestThreadState threadState) {
        final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        final long[] threadIds = bean.findDeadlockedThreads(); // Returns null if no threads are deadlocked.
        if (threadIds != null) {
            final HashMap<Thread, Object> threadWaitConditions = threadState.threadWaitConditions();
            throw new DeadlockDetectedException(threadWaitConditions);
        }
    }
}
