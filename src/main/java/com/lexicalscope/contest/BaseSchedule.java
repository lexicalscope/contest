package com.lexicalscope.contest;

import static java.lang.Thread.currentThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class BaseSchedule {
    private final List<ScheduleRecord> scheduleRecords = new ArrayList<ScheduleRecord>();
    private final Map<Object, Object> actionMonitors = new HashMap<Object, Object>();
    private final List<Object> trace = new ArrayList<Object>();

    public synchronized ScheduleRecord action(final Enum<?> action) {
        final ScheduleRecord scheduleRecord = new ScheduleRecord(this, action);
        scheduleRecords.add(scheduleRecord);
        return scheduleRecord;
    }

    synchronized void registerAction(final Object action)
    {
        if(!actionMonitors.containsKey(action))
        {
            actionMonitors.put(action, new Object());
        }
    }

    public synchronized void enforceSchedule_beforeAction(final TestThreadState threadState, final Object action) {
        while (!allowed(action) && !threadState.anyFailedThreads())
        {
            threadState.threadBlocked(currentThread(), action);
            try {
                wait();
            } catch (final InterruptedException e) {
                // nothing yet
            }
        }

        if(threadState.anyFailedThreads())
        {
            throw new RuntimeException("another thread failed, terminating early");
        }
        threadState.threadProgressing(currentThread());
    }

    private boolean allowed(final Object action) {
        for (final ScheduleRecord scheduleRecord : scheduleRecords) {
            final List<Object> actions = scheduleRecord.actions;
            if (actions.contains(action))
            {
                final int index = actions.indexOf(action);
                for (int i = 0; i < index; i++) {
                    if (!trace.contains(actions.get(i)))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public synchronized void enforceSchedule_afterAction(final TestThreadState threadState, final Object action) {
        trace.add(action);
        threadState.unblock(action);
        notifyAll();
    }
}
