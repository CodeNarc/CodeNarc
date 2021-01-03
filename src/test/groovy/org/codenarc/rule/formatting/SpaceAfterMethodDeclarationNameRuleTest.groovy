/*
 * Copyright 2020 the original author or authors.
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
 * Tests for SpaceAfterMethodDeclarationNameRule
 */
class SpaceAfterMethodDeclarationNameRuleTest extends AbstractRuleTestCase<SpaceAfterMethodDeclarationNameRule> {

    @Test
    void ruleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterMethodDeclarationName'
    }

    @Test
    void noViolations() {
        assertNoViolations '''
            class Valid {
                Valid() {
                    super()
                }

                void valid() {
                }
            }
        '''
    }

    @Test
    void noViolationIsReportedWhenMethodNameContainsParanthesis() {
        assertNoViolations '''
            class Ignored {
                def "set NO_RESTRICTIONS on page (and descendants)"() {
                }
            }
        '''
    }

    @Test
    void trailingWhitespaceInMethodNameDeclarationCausesViolation() {
        final SOURCE = '''
            class Invalid {
                void invalid () {
                }
            }
        '''

        assertSingleViolation(SOURCE, 3, 'void invalid () {', 'There is trailing whitespace in method name declaration.')
    }

    @Test
    void trailingWhitespaceInConstructorNameDeclarationCausesViolation() {
        final SOURCE = '''
            class Invalid {
                Invalid () {
                }
            }
        '''

        assertSingleViolation(SOURCE, 3, 'Invalid () {', 'There is trailing whitespace in constructor name declaration.')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class Invalid {
                void firstInvalid () {
                }

                void secondInvalid (String argument) {
                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 3, sourceLineText: 'void firstInvalid () {', messageText: 'There is trailing whitespace in method name declaration.'],
            [lineNumber: 6, sourceLineText: 'void secondInvalid (String argument) {', messageText: 'There is trailing whitespace in method name declaration.']
        )
    }

    @Override
    protected SpaceAfterMethodDeclarationNameRule createRule() {
        new SpaceAfterMethodDeclarationNameRule()
    }
}
