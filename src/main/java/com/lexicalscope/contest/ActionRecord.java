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
    private Object target;
    private Method method;
    private Object[] args;

    public <T> T is(final T objectUnderTest)
    {
        final Method[] interfaces = objectUnderTest.getClass().getMethods();
        for (final Method method : interfaces) {
            if (method.getParameterTypes().length == 1)
            {
                System.out.println(" ** " + method + " and " + method.getParameterTypes()[0].equals(CallRecord.class));
            }
        }
        //
        //        try {
        //            objectUnderTest
        //                    .getClass()
        //                    .getMethod("contest_tellMeAboutTheNextInvokation", CallRecord.class)
        //                    .invoke(objectUnderTest, this);
        //        } catch (final IllegalArgumentException e) {
        //            throw new RuntimeException(e);
        //        } catch (final SecurityException e) {
        //            throw new RuntimeException(e);
        //        } catch (final IllegalAccessException e) {
        //            throw new RuntimeException(e);
        //        } catch (final InvocationTargetException e) {
        //            throw new RuntimeException(e);
        //        } catch (final NoSuchMethodException e) {
        //            throw new RuntimeException(e);
        //        }
        ((ContestObjectUnderTest) objectUnderTest).contest_tellMeAboutTheNextInvokation(this);
        return objectUnderTest;
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
