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
    void testApplyTo_ClassDeclaration_Violation() {
        final SOURCE = '''
            class MyClass { int count }//comment
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyClass { int count }//comment', messageText:'The closing brace for class MyClass is not followed'])
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
c        '''
        assertSingleViolation(SOURCE, 2, 'enum MyEnum', 'The closing brace for enum MyEnum is not followed')
    }

    @Test
    void testApplyTo_Method_Violations() {
        final SOURCE = '''
            def myMethod() { return 9 }// ok
            def otherMethod()
            { return 9 }// ok
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'def myMethod() { return 9 }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'{ return 9 }', messageText:BLOCK_VIOLATION_MESSAGE] )
    }

    @Test
    void testApplyTo_Constructor_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass() { int count }//comment
                MyClass(int num) {
                    doStuff() }//comment
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'MyClass() { int count }', messageText:'The closing brace for the block in class MyClass'],
            [lineNumber:5, sourceLineText:'doStuff() }', messageText:'The closing brace for the block in class MyClass'])
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
            [lineNumber:2, sourceLineText:'if (ready) { return 9 }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done) { return 9 }', messageText:BLOCK_VIOLATION_MESSAGE] )
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
            [lineNumber:2, sourceLineText:'for (int i=0; i<10; i++) { println i }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'for (String name in names) { println name }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:5, sourceLineText:'for (String name: names) { println name }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:8, sourceLineText:'i++) { println i }', messageText:BLOCK_VIOLATION_MESSAGE]
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
            [lineNumber:2, sourceLineText:'while (ready) { println 9 }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done) { println 9 }', messageText:BLOCK_VIOLATION_MESSAGE] )
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
            [lineNumber:2, sourceLineText:'list.each { name -> doStuff() }', messageText:'The closing brace for the closure in class None is not followed'],
            [lineNumber:3, sourceLineText:'shouldFail(Exception) { doStuff() }', messageText:'The closing brace for the closure in class None is not followed'],
            [lineNumber:4, sourceLineText:'def c = { println 123 }', messageText:'The closing brace for the closure in class None is not followed'])
    }

    @Test
    void testApplyTo_Closure_AllowedCharacters_NoViolations() {
        final SOURCE = '''
            def matching = list.find { it.isReady() }.filter()  // no violation for dot operator
            assert list.every { it.isReady() }, "Error"         // no violation for comma
            def m = [a:123, b:{ println 7 },c:99]               // no violation for comma
            processItems(list.select { it.isReady() })          // no violation for closing parenthesis
            processItems([{ named("a") }, { named("b")}])       // no violation for closing square bracket
            def names = records.findAll { it.age > 1 }*.name    // no violation for spread operator
            parameters?.collect { it?.type?.toString() }?.join(', ')    // no violation for null-safe operator
            def closure = { println 7 };                       // no violation for comma
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
                def things = new ObjectMapper().readValue('{}', new TypeReference<List<Thing>>() {} )
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

    @Override
    protected SpaceAfterClosingBraceRule createRule() {
        new SpaceAfterClosingBraceRule()
    }
}
