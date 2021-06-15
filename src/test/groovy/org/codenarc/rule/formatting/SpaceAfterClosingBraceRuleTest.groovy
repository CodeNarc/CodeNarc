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
import org.junit.Test

/**
 * Tests for SpaceAfterClosingBraceRule
 *
 * @author Chris Mair
  */
class SpaceAfterClosingBraceRuleTest extends AbstractRuleTestCase<SpaceAfterClosingBraceRule> {

    private static final String BLOCK_VIOLATION_MESSAGE = 'The closing brace for the block in class None is not followed by a space or whitespace'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterClosingBrace'
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
                } // ok
                static void reset() { violationCounts = [1:0, 2:0, 3:0] }
                void doStuff() { println 9 }
            }
            interface MyInterface { }
            enum MyEnum { OK, BAD }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClassDeclaration_Violation() {
        final SOURCE = '''
            class MyClass { int count }//comment
        '''
        assertViolations(SOURCE,
            [line:2, source:'class MyClass { int count }//comment', message:'The closing brace for class MyClass is not followed'])
    }

    @Test
    void testApplyTo_InterfaceDeclaration_Violation() {
        final SOURCE = '''
            interface MyInterface { void doStuff() }//comment
        '''
        assertSingleViolation(SOURCE, 2, 'interface MyInterface { void doStuff() }//comment', 'The closing brace for interface MyInterface is not followed')
    }

    @Test
    void testApplyTo_EnumDeclaration_Violations() {
        final SOURCE = '''
            enum MyEnum { OK, BAD }//comment
        '''
        assertSingleViolation(SOURCE, 2, 'enum MyEnum', 'The closing brace for enum MyEnum is not followed')
    }

    @Test
    void testApplyTo_Method_Violations() {
        final SOURCE = '''
            def myMethod() { return 9 }// comment
            def otherMethod()
            { return 9 }// comment
            def myMethod2() { }// comment
            def m4() {
            }// comment
        '''
        assertViolations(SOURCE,
            [line:2, source:'def myMethod() { return 9 }', message:'The closing brace for the method myMethod in class None is not followed by a space or whitespace'],
            [line:4, source:'{ return 9 }', message:'The closing brace for the method otherMethod in class None is not followed by a space or whitespace'],
            [line:5, source:'def myMethod2() { }', message:'The closing brace for the method myMethod2 in class None is not followed by a space or whitespace'],
            [line:6, source:'def m4() {', message:'The closing brace for the method m4 in class None is not followed by a space or whitespace'])
    }

    @Test
    void testApplyTo_Constructor_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass() { int count }//comment
                MyClass(int num) {
                    doStuff() }//comment
                MyClass(String name) { }//comment
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'MyClass() { int count }', message:'The closing brace for the method <init> in class MyClass is not followed by a space or whitespace'],
            [line:4, source:'MyClass(int num)', message:'The closing brace for the method <init> in class MyClass is not followed by a space or whitespace'],
            [line:6, source:'MyClass(String name)', message:'The closing brace for the method <init> in class MyClass is not followed by a space or whitespace'])
    }

    @Test
    void testApplyTo_If_Violations() {
        final SOURCE = '''
            if (ready) { return 9 }//comment
            if (ready ||
                done) { return 9 }else { }
            if (ready) println '}'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [line:2, source:'if (ready) { return 9 }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'done) { return 9 }', message:BLOCK_VIOLATION_MESSAGE])
    }

    @Test
    void testApplyTo_Else_Violations() {
        final SOURCE = '''
            if (ready) { }
            else { return 9 }//comment
            if (ready) { }
            else println '{'  // no block; ignore
        '''
        assertSingleViolation(SOURCE, 3, 'else { return 9 }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_For_Violations() {
        final SOURCE = '''
            for (int i=0; i<10; i++) { println i }//comment
            for (int i=0; i<10; i++) println '{'  // no block; ignore
            for (String name in names) { println name }//comment
            for (String name: names) { println name }//comment
            for (int i=0;
                i<10;
                i++) { println i }//comment
        '''
        assertViolations(SOURCE,
            [line:2, source:'for (int i=0; i<10; i++) { println i }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'for (String name in names) { println name }', message:BLOCK_VIOLATION_MESSAGE],
            [line:5, source:'for (String name: names) { println name }', message:BLOCK_VIOLATION_MESSAGE],
            [line:8, source:'i++) { println i }', message:BLOCK_VIOLATION_MESSAGE]
        )
    }

    @Test
    void testApplyTo_While_Violations() {
        final SOURCE = '''
            while (ready) { println 9 }//comment
            while (ready ||
                    done) { println 9 }//comment
            while (ready) println '{'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [line:2, source:'while (ready) { println 9 }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'done) { println 9 }', message:BLOCK_VIOLATION_MESSAGE])
    }

    @Test
    void testApplyTo_Try_Violations() {
        final SOURCE = '''
            try { doStuff() }finally { }
        '''
        assertSingleViolation(SOURCE, 2, 'try { doStuff() }finally', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Catch_Violations() {
        final SOURCE = '''
            try {
            } catch(Exception e) {
                doStuff()
            }finally { }
        '''
        assertSingleViolation(SOURCE, 3, 'catch(Exception e) {', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Finally_Violations() {
        final SOURCE = '''
            try { } finally { doStuff() }//comment
        '''
        assertSingleViolation(SOURCE, 2, 'finally { doStuff() }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Closure_Violations() {
        final SOURCE = '''
            list.each { name -> doStuff() }//comment
            shouldFail(Exception) { doStuff() }//comment
            def c = { println 123 }//comment
            def m = [a:123, b:{ println 7 }]            //not a violation
            def m2 = [a:123, b: m.each{ println it }]   //not a violation
        '''
        assertViolations(SOURCE,
            [line:2, source:'list.each { name -> doStuff() }', message:'The closing brace for the closure in class None is not followed'],
            [line:3, source:'shouldFail(Exception) { doStuff() }', message:'The closing brace for the closure in class None is not followed'],
            [line:4, source:'def c = { println 123 }', message:'The closing brace for the closure in class None is not followed'])
    }

    @Test
    void testApplyTo_Closure_AllowedCharacters_NoViolations() {
        final SOURCE = '''
            def matching = list.find { it.isReady() }.filter()  // no violation for dot operator
            assert list.every { it.isReady() }, "Error"         // no violation for comma
            def m = [a:123, b:{ println 7 },c:99]               // no violation for comma
            processItems(list.select { it.isReady() })          // no violation for closing parenthesis
            maps.find { m -> m[index] }[index]                  // no violation for opening square bracket
            processItems([{ named("a") }, { named("b")}])       // no violation for closing square bracket
            def names = records.findAll { it.age > 1 }*.name    // no violation for spread operator
            parameters?.collect { it?.type?.toString() }?.join(', ')    // no violation for null-safe operator
            def closure = { println 7 };                        // no violation for comma
            writeLockLockInterceptor.tap { it.delegate = owner.delegate }()     // no violation for opening parenthesis
            switch(x) {
                case { x < 0 }:                                 // no violation for colon
                break
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_InnerClass_SemicolonFollowingClosingBrace_NoViolations() {
        final SOURCE = '''
            def service = new MyService() { };
            def service2 = new MyService() {
                @Override
                void run() throws Exception {
                     println 123
                }
            };
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_InnerClass_ParenthesisFollowingClosingBrace_NoViolations() {
        final SOURCE = '''
            foo.bar(new Whatever() {
                void doSomething() {/*...*/}
            })
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_UnicodeCharacterLiteral_Violation() {
        final SOURCE = '''
            if (valid()) { return '\\u00A0' }else { }
        '''
        assertSingleViolation(SOURCE, 2, 'if (valid())', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_ClosureMapValue_NoViolations() {
        final SOURCE = '''
            def m1 = [a:123, b:{ println 7 }]
            def m2 = [a: myList.collect { it.value }]
        '''
        rule.checkClosureMapEntryValue = true   // ignored
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClosureListValue_NoViolations() {
        final SOURCE = '''
            def list = [{ println 7 }, { println 3 }]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClosingBraceWithinStringLiteral_NoViolations() {
        final SOURCE = '''
            def doStuff() {
                def things = new ObjectMapper().readValue('{}', new TypeReference<List<Thing>>() {})
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_GStringWithClosure_NoViolations() {
        assertNoViolations('''
            def foo = 1
            "I am a ${ -> foo }"
        ''')
    }

    @Test
    void testApplyTo_GStringWithClosure_AnyCharacterAllowedAfterClosureInsideGString_NoViolations() {
        assertNoViolations('''
            def foo = 1
            "I am a ${ -> foo }0"
        ''')
    }

    @Test
    void testApplyTo_ClosingBraceWithinAnnotationDefaultDeclaration_NoViolations() {
        final SOURCE = '''
            @interface SomeConstraint {
              String message() default "{my.message}"
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpaceAfterClosingBraceRule createRule() {
        new SpaceAfterClosingBraceRule()
    }
}
