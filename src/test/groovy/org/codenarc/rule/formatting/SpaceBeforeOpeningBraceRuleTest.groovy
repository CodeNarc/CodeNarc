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
 * Tests for SpaceBeforeOpeningBraceRule
 *
 * @author Chris Mair
  */
class SpaceBeforeOpeningBraceRuleTest extends AbstractRuleTestCase<SpaceBeforeOpeningBraceRule> {

    private static final String BLOCK_VIOLATION_MESSAGE = 'The opening brace for the block in class None is not preceded by a space or whitespace'

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
                    if (count > this."maxPriority${priority}Violations") { }
                    while (count > this."maxPriority${priority}Violations") { }
                }
                MyClass() {
                    this(classNames)
                }
            }
            interface MyInterface { }
            enum MyEnum { OK, BAD }
            trait MyTrait { }
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
    void testApplyTo_EnumDeclaration_Violation() {
        final SOURCE = '''
            enum MyEnum{ OK, BAD }
c        '''
        assertSingleViolation(SOURCE, 2, 'enum MyEnum{ OK, BAD }', 'The opening brace for enum MyEnum is not preceded')
    }

    @Test
    void testApplyTo_Method_Violations() {
        final SOURCE = '''
            def myMethod(){ }
            def otherMethod()
            { }     // opening brace on separate line; no violation

            int putBulkAccountInfo(List<Map> jsonObject){
                doStuff()
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'def myMethod(){ }', messageText:'The opening brace for the method'],
            [lineNumber:6, sourceLineText:'int putBulkAccountInfo(List<Map> jsonObject){', messageText:'The opening brace for the method'] )
    }

    @Test
    void testApplyTo_Constructor_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass(){ }
                MyClass(int num){
                    doStuff()
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'MyClass(){ }', messageText:'The opening brace for the constructor in class MyClass'],
            [lineNumber:4, sourceLineText:'MyClass(int num){', messageText:'The opening brace for the constructor in class MyClass'])
    }

    @Test
    void testApplyTo_If_Violations() {
        final SOURCE = '''
            if (ready){ }
            if (ready ||
                done){ }
            if (ready) println '{'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (ready){ }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done){ }', messageText:BLOCK_VIOLATION_MESSAGE] )
    }

    @Test
    void testApplyTo_Else_Violations() {
        final SOURCE = '''
            if (ready) { }
            else{}
            if (ready) {}
            else println '{'  // no block; ignore
        '''
        assertSingleViolation(SOURCE, 3, 'else{}', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_For_Violations() {
        final SOURCE = '''
            for (int i=0; i<10; i++){ }
            for (int i=0; i<10; i++) println '{'  // no block; ignore
            for (String name in names){ }
            for (String name: names){ }
            for (int i=0;
                i<10;
                i++){ }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'for (int i=0; i<10; i++){ }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'for (String name in names){ }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:5, sourceLineText:'for (String name: names){ }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:8, sourceLineText:'i++){ }', messageText:BLOCK_VIOLATION_MESSAGE]
        )
    }

    @Test
    void testApplyTo_While_Violations() {
        final SOURCE = '''
            while (ready){ }
            while (ready ||
                    done){ }
            while (ready) println '{'  // no block; ignore
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'while (ready){ }', messageText:BLOCK_VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'done){ }', messageText:BLOCK_VIOLATION_MESSAGE],
            )
    }

    @Test
    void testApplyTo_Try_Violations() {
        final SOURCE = '''
            try{ } finally { }
        '''
        assertSingleViolation(SOURCE, 2, 'try{ }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Catch_Violations() {
        final SOURCE = '''
            try { } catch(Exception e){ }
        '''
        assertSingleViolation(SOURCE, 2, 'catch(Exception e){ }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Finally_Violations() {
        final SOURCE = '''
            try { } finally{ }
        '''
        assertSingleViolation(SOURCE, 2, 'finally{ }', BLOCK_VIOLATION_MESSAGE)
    }

    @Test
    void testApplyTo_Switch_Violations() {
        final SOURCE = '''
            switch (var){ }
        '''
        assertSingleViolation(SOURCE, 2, 'switch (var){ }', 'The opening brace for the switch statement in class None')
    }

    @Test
    void testApplyTo_Closure_Violations() {
        final SOURCE = '''
            list.each{ name -> }
            shouldFail(Exception){ doStuff() }
            def c ={ println 123 }
            def m = [a:123, b:{ println 7 }]
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'list.each{ name -> }', messageText:'The opening brace for the closure in class None is not preceded'],
            [lineNumber:3, sourceLineText:'shouldFail(Exception){ doStuff() }', messageText:'The opening brace for the closure in class None is not preceded'],
            [lineNumber:4, sourceLineText:'def c ={ println 123 }', messageText:'The opening brace for the closure in class None is not preceded'],
            [lineNumber:5, sourceLineText:'def m = [a:123, b:{ println 7 }]', messageText:'The opening brace for the closure in class None is not preceded'])
    }

    @Test
    void testApplyTo_UnicodeCharacterLiteral_CausesIncorrectColumnIndexesInAST_NoViolations_KnownIssue() {
        final SOURCE = '''
            if (valid('\\u00A0')){ }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (valid(', messageText:BLOCK_VIOLATION_MESSAGE])
    }

    @Test
    void testApplyTo_CheckClosureMapEntryValue_False_NoViolations() {
        final SOURCE = '''
            def m = [a:123, b:{ println 7 }]
        '''
        rule.checkClosureMapEntryValue = false
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
    void testApplyTo_CheckClosureAsFirstMethodParameter_NoViolations() {
        final SOURCE = '''
            execute({ println 7 }, true)
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

    @Override
    protected SpaceBeforeOpeningBraceRule createRule() {
        new SpaceBeforeOpeningBraceRule()
    }
}
