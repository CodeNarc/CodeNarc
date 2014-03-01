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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for LineLengthRule
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class LineLengthRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'LineLength'
        assert rule.ignoreImportStatements
        assert rule.ignorePackageStatements
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	class Person {
                def longMethodButNotQuiteLongEnough1234567890123456789012345() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
        	class Person {
                def longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456',
                'The line exceeds 120 characters. The line is 121 characters.')
    }

    @Test
    void testIgnoresImportStatements() {
        final SOURCE = '''
            import longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456423452435asdfasdfadsfasdfasdfasdfadfasdfasdfadfasdfasdfadsf
        	class Person {
                def longMethodButNotQuiteLongEnough1234567890123456789012345() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoesNotIgnoreImportStatementsWhenFlagDisabled() {
        rule.ignoreImportStatements = false

        final SOURCE = '''
            import longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456423452435asdfasdfadsfasdfasdfasdfadfasdfasdfadfasdfasdfadsf
        	class Person {
                def longMethodButNotQuiteLongEnough1234567890123456789012345() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'import longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456423452435asdfasdfadsfasdfasdfasdfadfasdfasdfadfasdfasdfadsf')
    }

    @Test
    void testIgnoresPackageStatements() {
        final SOURCE = '''
            package longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456423452435asdfasdfadsfasdfasdfasdfadfasdfasdfadfasdfasdfadsf
        	class Person {
                def longMethodButNotQuiteLongEnough1234567890123456789012345() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoesNotIgnorePackageStatementsWhenFlagDisabled() {
        rule.ignorePackageStatements = false

        final SOURCE = '''
            package longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456423452435asdfasdfadsfasdfasdfasdfadfasdfasdfadfasdfasdfadsf
        	class Person {
                def longMethodButNotQuiteLongEnough1234567890123456789012345() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'package longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456423452435asdfasdfadsfasdfasdfasdfadfasdfasdfadfasdfasdfadsf')
    }

    @Test
    void testComments() {
        final SOURCE = '''
        	class Person {
                // this is a really long comment 001234567890123456789012345678907890123456789012345678901234567890123456
            }
        '''
        assertSingleViolation(SOURCE, 3, '// this is a really long comment 001234567890123456789012345678907890123456789012345678901234567890123456')
    }

    protected Rule createRule() {
        new LineLengthRule()
    }
}
