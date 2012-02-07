package com.lexicalscope.contest;

import java.lang.reflect.InvocationTargetException;

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

public class PollingChannelRecord<T> extends ChannelRecord<T> {
    public PollingChannelRecord(final Channel<T> channel) {
        super(channel);
    }

    public void execute() throws Throwable {
        try {
            T value = null;
            while (value == null)
            {
                value = invokeRecordedMethod();
            }
            channel.push(value);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
