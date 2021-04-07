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
import org.codenarc.util.GroovyVersion
import org.junit.Test

/**
 * Tests for SpaceAfterOpeningBraceRule
 *
 * @author Chris Mair
  */
class SpaceAfterOpeningBraceRuleTest extends AbstractRuleTestCase<SpaceAfterOpeningBraceRule> {

    private static final String BLOCK_VIOLATION_MESSAGE = 'The opening brace for the block in class None is not followed by a space or whitespace'
    private static final String CLOSURE_VIOLATION_MESSAGE = 'The opening brace for the closure'

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
                MyClass(String s) { }
                MyClass(@Annotation('${prop}') String s) {
                }
                MyClass(Date date) { // comment
                    this(classNames)
                }
                MyClass(Object object) { /* comment */ }
            }
            interface MyInterface { }
            enum MyEnum { OK, BAD }
            trait MyTrait { }
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
                MyClass() {}
                MyClass(@Annotation('${prop}') String s) {}
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
            [line:2, source:'class MyClass {int count }', message:'The opening brace for class MyClass is not followed'],
            [line:3, source:'class MyOtherClass extends AbstractClass {int count }', message:'The opening brace for class MyOtherClass is not followed'])
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
            [line:2, source:'def myMethod() {int count }', message:'The opening brace for the method myMethod in class None'],
            [line:4, source:'{int count }', message:'The opening brace for the method otherMethod in class None'],
            [line:6, source:'{println 9 }', message:'The opening brace for the method bigMethod in class None'])
    }

    @Test
    void testApplyTo_EmptyConstructorWithIgnoreEmptyBlock_NoViolations() {
        final SOURCE = '''
            class MyClass {
                MyClass() {}
            }
        '''

        rule.ignoreEmptyBlock = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Constructor_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass() {int count }
                MyClass() {s = '{"json": true}' }
                MyClass(@Annotation('${prop}') String s) {println 123 }

            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'MyClass() {int count }', message:'The opening brace for the method <init> in class MyClass'],
            [line:4, source:'MyClass() {s = \'{"json": true}\' }', message:'The opening brace for the method <init> in class MyClass'],
            [line:5, source:'MyClass(@Annotation(\'${prop}\') String s) {println 123 }', message:'The opening brace for the method <init> in class MyClass'])
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
            [line:2, source:'if (ready) {println 9 }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'done) {println 9 }', message:BLOCK_VIOLATION_MESSAGE])
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
            [line:2, source:'for (int i=0; i<10; i++) {println i }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'for (String name in names) {println name }', message:BLOCK_VIOLATION_MESSAGE],
            [line:5, source:'for (String name: names) {println name }', message:BLOCK_VIOLATION_MESSAGE],
            [line:8, source:'i++) {println name }', message:BLOCK_VIOLATION_MESSAGE]
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
            [line:2, source:'while (ready) {println name }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'done) {println name }', message:BLOCK_VIOLATION_MESSAGE],
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
            [line:2, source:'list.each {name -> }', message:'The opening brace for the closure in class None'],
            [line:3, source:'shouldFail(Exception) {doStuff() }', message:'The opening brace for the closure in class None'],
            [line:4, source:'def c = {println 123 }', message:'The opening brace for the closure in class None'],
            [line:5, source:'def m = [a:123, b: {println 7 }]', message:'The opening brace for the closure in class'])
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

    @Test
    void testApplyTo_OneLineConstructorBodyContainsBrace_NoViolations() {
        final SOURCE = '''
            class MyClass {
                String s
                MyClass() { s = '{"json": true}' }
                MyClass(@Annotation('${prop}') String s) { println 123 }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_OneLineMethodBodyContainsBrace_NoViolations() {
        final SOURCE = '''
            class MyClass {
                String s
                def doStuff() { s = '{"json": true}' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClosureWithLambdaSyntax_NoViolations() {
        final SOURCE = '''
            def myClosure = () -> println "bla"
            myClosure = () -> { println "bla" }
            myClosure = () -> { }
            myClosure = () -> {
              println "bla"
            }
            myClosure = (def param1) -> { println "bla" }
            myClosure = param1 -> { println "bla" }
            myClosure = param1 -> println "bla"
            myClosure = (def param1, def param2) -> { println "bla" }
        '''

        if (GroovyVersion.isNotGroovyVersion2()) {
            assertNoViolations(SOURCE)
        }
    }

    @Test
    void testApplyTo_ClosureWithLambdaSyntax_Violations() {
        final SOURCE = '''
            myClosure = () -> {println "aaa" }
            myClosure = (def param1) -> {println "bbb" }
            myClosure = param1 -> {println "ccc" }
        '''

        if (GroovyVersion.isNotGroovyVersion2()) {
            assertViolations(SOURCE,
                    [line:2, source:'{println "aaa" }', message:CLOSURE_VIOLATION_MESSAGE],
                    [line:3, source:'{println "bbb" }', message:CLOSURE_VIOLATION_MESSAGE],
                    [line:4, source:'{println "ccc" }', message:CLOSURE_VIOLATION_MESSAGE])
        }
    }

    @Override
    protected SpaceAfterOpeningBraceRule createRule() {
        new SpaceAfterOpeningBraceRule()
    }
}
