package com.lexicalscope.contest;

import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class ChannelImpl<T> implements Channel<T> {
    private final List<T> messages = synchronizedList(new ArrayList<T>());

    public Iterator<T> iterator() {
        return new ArrayList<T>(messages).iterator();
    }

    public void push(final T value) {
        messages.add(value);
    }
}
