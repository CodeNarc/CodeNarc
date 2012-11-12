/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SpaceBeforeOpeningBraceRule
 *
 * @author Chris Mair
  */
class SpaceBeforeOpeningBraceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceBeforeOpeningBrace'
    }

    @Test
    void testApplyTo_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def closure = { }
                    if (true) { }
                    while(ready) { }
                    try {
                    } catch(Exception e) {
                    } finally { }
                    for(int i=0; i<10; i++) { }
                    for(String name in names) { }
                    for(String name: names) { }
                }
            }
            interface MyInterface { }
            enum MyEnum { OK, BAD }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClassDeclaration_Violation() {
        final SOURCE = '''
            class MyClass{ }
            class MyOtherClass extends AbstractClass{ }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyClass{', messageText:'The opening brace for class MyClass is not preceded'],
            [lineNumber:3, sourceLineText:'class MyOtherClass extends AbstractClass{ }', messageText:'The opening brace for class MyOtherClass is not preceded'])
    }

    @Test
    void testApplyTo_InterfaceDeclaration_Violation() {
        final SOURCE = '''
            interface MyInterface{ }
        '''
        assertSingleViolation(SOURCE, 2, 'interface MyInterface{ }', 'The opening brace for interface MyInterface is not preceded')
    }

    @Test
    void testApplyTo_EnumDeclaration_KnownLimitation_NoViolations() {
        final SOURCE = '''
            enum MyEnum{ OK, BAD }
c        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Method_Violations() {
        final SOURCE = '''
            def myMethod(){ }
            def otherMethod()
            { }     // opening brace on separate line; no violation
        '''
        assertSingleViolation(SOURCE, 2, 'def myMethod(){ }', 'The opening brace for the method myMethod in class None is not preceded')
    }

    @Test
    void testApplyTo_If_Violations() {
        final SOURCE = '''
            if (ready){ }
            if (ready) println '{'  // no block; ignore
        '''
        assertSingleViolation(SOURCE, 2, 'if (ready){ }', 'The opening brace for the if block in class None is not preceded')
    }

    @Test
    void testApplyTo_Else_Violations() {
        final SOURCE = '''
            if (ready) { }
            else{}
            if (ready) {}
            else println '{'  // no block; ignore
        '''
        assertSingleViolation(SOURCE, 3, 'else{}', 'The opening brace for the else block in class None is not preceded')
    }

    @Test
    void testApplyTo_For_Violations() {
        final SOURCE = '''
            for (int i=0; i<10; i++){ }
            for (int i=0; i<10; i++) println '{'  // no block; ignore
            for (String name in names){ }
            for (String name: names){ }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'for (int i=0; i<10; i++){ }', messageText:'The opening brace for the for block in class None is not preceded'],
            [lineNumber:4, sourceLineText:'for (String name in names){ }', messageText:'The opening brace for the for block in class None is not preceded'],
            [lineNumber:5, sourceLineText:'for (String name: names){ }', messageText:'The opening brace for the for block in class None is not preceded']
            )
    }

    @Test
    void testApplyTo_While_Violations() {
        final SOURCE = '''
            while (ready){ }
            while (ready) println '{'  // no block; ignore
        '''
        assertSingleViolation(SOURCE, 2, 'while (ready){ }', 'The opening brace for the while block in class None is not preceded')
    }

    @Test
    void testApplyTo_Try_Violations() {
        final SOURCE = '''
            try{ } finally { }
        '''
        assertSingleViolation(SOURCE, 2, 'try{ }', 'The opening brace for the try block in class None is not preceded')
    }

    @Test
    void testApplyTo_Catch_Violations() {
        final SOURCE = '''
            try { } catch(Exception e){ }
        '''
        assertSingleViolation(SOURCE, 2, 'catch(Exception e){ }', 'The opening brace for the catch block in class None is not preceded')
    }

    @Test
    void testApplyTo_Finally_Violations() {
        final SOURCE = '''
            try { } finally{ }
        '''
        assertSingleViolation(SOURCE, 2, 'finally{ }', 'The opening brace for the finally block in class None is not preceded')
    }

    protected Rule createRule() {
        new SpaceBeforeOpeningBraceRule()
    }
}