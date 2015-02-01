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
 * Tests for SpaceBeforeClosingBraceRule
 *
 * @author Chris Mair
  */
class SpaceBeforeClosingBraceRuleTest extends AbstractRuleTestCase {

    private static final String BLOCK_VIOLATION_MESSAGE = 'The closing brace for the block in class None is not preceded by a space or whitespace'
    private static final String CLOSURE_VIOLATION_MESSAGE = 'The closing brace for the closure in class None is not preceded by a space or whitespace'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceBeforeClosingBrace'
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
                static void reset() { violationCounts = [1:0, 2:0, 3:0] }
                void doStuff() { println 9 }
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
        assertSingleViolation(SOURCE, 4, 'def closure = {}', 'The closing brace for the closure in class MyClass is not preceded by a space or whitespace')
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
            class MyClass { int count}
            class MyOtherClass extends AbstractClass { int count}
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyClass { int count}', messageText:'The closing brace for class MyClass is not preceded'],
            [lineNumber:3, sourceLineText:'class MyOtherClass extends AbstractClass { int count}', messageText:'The closing brace for class MyOtherClass is not preceded'])
    }

    @Test
    void testApplyTo_InterfaceDeclaration_Violation() {
        final SOURCE = '''
            interface MyInterface { void doStuff()}
        '''
        assertSingleViolation(SOURCE, 2, 'interface MyInterface { void doStuff()}', 'The closing brace for interface MyInterface is not preceded')
    }

    @Test
    void testApplyTo_EnumDeclaration_Violation() {
        final SOURCE = '''
            enum MyEnum { OK, BAD}
c        '''
        assertSingleViolation(SOURCE, 2, 'enum MyEnum { OK, BAD}', 'The closing brace for enum MyEnum is not preceded')
    }

    @Test
    void testApplyTo_Method_Violations() {
        final SOURCE = '''
            def myMethod() { return 9}
            def otherMethod()
            { return 9}
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'def myMethod() { return 9}', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'{ return 9}', messageText:BLOCK_VIOLATION_MESSAGE] )
    }

    @Test
    void testApplyTo_Constructor_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass() { int count}
                MyClass(int num) {
                    doStuff()}
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'MyClass() { int count}', messageText:'The closing brace for the block in class MyClass'],
            [lineNumber:5, sourceLineText:'doStuff()}', messageText:'The closing brace for the block in class MyClass'])
    }

    @Test
    void testApplyTo_If_Violations() {
        final SOURCE = '''
            if (ready) { return 9}
            if (ready ||
                done) { return 9}
            if (ready) println '}'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (ready) { return 9}', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done) { return 9}', messageText:BLOCK_VIOLATION_MESSAGE] )
    }

    @Test
    void testApplyTo_Else_Violations() {
        final SOURCE = '''
            if (ready) { }
            else { return 9}
            if (ready) { }
            else println '{'  // no block; ignore
        '''
        assertSingleViolation(SOURCE, 3, 'else { return 9}', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_For_Violations() {
        final SOURCE = '''
            for (int i=0; i<10; i++) { println i}
            for (int i=0; i<10; i++) println '{'  // no block; ignore
            for (String name in names) { println name}
            for (String name: names) { println name}
            for (int i=0;
                i<10;
                i++) { println i}
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'for (int i=0; i<10; i++) { println i}', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'for (String name in names) { println name}', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:5, sourceLineText:'for (String name: names) { println name}', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:8, sourceLineText:'i++) { println i}', messageText:BLOCK_VIOLATION_MESSAGE]
        )
    }

    @Test
    void testApplyTo_While_Violations() {
        final SOURCE = '''
            while (ready) { println 9}
            while (ready ||
                    done) { println 9}
            while (ready) println '{'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'while (ready) { println 9}', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done) { println 9}', messageText:BLOCK_VIOLATION_MESSAGE] )
    }

    @Test
    void testApplyTo_Try_Violations() {
        final SOURCE = '''
            try { doStuff()} finally { }
        '''
        assertSingleViolation(SOURCE, 2, 'try { doStuff()}', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Catch_Violations() {
        final SOURCE = '''
            try { } catch(Exception e) { doStuff()}
        '''
        assertSingleViolation(SOURCE, 2, 'catch(Exception e) { doStuff()}', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Finally_Violations() {
        final SOURCE = '''
            try { } finally { doStuff()}
        '''
        assertSingleViolation(SOURCE, 2, 'finally { doStuff()}', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Closure_Violations() {
        final SOURCE = '''
            list.each { name -> doStuff()}
            shouldFail(Exception) { doStuff()}
            def c = { println 123}
            def m = [a:123, b: { println 7}]
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'list.each { name -> doStuff()}', messageText:CLOSURE_VIOLATION_MESSAGE],
            [lineNumber:3, sourceLineText:'shouldFail(Exception) { doStuff()}', messageText:CLOSURE_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'def c = { println 123}', messageText:CLOSURE_VIOLATION_MESSAGE],
            [lineNumber:5, sourceLineText:'def m = [a:123, b: { println 7}]', messageText:CLOSURE_VIOLATION_MESSAGE])
    }

    @Test
    void testApplyTo_Closure_UnicodeCharacterLiteral_Violation() {
        final SOURCE = '''
            list.each { name -> doStuff('\\u00A0')}
        '''
        assertSingleViolation(SOURCE, 2, 'list.each { name -> ', CLOSURE_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_CheckClosureMapEntryValue_False_NoViolations() {
        final SOURCE = '''
            def m = [a:123, b: { println 7}]
        '''
        rule.checkClosureMapEntryValue = false
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SpaceBeforeClosingBraceRule()
    }
}
