package com.lexicalscope.contest;

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

public class ThreadRecord {
    Action actionRecord;
    Object action;

    public ThreadRecord(final Object thread) {
        // TODO Auto-generated constructor stub
    }

    public ActionRecord action(final Object action) {
        if(action == null)
        {
            throw new NullPointerException("null actions are not allowed");
        }
        if(this.action != null)
        {
            throw new RuntimeException("Action is already set to " + action + " cannot change it to " + action);
        }
        this.action = action;
        return new ActionRecord(this);
    }

    public ChannelRecord<Object> receive(final Channel<Object> channel) {
        return action(new Object()).take(channel);
    }

    public ChannelRecord<Object> poll(final Channel<Object> channel) {
        return action(new Object()).poll(channel);
    }
}
