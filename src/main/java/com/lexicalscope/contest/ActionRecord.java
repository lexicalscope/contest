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

public class ActionRecord implements CallRecord, Action {
    private final ThreadRecord threadRecord;

    private Object target;
    private Method method;
    private Object[] args;

    public ActionRecord(final ThreadRecord threadRecord)
    {
        this.threadRecord = threadRecord;
    }

    public <T> T is(final T objectUnderTest)
    {
        ((ContestObjectUnderTest) objectUnderTest).contest_tellMeAboutTheNextInvokation(this);
        threadRecord.actionRecord = this;
        return objectUnderTest;
    }

    /**
     * Blocking receive to the given channel
     * 
     * @param channel
     *            the channel the message will be sent to
     * 
     * @return record a call to the message producer
     */
    public ChannelRecord<Object> receive(final Channel<Object> channel) {
        final ChannelRecord<Object> channelRecord = new BlockingChannelRecord<Object>(channel);
        threadRecord.actionRecord = channelRecord;
        return channelRecord;
    }

    /**
     * Polling receive to the given channel
     * 
     * @param channel
     *            the channel the message will be sent to
     * 
     * @return record a call to the message producer
     */
    public ChannelRecord<Object> poll(final Channel<Object> channel) {
        final ChannelRecord<Object> channelRecord = new PollingChannelRecord<Object>(channel);
        threadRecord.actionRecord = channelRecord;
        return channelRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.lexicalscope.contest.Action#execute()
     */
    public void execute() throws Throwable {
        try {
            method.invoke(target, args);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    public void callOn(final Object target, final Method method, final Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }
}
