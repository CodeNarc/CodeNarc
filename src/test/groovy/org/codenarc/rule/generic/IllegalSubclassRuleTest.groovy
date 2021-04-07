/*
 * Copyright 2014 the original author or authors.
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
 * Tests for IllegalSubclassRule
 *
 * @author Chris Mair
 */
class IllegalSubclassRuleTest extends AbstractRuleTestCase<IllegalSubclassRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IllegalSubclass'
    }

    @Test
    void testNullSuperclassNames_RuleIsNotReady() {
        final SOURCE = 'class MyClass { }'
        assert !rule.ready
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoesNotMatchSuperclassNames_NoViolations() {
        final SOURCE = '''
            class MyClass { }
            class MyClass2 extends Object { }
            class MyClass3 extends MyClass { }
        '''
        rule.superclassNames = 'OtherClass,ClassAbc'
        assertNoViolations(SOURCE)
    }

    @Test
    void testMatchesSingleNames_Violations() {
        final SOURCE = '''
            class MyObject extends Object { }
            class MyException extends Exception { }
            class MyClass2 extends MyClass { }
        '''
        rule.superclassNames = 'MyClass,Object, Exception, Other'
        assertViolations(SOURCE,
            [line:2, source:'class MyObject extends Object { }', message:'The class MyObject extends from the illegal superclass Object'],
            [line:3, source:'class MyException extends Exception { }', message:'The class MyException extends from the illegal superclass Exception'],
            [line:4, source:'class MyClass2 extends MyClass { }', message:'The class MyClass2 extends from the illegal superclass MyClass'])
    }

    @Test
    void testMatchesFullyQualifiedNames_Violations() {
        final SOURCE = '''
            class MyObject extends java.lang.Object { }
            class MyException extends java.lang.Exception { }
            class MyClass2 extends org.example.MyClass { }
        '''
        rule.superclassNames = '*MyClass,java.lang.Object, java.*.Exception, Other'
        assertViolations(SOURCE,
            [line:2, source:'class MyObject extends java.lang.Object { }', message:'The class MyObject extends from the illegal superclass java.lang.Object'],
            [line:3, source:'class MyException extends java.lang.Exception { }', message:'The class MyException extends from the illegal superclass java.lang.Exception'],
            [line:4, source:'class MyClass2 extends org.example.MyClass { }', message:'The class MyClass2 extends from the illegal superclass org.example.MyClass'])
    }

    @Override
    protected IllegalSubclassRule createRule() {
        new IllegalSubclassRule()
    }
}
