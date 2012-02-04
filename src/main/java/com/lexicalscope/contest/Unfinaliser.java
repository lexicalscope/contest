package com.lexicalscope.contest;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.Translator;

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

public class Unfinaliser implements Translator {
    public void start(final ClassPool pool) throws NotFoundException, CannotCompileException {
        // fine
    }

    public void onLoad(final ClassPool pool, final String classname) throws NotFoundException, CannotCompileException {
        final CtClass ctClass = pool.getCtClass(classname);
        final int modifiers = ctClass.getModifiers();

        if (Modifier.isFinal(modifiers)) {
            ctClass.setModifiers(Modifier.clear(modifiers, Modifier.FINAL));
        }
    }
}
