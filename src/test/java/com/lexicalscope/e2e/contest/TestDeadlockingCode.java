package com.lexicalscope.e2e.contest;

import static com.lexicalscope.e2e.contest.TestDeadlockingCode._.*;

import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.lexicalscope.contest.BaseSchedule;
import com.lexicalscope.contest.BaseTheory;
import com.lexicalscope.contest.Channel;
import com.lexicalscope.contest.ConcurrentContext;
import com.lexicalscope.contest.ConcurrentTest;
import com.lexicalscope.contest.ConcurrentTestRunner;
import com.lexicalscope.contest.FailedThreadsException;
import com.lexicalscope.contest.Schedule;
import com.lexicalscope.contest.TestRun;

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

@RunWith(ConcurrentTestRunner.class) public class TestDeadlockingCode {
    @ConcurrentContext public ConcurrentTest context = new ConcurrentTest();

    private final LinkedBlockingDeque<Object> queue = context.testing(new LinkedBlockingDeque<Object>());
    private final Channel<Object> dequeuedItems = context.channel(Object.class);

    private final Object item = new Object();

    @Test(expected = FailedThreadsException.class) @Schedule(when = AddTheRemoveThenAdd.class, then = AssertNothing.class)
    public void twoOffersTwoTakes() throws InterruptedException {
        context.executing(new TestRun() {{
            action(Add).is(queue).offer(item);

            action(Remove).receive(dequeuedItems).from(queue).take();
        }});
    }

    class AddTheRemoveThenAdd extends BaseSchedule {{
        action(Add).isBefore(Remove);
        action(Remove).isBefore(Add);
    }}

    class AssertNothing extends BaseTheory {{ }}

    enum _ { Producer, Consumer, Add, Remove }
}
