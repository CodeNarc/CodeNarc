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

import org.codenarc.rule.AbstractClassReferenceRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for IllegalClassReferenceRule - checks for specifying a multiple, comma-separated class names for the classNames field
 *
 * @see IllegalClassReferenceRule_SingleClassNameTest
 * @see IllegalClassReferenceRule_WildcardsClassNamesTest
 *
 * @author Chris Mair
 */
class IllegalClassReferenceRule_MultipleClassNamesTest extends AbstractClassReferenceRuleTestCase {

    final String className = 'com.example.MyExampleClass'

    protected Rule createRule() {
        new IllegalClassReferenceRule(classNames:'org.example.OtherClass,com.example.MyExampleClass, UnrelatedClass')
    }
}
