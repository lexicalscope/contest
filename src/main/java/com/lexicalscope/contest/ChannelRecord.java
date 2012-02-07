package com.lexicalscope.contest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

public abstract class ChannelRecord<T> implements Action, CallRecord {
    protected final Channel<T> channel;

    private Object target;
    private Method method;
    private Object[] args;

    public ChannelRecord(final Channel<T> channel) {
        this.channel = channel;
    }

    @SuppressWarnings("unchecked") protected final T invokeRecordedMethod()
            throws IllegalAccessException,
            InvocationTargetException {
        return (T) method.invoke(target, args);
    }

    public final void callOn(final Object target, final Method method, final Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public final <S> S from(final S objectUnderTest) {
        ((ContestObjectUnderTest) objectUnderTest).contest_tellMeAboutTheNextInvokation(this);
        return objectUnderTest;
    }
}
