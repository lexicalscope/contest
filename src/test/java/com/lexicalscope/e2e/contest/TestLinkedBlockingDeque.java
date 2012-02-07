package com.lexicalscope.e2e.contest;

import static com.lexicalscope.e2e.contest.TestLinkedBlockingDeque._.*;
import static org.hamcrest.Matchers.contains;

import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.lexicalscope.contest.BaseSchedule;
import com.lexicalscope.contest.BaseTheory;
import com.lexicalscope.contest.Channel;
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
    @Rule public ConcurrentTest context = new ConcurrentTest();

    private final LinkedBlockingDeque<Object> queue = context.testing(new LinkedBlockingDeque<Object>());
    private final Channel<Object> channel = context.channel(Object.class);

    private final Object message1 = new Object();
    private final Object message2 = new Object();

    @Test @Schedules({
            @Schedule(
                    when = Add1ThenAdd2.class,
                    then = Message1BeforeMessage2.class),
            @Schedule(
                    when = Add2ThenAdd1.class,
                    then = Message2BeforeMessage1.class),
    }) public void twoAddsOneRemove() throws InterruptedException
    {
        context.checking(new TestRun() {
            {
                action(Add1).is(queue).offer(message1);
                action(Add2).is(queue).offer(message2);

                inThread(Consumer).receiveIn(channel).from(queue).take();
                inThread(Consumer).receiveIn(channel).from(queue).take();
            }
        });
    }

    class Add1ThenAdd2 extends BaseSchedule
    {
        {
            action(Add1).isBefore(Add2);
        }
    }

    class Add2ThenAdd1 extends BaseSchedule
    {
        {
            action(Add2).isBefore(Add1);
        }
    }

    class Message1BeforeMessage2 extends BaseTheory
    {
        {
            asserting(channel, contains(message1, message2));
        }
    }

    class Message2BeforeMessage1 extends BaseTheory
    {
        {
            asserting(channel, contains(message2, message1));
        }
    }

    enum _ {
        Producer,
        Consumer,
        Add1,
        Add2,
        Remove1,
        Remove2
    }
}
