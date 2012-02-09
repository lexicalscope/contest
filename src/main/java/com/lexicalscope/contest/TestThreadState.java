package com.lexicalscope.contest;

import static java.lang.Thread.currentThread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

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

public class TestThreadState {
    private final HashMap<Thread, Throwable> failedThreads = new HashMap<Thread, Throwable>();
    private final HashMap<Thread, Object> blockedThreads = new HashMap<Thread, Object>();
    private final CountDownLatch terminationBarrier;

    public TestThreadState(final CountDownLatch terminationBarrier) {
        this.terminationBarrier = terminationBarrier;
    }

    public synchronized void threadBlocked(final Thread thread, final Object action)
    {
        blockedThreads.put(thread, action);
        if(blockedThreads.size() == terminationBarrier.getCount())
        {
            throw new DeadlockDetectedException(threadWaitConditions());
        }
    }

    public synchronized void unblock(final Object action) {
        final Iterator<Entry<Thread, Object>> it = blockedThreads.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<Thread, Object> blocked = it.next();
            if(blocked.getValue().equals(action))
            {
                it.remove();
            }
        }
    }

    public synchronized void threadProgressing(final Thread thread)
    {
        blockedThreads.remove(thread);
    }

    public synchronized void threadFailed(final Throwable e) {
        failedThreads.put(currentThread(), e);
        for (final Thread thread : blockedThreads.keySet()) {
            thread.interrupt();
        }
    }

    public synchronized boolean anyFailedThreads() {
        return !failedThreads.isEmpty();
    }

    public synchronized Map<Thread, Throwable> threadFailures()
    {
        return new HashMap<Thread, Throwable>(failedThreads);
    }

    public synchronized HashMap<Thread, Object> threadWaitConditions() {
        return new HashMap<Thread, Object>(blockedThreads);
    }
}
