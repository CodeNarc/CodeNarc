/*
 * Copyright 2013 the original author or authors.
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

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ClosureStatementOnOpeningLineOfMultipleLineClosureRule
 *
 * @author Chris Mair
 */
class ClosureStatementOnOpeningLineOfMultipleLineClosureRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClosureStatementOnOpeningLineOfMultipleLineClosure'
    }

    @Test
    void testMultiLineClosure_NoViolations() {
        final SOURCE = '''
            def closure1 = { name ->
                println name
                addToCounts()
                println "done" }

            def closure2 = {
                println name
                addToCounts()
                println "done" }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleLineClosure_NoViolations() {
        final SOURCE = '''
            def closure = { name -> println name }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleLineClosure_WithMultipleStatements_NoViolations() {
        final SOURCE = '''
            def sourceAnalyzer = [analyze: { rs -> ruleSet = rs; RESULTS }, getSourceDirectories: { SOURCE_DIRS }] as SourceAnalyzer
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEmptyClosure_NoViolations() {
        final SOURCE = '''
            def closure1 = { -> }
            def closure2 = {  }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultiLineClosure_MultipleStatements_Violation() {
        final SOURCE = '''
            def closure = { name -> println name    # The multi-line closure within class None contains a statement on the opening line of the closure
                addToCounts()
                println “done” }
            '''
        assertInlineViolations(SOURCE)
    }

    @Test
    void testMultiLineClosure_SingleStatement_Violation() {
        final SOURCE = '''
            def closure = { name -> doStuff(name,    # The multi-line closure within class None contains a statement on the opening line of the closure
                97, 'sample text')
            }
            '''
        assertInlineViolations(SOURCE)
    }

    @Test
    void testEmptyClosure_NoViolation() {
        final SOURCE = '''
            doStuff { }
            assertCondition(TEXT, 'Info') {
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ClosureMapEntry_NoViolation() {
        final SOURCE = '''
            def m = [isOkay:{ return true }
            ]
            resource.authenticationDao = [
                getPlans:{ ssn, birthDate -> throw new NoMatchingDataException() },
                isHandler:{     return true}
            ] as AuthenticationDao
            '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ClosureStatementOnOpeningLineOfMultipleLineClosureRule()
    }
}
