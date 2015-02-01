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
 * Tests for SpaceAfterOpeningBraceRule
 *
 * @author Chris Mair
  */
class SpaceAfterOpeningBraceRuleTest extends AbstractRuleTestCase {

    private static final String BLOCK_VIOLATION_MESSAGE = 'The opening brace for the block in class None is not followed by a space or whitespace'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterOpeningBrace'
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
                    if (count > this."maxPriority${priority}Violations") { }
                    while (count > this."maxPriority${priority}Violations") { }
                }
                MyClass() {
                    this(classNames)
                }
            }
            interface MyInterface { }
            enum MyEnum { OK, BAD }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ProperSpacingWithoutIgnoreEmptyBlock_OneViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def closure = {}
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'def closure = {}', 'The opening brace for the closure in class MyClass is not followed by a space or whitespace')
    }

    @Test
    void testApplyTo_ProperSpacingWithIgnoreEmptyBlock_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def closure = {}
                    if (true) {}
                    while(ready) {}
                    try {
                    } catch(Exception e) {
                    } finally {}
                    for(int i=0; i<10; i++) {}
                    for(String name in names) {}
                    for(String name: names) {}
                    if (count > this."maxPriority${priority}Violations") {}
                    while (count > this."maxPriority${priority}Violations") {}
                }
                void doStuff2() {}
            }
            interface MyInterface2 {}
        '''
        rule.ignoreEmptyBlock = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClassDeclaration_Violation() {
        final SOURCE = '''
            class MyClass {int count }
            class MyOtherClass extends AbstractClass {int count }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyClass {int count }', messageText:'The opening brace for class MyClass is not followed'],
            [lineNumber:3, sourceLineText:'class MyOtherClass extends AbstractClass {int count }', messageText:'The opening brace for class MyOtherClass is not followed'])
    }

    @Test
    void testApplyTo_ClassDeclarationOnMultipleLines_Violation() {
        final SOURCE = '''
// starts at leftmost column
class MyTest
     extends AbstractTest implements ManualTest {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_InterfaceDeclaration_Violation() {
        final SOURCE = '''
            interface MyInterface {static final COUNT = 1 }
        '''
        assertSingleViolation(SOURCE, 2, 'interface MyInterface {static final COUNT = 1 }', 'The opening brace for interface MyInterface is not followed')
    }

    @Test
    void testApplyTo_EnumDeclaration_Violation() {
        final SOURCE = '''
            enum MyEnum {OK, BAD }
c        '''
        assertSingleViolation(SOURCE, 2, 'enum MyEnum {OK, BAD }', 'The opening brace for enum MyEnum')
    }

    @Test
    void testApplyTo_Method_Violations() {
        final SOURCE = '''
            def myMethod() {int count }
            def otherMethod()
                {int count }
            def bigMethod(
                String name) {println 9 }
            def myMethod2() {
                int count }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'def myMethod() {int count }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'{int count }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:6, sourceLineText:'{println 9 }', messageText:BLOCK_VIOLATION_MESSAGE])
    }

    @Test
    void testApplyTo_Constructor_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass() {int count }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'MyClass() {int count }', messageText:'The opening brace for the block in class MyClass'])
    }

    @Test
    void testApplyTo_If_Violations() {
        final SOURCE = '''
            if (ready) {println 9 }
            if (ready ||
                done) {println 9 }
            if (ready) println '{'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (ready) {println 9 }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done) {println 9 }', messageText:BLOCK_VIOLATION_MESSAGE] )
    }

    @Test
    void testApplyTo_Else_Violations() {
        final SOURCE = '''
            if (ready) { }
            else {println 9 }
            if (ready) { }
            else println '{'  // no block; ignore
        '''
        assertSingleViolation(SOURCE, 3, 'else {println 9 }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_For_Violations() {
        final SOURCE = '''
            for (int i=0; i<10; i++) {println i }
            for (int i=0; i<10; i++) println '{'  // no block; ignore
            for (String name in names) {println name }
            for (String name: names) {println name }
            for (int i=0;
                i<10;
                i++) {println name }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'for (int i=0; i<10; i++) {println i }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'for (String name in names) {println name }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:5, sourceLineText:'for (String name: names) {println name }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:8, sourceLineText:'i++) {println name }', messageText:BLOCK_VIOLATION_MESSAGE]
        )
    }

    @Test
    void testApplyTo_While_Violations() {
        final SOURCE = '''
            while (ready) {println name }
            while (ready ||
                    done) {println name }
            while (ready) println '{'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'while (ready) {println name }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done) {println name }', messageText:BLOCK_VIOLATION_MESSAGE],
            )
    }

    @Test
    void testApplyTo_Try_Violations() {
        final SOURCE = '''
            try {doStuff() } finally { }
        '''
        assertSingleViolation(SOURCE, 2, 'try {doStuff() }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Catch_Violations() {
        final SOURCE = '''
            try { } catch(Exception e) {doStuff() }
        '''
        assertSingleViolation(SOURCE, 2, 'catch(Exception e) {doStuff() }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Finally_Violations() {
        final SOURCE = '''
            try { } finally {doStuff() }
        '''
        assertSingleViolation(SOURCE, 2, 'finally {doStuff() }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Closure_Violations() {
        final SOURCE = '''
            list.each {name -> }
            shouldFail(Exception) {doStuff() }
            def c = {println 123 }
            def m = [a:123, b: {println 7 }]
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'list.each {name -> }', messageText:'The opening brace for the closure in class None'],
            [lineNumber:3, sourceLineText:'shouldFail(Exception) {doStuff() }', messageText:'The opening brace for the closure in class None'],
            [lineNumber:4, sourceLineText:'def c = {println 123 }', messageText:'The opening brace for the closure in class None'],
            [lineNumber:5, sourceLineText:'def m = [a:123, b: {println 7 }]', messageText:'The opening brace for the closure in class'])
    }

    @Test
    void testApplyTo_UnicodeCharacterLiteral_Violation() {
        final SOURCE = '''
            if (valid('\\u00A0')) {println 9 }
        '''
        assertSingleViolation(SOURCE, 2, 'if (valid', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_CheckClosureMapEntryValue_False_NoViolations() {
        final SOURCE = '''
            def m = [a:123, b: {println 7 }]
        '''
        rule.checkClosureMapEntryValue = false
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SpaceAfterOpeningBraceRule()
    }
}
