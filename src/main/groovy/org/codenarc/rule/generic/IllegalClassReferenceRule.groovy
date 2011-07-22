/*
 * Copyright 2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.generic

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AstVisitor
import org.codenarc.rule.ClassReferenceAstVisitor

/**
 * Checks for reference to any of the named classes.
 * <p/>
 * The <code>classNames</code> property specifies the comma-separated list of (fully-qualified) class names to check for.
 * The class name(s) may optionally include wildcard characters ('*' or '?'). Note that the '*' wildcard
 * matches any sequence of zero or more characters in the class/package name, e.g. 'a.*.MyClass' matches
 * 'a.b.MyClass' as well as 'a.b.c.d.MyClass'. If <code>classNames</code> is null or empty, do nothing.
 *
 * Known limitation: Does not catch references as Anonymous Inner class: def x = new org.bad.Handler() { .. }
 *
 * @author Chris Mair
 */
class IllegalClassReferenceRule extends AbstractAstVisitorRule {
    String name = 'IllegalClassReference'
    int priority = 2
    String classNames = null

    @Override
    AstVisitor getAstVisitor() {
        new ClassReferenceAstVisitor(classNames)
    }

    @Override
    boolean isReady() {
        classNames
    }
}
