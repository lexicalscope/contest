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

    class AddAddRemove extends BaseSchedule
    {
        {
            action(FirstAdd).isBefore(Remove);
            action(SecondAdd).isBefore(Remove);
        }
    }

    class RemoveAddAdd extends BaseSchedule
    {
        {
            action(Remove).isBefore(FirstAdd);
            action(Remove).isBefore(SecondAdd);
        }
    }

    class AddRemoveAdd extends BaseSchedule
    {
        {
            action(FirstAdd).isBefore(Remove).isBefore(SecondAdd);
        }
    }

    class SizeIsOne extends BaseTheory
    {
        {
            asserting(that(multiset).size(), equalTo(1));
        }
    }

    class SizeIsTwo extends BaseTheory
    {
        {
            asserting(that(multiset).size(), equalTo(2));
        }
    }

    final Multiset<Object> multiset = context.testing(ConcurrentHashMultiset.create());

    @Test @Schedules({
            @Schedule(
                    when = AddAddRemove.class,
                    then = SizeIsOne.class),
            @Schedule(
                    when = AddRemoveAdd.class,
                    then = SizeIsOne.class),
            @Schedule(
                    when = RemoveAddAdd.class,
                    then = SizeIsTwo.class)
    }) public void twoAddsOneRemove()
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
            when = AddAddRemove.class,
            then = SizeIsOne.class) public void onlyOneSchedule()
    {
        context.checking(new TestRun() {
            {
                inThread(AddingThread).action(FirstAdd).is(multiset).add(42);
                inThread(AddingThread).action(SecondAdd).is(multiset).add(42);
                inThread(RemovingThread).action(Remove).is(multiset).remove(42);
            }
        });
    }
}
