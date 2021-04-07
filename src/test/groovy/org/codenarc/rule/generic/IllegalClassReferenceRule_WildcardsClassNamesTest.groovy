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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for IllegalClassReferenceRule - checks for specifying values containing wildcards for the classNames field
 *
 * @see IllegalClassReferenceRule_SingleClassNameTest
 * @see IllegalClassReferenceRule_MultipleClassNamesTest
 *
 * @author Chris Mair
 */
class IllegalClassReferenceRule_WildcardsClassNamesTest extends AbstractRuleTestCase<IllegalClassReferenceRule> {

    // Just test proper handling of wildcards by this rule. Assume that the other IllegalClassReferenceRule_*Test
    // classes sufficiently test references across the possible language constructs.

    @Test
    void testWildcards_Violations() {
        final SOURCE = '''
            import com.other.Example
            class MyClass extends com.example.Example {
                void writeOther(com.other.Other other) { }
                def myDao = new org.stuff.CoolDao()
            }
        '''
        rule.classNames = 'com.*.Example, com.other.Oth?r,*Dao'
        assertViolations(SOURCE,
            [line:2, source:'import com.other.Example', message:'com.other.Example'],
            [line:3, source:'class MyClass extends com.example.Example {', message:'com.example.Example'],
            [line:4, source:'void writeOther(com.other.Other other) { }', message:'com.other.Other'],
            [line:5, source:'def myDao = new org.stuff.CoolDao', message:'org.stuff.CoolDao'])
    }

    @Test
    void testWildcards_NoViolations() {
        final SOURCE = '''
            import com.other.Example
            class MyClass extends com.example.Example {
                void writeOther(com.other.Other other) { }
            }
        '''
        rule.classNames = 'ignore.*.Example, com.ignore.Oth?r'
        assertNoViolations(SOURCE)
    }

    @Override
    protected IllegalClassReferenceRule createRule() {
        new IllegalClassReferenceRule()
    }
}
