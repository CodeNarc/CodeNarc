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
            [line:2, source:'class MyClass{', message:'The opening brace for class MyClass is not preceded'],
            [line:3, source:'class MyOtherClass extends AbstractClass{ }', message:'The opening brace for class MyOtherClass is not preceded'])
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
            [line:2, source:'def myMethod(){ }', message:'The opening brace for the method'],
            [line:6, source:'int putBulkAccountInfo(List<Map> jsonObject){', message:'The opening brace for the method'])
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
            [line:3, source:'MyClass(){ }', message:'The opening brace for the constructor in class MyClass'],
            [line:4, source:'MyClass(int num){', message:'The opening brace for the constructor in class MyClass'])
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
            [line:2, source:'if (ready){ }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'done){ }', message:BLOCK_VIOLATION_MESSAGE])
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
            [line:2, source:'for (int i=0; i<10; i++){ }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'for (String name in names){ }', message:BLOCK_VIOLATION_MESSAGE],
            [line:5, source:'for (String name: names){ }', message:BLOCK_VIOLATION_MESSAGE],
            [line:8, source:'i++){ }', message:BLOCK_VIOLATION_MESSAGE]
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
            [line:2, source:'while (ready){ }', message:BLOCK_VIOLATION_MESSAGE],
            [line:4, source:'done){ }', message:BLOCK_VIOLATION_MESSAGE],
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
            [line:2, source:'list.each{ name -> }', message:'The opening brace for the closure in class None is not preceded'],
            [line:3, source:'shouldFail(Exception){ doStuff() }', message:'The opening brace for the closure in class None is not preceded'],
            [line:4, source:'def c ={ println 123 }', message:'The opening brace for the closure in class None is not preceded'],
            [line:5, source:'def m = [a:123, b:{ println 7 }]', message:'The opening brace for the closure in class None is not preceded'])
    }

    @Test
    void testApplyTo_UnicodeCharacterLiteral_CausesIncorrectColumnIndexesInAST_NoViolations_KnownIssue() {
        final SOURCE = '''
            if (valid('\\u00A0')){ }
        '''
        assertViolations(SOURCE,
            [line:2, source:'if (valid(', message:BLOCK_VIOLATION_MESSAGE])
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

    @Test
    void testApplyTo_ClosureOnSameLine_NoViolations() {
        final SOURCE = '''
            new LazyReferenceByFunction<Object, Object>({ null }) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpaceBeforeOpeningBraceRule createRule() {
        new SpaceBeforeOpeningBraceRule()
    }
}
