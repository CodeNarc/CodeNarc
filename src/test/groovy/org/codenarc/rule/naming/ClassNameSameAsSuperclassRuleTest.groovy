/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ClassNameSameAsSuperclassRule
 *
 * @author Chris Mair
 */
class ClassNameSameAsSuperclassRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassNameSameAsSuperclass'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass { }
            class Integer { }
            class MyOtherClass extends SomeOtherClass { }

            def innerClass = new MyClass() {
                int getCount() { return 99 }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInterfaces_NoViolations() {
        final SOURCE = '''
            interface MyOtherInterface { }
            interface MyInterface extends other.MyOtherInterface { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassNames_SameAsImplicitSuperclass() {
        final SOURCE = 'class Object { }'
        assertViolations(SOURCE,
            [lineNumber:1, sourceLineText:'class Object', messageText:'Class Object has the same simple name as its superclass java.lang.Object'] )
    }

    @Test
    void testClassName_SameAsSuperclass() {
        final SOURCE = 'class MyClass extends other.MyClass { }'
        assertViolations(SOURCE,
            [lineNumber:1, sourceLineText:'class MyClass extends other.MyClass', messageText:'Class MyClass has the same simple name as its superclass other.MyClass'] )
    }

    @Test
    void testClassName_SameAsSuperclass_Package() {
        final SOURCE = '''
            package com.example
            class MyClass extends other.MyClass { }
            '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'class MyClass extends other.MyClass', messageText:'Class com.example.MyClass has the same simple name as its superclass other.MyClass'] )
    }

    protected Rule createRule() {
        new ClassNameSameAsSuperclassRule()
    }
}
