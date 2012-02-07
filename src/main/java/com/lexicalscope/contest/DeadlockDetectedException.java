package com.lexicalscope.contest;

import java.util.HashMap;

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

public class DeadlockDetectedException extends RuntimeException {
    private static final long serialVersionUID = -2739307492466973702L;

    public DeadlockDetectedException(final HashMap<Thread, Object> hashMap) {
        super("All test threads are blocked " + hashMap);
    }
}
