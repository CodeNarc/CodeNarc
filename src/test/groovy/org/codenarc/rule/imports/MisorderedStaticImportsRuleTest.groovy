/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.imports

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for MisorderedStaticImportsRule
 *
 * @author Erik Pragt
 * @author Marcin Erdmann
  */
class MisorderedStaticImportsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MisorderedStaticImports'
    }

    @Test
    void testSuccessScenarioWithNonStaticImports() {
        final SOURCE = '''
        	import foo.bar.*
            import my.something.*
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioWithStaticImportsOnly() {
        final SOURCE = '''
        	import static foo.bar.*
            import static my.something.*
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioWithCorrectSequence() {
        final SOURCE = '''
        	import static foo.bar.*
            import my.something.*
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenarioWithCorrectSequence_After() {
        final SOURCE = '''
            import my.something.*
        	import static foo.bar.*
        '''

        rule.comesBefore = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolationWithIncorrectSequence() {
        final SOURCE = '''
            import my.something.*
        	import static foo.bar.*
        '''
        assertSingleViolation(SOURCE, 3, 'import static foo.bar.*', 'Static imports should appear before normal imports')
    }

    @Test
    void testSingleViolationWithIncorrect_After() {
        final SOURCE = '''
        	import static foo.bar.*
            import my.something.*
        '''
        rule.comesBefore = false
        assertSingleViolation(SOURCE, 3, 'import my.something.*', 'Normal imports should appear before static imports')
    }

    @Test
    void testTwoViolations() {
        final SOURCE = '''
            import my.something.*
        	import static foo.bar.*
            import my.otherthing.*
        	import static bar.foo.*
        '''
        assertTwoViolations(SOURCE,
                3, 'import static foo.bar.*',
                5, 'import static bar.foo.*')
    }

    @Test
    void testOneViolationStartingWithStaticImport() {
        final SOURCE = '''
        	import static foo.bar.*
            import my.something.*
        	import static bar.foo.*
            import my.otherthing.*
        '''
        assertSingleViolation(SOURCE,
                4, 'import static bar.foo.*')
    }

    protected Rule createRule() {
        new MisorderedStaticImportsRule()
    }
}
