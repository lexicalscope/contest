package com.lexicalscope.contest;

import java.util.ArrayList;
import java.util.List;

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
    private final List<Enum> trace = new ArrayList<Enum>();
    private final List<ScheduleRecord> scheduleRecords = new ArrayList<ScheduleRecord>();

    public ScheduleRecord action(final Enum action) {
        final ScheduleRecord scheduleRecord = new ScheduleRecord(this);
        scheduleRecord.addFirst(action);
        scheduleRecords.add(scheduleRecord);
        return scheduleRecord;
    }

    public synchronized void enforceSchedule_beforeAction(final Enum action) {
        while (!allowed(action))
        {
            try {
                wait();
            } catch (final InterruptedException e) {
                // nothing yet
            }
        }
    }

    private boolean allowed(final Enum action) {
        for (final ScheduleRecord scheduleRecord : scheduleRecords) {
            final List<Enum> actions = scheduleRecord.actions;
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

    public synchronized void enforceSchedule_afterAction(final Enum action) {
        trace.add(action);
        notifyAll();
    }
}
