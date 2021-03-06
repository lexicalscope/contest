package com.lexicalscope.e2e.contest;

import static com.lexicalscope.e2e.contest.TestLinkedBlockingDeque._.*;
import static org.hamcrest.Matchers.contains;

import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.lexicalscope.contest.BaseSchedule;
import com.lexicalscope.contest.BaseTheory;
import com.lexicalscope.contest.Channel;
import com.lexicalscope.contest.ConcurrentContext;
import com.lexicalscope.contest.ConcurrentTest;
import com.lexicalscope.contest.ConcurrentTestRunner;
import com.lexicalscope.contest.Schedule;
import com.lexicalscope.contest.Schedules;
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

@RunWith(ConcurrentTestRunner.class) public class TestLinkedBlockingDeque {
    @ConcurrentContext public ConcurrentTest context = new ConcurrentTest();

    private final LinkedBlockingDeque<Object> queue = context.testing(new LinkedBlockingDeque<Object>());
    private final Channel<Object> dequeuedItems = context.channel(Object.class);

    private final Object item1 = new Object();
    private final Object item2 = new Object();

    @Test @Schedules({
        @Schedule(when = Add1ThenAdd2.class, then = Message1BeforeMessage2.class),
        @Schedule(when = Add2ThenAdd1.class, then = Message2BeforeMessage1.class),
    }) public void twoOffersTwoTakes() throws InterruptedException {
        context.executing(new TestRun() {{
            action(Add1).is(queue).offer(item1);
            action(Add2).is(queue).offer(item2);

            inThread(Consumer).receive(dequeuedItems).from(queue).take();
            inThread(Consumer).receive(dequeuedItems).from(queue).take();
        }});
    }

    @Test @Schedules({
        @Schedule(when = Add1ThenAdd2.class, then = Message1BeforeMessage2.class),
        @Schedule(when = Add2ThenAdd1.class, then = Message2BeforeMessage1.class),
    }) public void twoOffersTwoPolls() throws InterruptedException{
        context.executing(new TestRun() {{
            action(Add1).is(queue).offer(item1);
            action(Add2).is(queue).offer(item2);

            inThread(Consumer).poll(dequeuedItems).from(queue).poll();
            inThread(Consumer).poll(dequeuedItems).from(queue).poll();
        }});
    }

    class Add1ThenAdd2 extends BaseSchedule {{
        action(Add1).isBefore(Add2);
    }}

    class Add2ThenAdd1 extends BaseSchedule {{
        action(Add2).isBefore(Add1);
    }}

    class Message1BeforeMessage2 extends BaseTheory {{
        asserting(dequeuedItems, contains(item1, item2));
    }}

    class Message2BeforeMessage1 extends BaseTheory {{
        asserting(dequeuedItems, contains(item2, item1));
    }}

    enum _ { Producer, Consumer, Add1, Add2, Remove1, Remove2 }
}
