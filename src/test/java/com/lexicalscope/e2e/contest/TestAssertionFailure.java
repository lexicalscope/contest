package com.lexicalscope.e2e.contest;

import static com.lexicalscope.e2e.contest.TestAssertionFailure._.*;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.lexicalscope.contest.BaseSchedule;
import com.lexicalscope.contest.BaseTheory;
import com.lexicalscope.contest.ConcurrentContext;
import com.lexicalscope.contest.ConcurrentTest;
import com.lexicalscope.contest.ConcurrentTestRunner;
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

@RunWith(ConcurrentTestRunner.class) public class TestAssertionFailure {
    @ConcurrentContext public ConcurrentTest context = new ConcurrentTest();
    @Rule public final ExpectedException exception = ExpectedException.none();

    private final Multiset<Object> multiset = context.testing(ConcurrentHashMultiset.create());

    @Test @Schedule(when = AddAddRemove.class, then = SizeIsTwo.class)
    public void onlyOneSchedule()
    {
        context.executing(new TestRun() {{
            inThread(Producer).action(FirstAdd).is(multiset).add(42);
            inThread(Producer).action(SecondAdd).is(multiset).add(42);
            inThread(Consumer).action(Remove).is(multiset).remove(42);
        }});

        exception.expect(AssertionError.class);
        exception.expectMessage("Expected: <2>");
    }

    class SizeIsTwo extends BaseTheory {{
        asserting(that(multiset).size(), equalTo(2));
    }}

    class AddAddRemove extends BaseSchedule {{
        action(FirstAdd).isBefore(Remove);
        action(SecondAdd).isBefore(Remove);
    }}

    enum _ { FirstAdd, SecondAdd, Remove, Producer, Consumer }
}
