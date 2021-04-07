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
 * Tests for SpaceAfterSemicolonRule
 *
 * @author Chris Mair
  */
class SpaceAfterSemicolonRuleTest extends AbstractRuleTestCase<SpaceAfterSemicolonRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterSemicolon'
    }

    // Tests for multiple statements per line

    @Test
    void testApplyTo_MultipleStatementsPerLine_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    println 1; println 2
                    int i=0;\ti++;    println i
                    def closure = { x -> println x; x = 23; }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MultipleStatementsPerLine_Violations() {
        final SOURCE = '''
            class MyTestCase {
                def myMethod() {
                    println 1;println 2
                    int i=0;i++;println i
                    def closure = { x -> println x;x = 23; }
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'println 1;println 2', message:'The statement "println 2"'],
            [line:5, source:'int i=0;i++;println i', message:'The statement "i++"'],
            [line:5, source:'int i=0;i++;println i', message:'The statement "println i"'],
            [line:6, source:'def closure = { x -> println x;x = 23; }', message:'The statement "x = 23"'])
    }

    // Tests for classic for statements

    @Test
    void testApplyTo_ForStatement_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    for(int i=0; i<10;\ti++) { }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NewForStatement_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    for (x in list) { }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ForStatement_Violations() {
        final SOURCE = '''
            class MyTestCase {
                def myMethod() {
                    for (int i=0;i<10;i++) {
                        for (int j=0; j < 10;j++) { }
                    }
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'for (int i=0;i<10;i++) {', message:'The for loop expression "i<10"'],
            [line:4, source:'for (int i=0;i<10;i++) {', message:'The for loop expression "i++"'] ,
            [line:5, source:'for (int j=0; j < 10;j++) { }', message:'The for loop expression "j++"'])
    }

    @Override
    protected SpaceAfterSemicolonRule createRule() {
        new SpaceAfterSemicolonRule()
    }
}
