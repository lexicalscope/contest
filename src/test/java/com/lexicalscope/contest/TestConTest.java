package com.lexicalscope.contest;

import static com.lexicalscope.contest.TestConTest.MultisetActions.*;
import static com.lexicalscope.contest.TestConTest.Threads.*;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

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
@RunWith(ConcurrentTestRunner.class) public class TestConTest {
    @Rule public ConcurrentTest context = new ConcurrentTest();

    enum MultisetActions
    {
        FirstAdd,
        SecondAdd,
        Remove
    }

    enum Threads
    {
        AddingThread,
        RemovingThread
    }

    class AddsBeforeRemove extends BaseSchedule
    {
        {
            action(FirstAdd).isBefore(Remove);
            action(SecondAdd).isBefore(Remove);
        }
    }

    class RemoveBeforeAdds extends BaseSchedule
    {
        {
            action(Remove).isBefore(FirstAdd);
            action(Remove).isBefore(SecondAdd);
        }
    }

    class MultiSetSizeIsOne extends BaseTheory
    {
        {
            asserting(that(multiset).size(), equalTo(1));
        }
    }

    class MultiSetSizeIsTwo extends BaseTheory
    {
        {
            asserting(that(multiset).size(), equalTo(2));
        }
    }

    final Multiset<Object> multiset = context.testing(ConcurrentHashMultiset.create());

    @Test @Schedules({
            @Schedule(
                    when = AddsBeforeRemove.class,
                    then = MultiSetSizeIsOne.class),
            @Schedule(
                    when = RemoveBeforeAdds.class,
                    then = MultiSetSizeIsTwo.class) }) public void twoAddsOneRemove()
    {
        context.checking(new TestRun() {
            {
                inThread(AddingThread).action(FirstAdd).is(multiset).add(42);
                inThread(AddingThread).action(SecondAdd).is(multiset).add(42);
                inThread(RemovingThread).action(Remove).is(multiset).remove(42);
            }
        });
    }

    @Test @Schedule(
            when = AddsBeforeRemove.class,
            then = MultiSetSizeIsOne.class) public void onlyOneSchedule()
    {
        context.checking(new TestRun() {
            {
                inThread(AddingThread).action(FirstAdd).is(multiset).add(42);
                inThread(AddingThread).action(SecondAdd).is(multiset).add(42);
                inThread(RemovingThread).action(Remove).is(multiset).remove(42);
            }
        });
    }

    //
    //    @Test @Schedule(AddsBeforeRemove.class) public void concurrentTestX()
    //    {
    //        final Multiset<Object> multiset = context.testing(ConcurrentHashMultiset.create());
    //
    //        new TestThread() {
    //            {
    //                action(FirstAdd).is(multiset).add(42);
    //                action(SecondAdd).is(multiset).add(42);
    //            }
    //        };
    //
    //        new TestThread() {
    //            {
    //                action(Remove).is(multiset).remove(42);
    //            }
    //        };
    //
    //        asserting(that(multiset).size(), equalTo(1));
    //    }

    //    @Test public void simpleConcurrentTest()
    //    {
    //        final Multiset<Object> multiset =
    //                context.testing(ConcurrentHashMultiset.create()).as(new TypeLiteral<Multiset<Object>>() {});
    //
    //        context.checking(new TestRun() {
    //            {
    //                // implcit sequence
    //                inThread(AddingThread).call(multiset).add(42);
    //                inThread(RemovingThread).call(multiset).remove(42);
    //                inThread(AddingThread).call(multiset).add(42);
    //
    //                asserting(that(multiset).size(), equalTo(1));
    //            }
    //        });
    //    }
}
