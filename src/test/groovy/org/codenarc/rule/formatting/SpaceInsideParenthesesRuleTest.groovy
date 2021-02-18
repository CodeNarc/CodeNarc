/*
 * Copyright 2021 the original author or authors.
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
 * Tests for SpaceInsideParenthesesRule
 *
 * @author Chris Mair
 */
@SuppressWarnings('SpaceInsideParentheses')
class SpaceInsideParenthesesRuleTest extends AbstractRuleTestCase<SpaceInsideParenthesesRule> {

    private static final String ERROR_MESSAGE_OPENING = 'The opening parenthesis is followed by whitespace'
    private static final String ERROR_MESSAGE_CLOSING = 'The closing parenthesis is preceded by whitespace'

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceInsideParentheses'
    }

    @Test
    void test_If_NoViolations() {
        final SOURCE = '''
            if(running) { check() }
            if(x < calculateLastIndex(
                    'name')) { check() }
            if(
                running
                ) { check() }
            if(     // Comment
                running) { check() }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_If_Violations() {
        final SOURCE = '''
            if( running) { }
            if(running ) { }
            if(      x < calculateLastIndex(
                    'name') + 1    ) { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if( running) { }', messageText: ERROR_MESSAGE_OPENING],
            [lineNumber:3, sourceLineText:'if(running ) { }', messageText: ERROR_MESSAGE_CLOSING],
            [lineNumber:4, sourceLineText:'if(      x < calculateLastIndex(', messageText: ERROR_MESSAGE_OPENING],
            [lineNumber:5, sourceLineText:"'name') + 1    ) { }", messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_While_NoViolations() {
        final SOURCE = '''
            while(running) { check() }
            while(x < calculateLastIndex(
                    'name')) { check() }
            while(
                running
                ) { check() }
            while(  // Comment
                running) { check() }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_While_Violations() {
        final SOURCE = '''
            while( running) { }
            while(running ) { }
            while(      x < calculateLastIndex(
                    'name') + 1    ) { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'while( running) { }', messageText: ERROR_MESSAGE_OPENING],
            [lineNumber:3, sourceLineText:'while(running ) { }', messageText: ERROR_MESSAGE_CLOSING],
            [lineNumber:4, sourceLineText:'while(      x < calculateLastIndex(', messageText: ERROR_MESSAGE_OPENING],
            [lineNumber:5, sourceLineText:"'name') + 1    ) { }", messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_For_NoViolations() {
        final SOURCE = '''
            for (name in names) { }
            for (int i=0; i < 10; i++) { }
            for(
                String name: names
                ) { }
            for(    // Comment
                String name: names) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_For_Violations() {
        final SOURCE = '''
            for ( name in names) { }
            for (int i=0; i < 10; i++     ) { }
            for(    String name: filterNames(
                names)   ) { }
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'for ( name in names) { }', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:3, sourceLineText:'for (int i=0; i < 10; i++     ) { }', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:4, sourceLineText:'for(    String name: filterNames(', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:5, sourceLineText:'names)   ) { }', messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_Expression_NoViolations() {
        final SOURCE = '''
            println (3 + (4 * 7)+7) + (5 * 1)
            def v =  (y - 7)*(x + (z - 3))
            def v2 =  (y - 7) *
                (
                    x +
                    (z - 3)
                )
            def v3 = calc(a) + calc(a + 1) + calc(a + (b - 1) * 2) + calc(7)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ParenthesesWithinStrings_NoViolations() {
        final SOURCE = '''
            def v1 = " ( ) ^ % # "
            def v2 = " ( " + " ) "
            println " ( ) ^ % # "
            doStuff(' ( ', ' ) ')
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Expression_Violations() {
        final SOURCE = '''
            println (3 + ( 4 * 7 )+7) + (     5 * 1 )
            def v =  (y - 7 )*( x + (z - 3))
            def v2 =  (y - 7 ) *
                (x +
                ( z - 3)
                )
            def v3 = calc(a) + calc(a + 1) + calc( a + ( b - 1) * 2) + calc(7)
            def v4 = (a ) ? (17) : (19 )
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'println (3 + ( 4 * 7 )+7) + (     5 * 1 )', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:2, sourceLineText:'println (3 + ( 4 * 7 )+7) + (     5 * 1 )', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:3, sourceLineText:'def v =  (y - 7 )*( x + (z - 3))', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:3, sourceLineText:'def v =  (y - 7 )*( x + (z - 3))', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:4, sourceLineText:'def v2 =  (y - 7 ) *', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:6, sourceLineText:'( z - 3)', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:8, sourceLineText:'def v3 = calc(a) + calc(a + 1) + calc( a + ( b - 1) * 2) + calc(7)', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:9, sourceLineText:'def v4 = (a ) ? (17) : (19 )', messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_MethodCalls_Violations() {
        final SOURCE = '''
            println( 123)
            println(123 )
            sum( 1,
                 2 )
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'println( 123)', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:3, sourceLineText:'println(123 )', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:4, sourceLineText:'sum( 1,', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:5, sourceLineText:'2 )', messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_MethodCalls_NoViolations() {
        final SOURCE = '''
            println(123)
            sum(1,
                 2)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MethodDeclaration_Violations() {
        final SOURCE = '''
            class MyClass {
                void doStuff( int n) { }
                int processStuff(String name ) { }
                void handle( int a,
                     int b ) { }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'void doStuff( int n) { }', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:4, sourceLineText:'int processStuff(String name ) { }', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:5, sourceLineText:'void handle( int a,', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:6, sourceLineText:'int b ) { }', messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_MethodDeclaration_NoViolations() {
        final SOURCE = '''
            class MyClass {
                void doStuff(int n) { }
                void handle(int a,
                     int b) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Comments_Violations() {
        final SOURCE = '''
            if( running) { }    // comment
            if(running ) { }    /* comment */
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'if( running) { }', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:3, sourceLineText:'if(running ) { }', messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_Comments_NoViolations() {
        final SOURCE = '''
            class MyClass {
                /* ( ) */
                /** (
                    sss ) **/
                //  (    )
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Catch_NoViolations() {
        final SOURCE = '''
            try { x= 1 } catch(Exception e) { }
            try {
                x= 1
            }
            catch (Exception e) {
                logger.error("Error", e)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Catch_Violations() {
        final SOURCE = '''
            try { x= 1 } catch( Exception e ) { }
            try {
                x= 1
            }
            catch (Exception e ) {
                logger.error("Error", e)
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'try { x= 1 } catch( Exception e ) { }', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:2, sourceLineText:'try { x= 1 } catch( Exception e ) { }', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:6, sourceLineText:'catch (Exception e ) {', messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_TernaryExpression_NoViolations() {
        final SOURCE = '''
            def x1 = (1 + 2 > 7) ? null : 7
            def x2 = isValid ? 23 : 11
            def x3 = (1
                 + 2
                 > 7) ? null : 7
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_TernaryExpression_Violations() {
        final SOURCE = '''
            def y1 = ( 1 + 2 > 7 ) ? null : 7
            def y2 = (       1
                 + 2
                 > 7 ) ? null : 7
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'def y1 = ( 1 + 2 > 7 ) ? null : 7', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:2, sourceLineText:'def y1 = ( 1 + 2 > 7 ) ? null : 7', messageText: ERROR_MESSAGE_CLOSING],
                [lineNumber:3, sourceLineText:'def y2 = (       1', messageText: ERROR_MESSAGE_OPENING],
                [lineNumber:5, sourceLineText:'> 7 ) ? null : 7', messageText: ERROR_MESSAGE_CLOSING])
    }

    @Test
    void test_GeneratedCode_Enum_NoViolations() {
        final SOURCE = '''
            enum Visibility {
                PUBLIC('public'), PROTECTED('protected'), PRIVATE('private')
                final String name
                private Visibility(String name) {
                    this.name = name
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpaceInsideParenthesesRule createRule() {
        new SpaceInsideParenthesesRule()
    }
}
