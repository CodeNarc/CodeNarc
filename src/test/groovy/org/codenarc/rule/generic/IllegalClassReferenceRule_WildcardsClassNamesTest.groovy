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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for IllegalClassReferenceRule - checks for specifying values containing wildcards for the classNames field
 *
 * @see IllegalClassReferenceRule_SingleClassNameTest
 * @see IllegalClassReferenceRule_MultipleClassNamesTest
 *
 * @author Chris Mair
 */
class IllegalClassReferenceRule_WildcardsClassNamesTest extends AbstractRuleTestCase {

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
            [lineNumber:2, sourceLineText:'import com.other.Example', messageText:'com.other.Example'],
            [lineNumber:3, sourceLineText:'class MyClass extends com.example.Example {', messageText:'com.example.Example'],
            [lineNumber:4, sourceLineText:'void writeOther(com.other.Other other) { }', messageText:'com.other.Other'],
            [lineNumber:5, sourceLineText:'def myDao = new org.stuff.CoolDao', messageText:'org.stuff.CoolDao'])
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

    protected Rule createRule() {
        new IllegalClassReferenceRule()
    }
}
