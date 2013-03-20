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
package org.codenarc.rule.grails

import org.codenarc.rule.Rule
import org.junit.Before
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for GrailsDomainReservedSqlKeywordNameRule
 *
 * @author Artur Gajowy
 */
class GrailsDomainReservedSqlKeywordNameRuleTest extends AbstractRuleTestCase {
    
    @Before
    void setup() {
        sourceCodePath = 'project/MyProject/grails-app/domain/com/xxx/Whatever.groovy'
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsDomainReservedSqlKeywordName'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	class Whatever {
        	}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class Order {
            }
        '''
        assertViolations(SOURCE,
            violation(2, 'class Order', "'Order' is a reserved SQL keyword and - as such - a problematic domain class name."))
    }

    @Test
    void testTwoViolations() {
        final SOURCE = '''
            class Whatever {
                String where
                Integer rows
            }
        '''
        assertViolations(SOURCE,
            violation(3, 'String where', violationMessage('where')),
            violation(4, 'Integer rows', violationMessage('rows')))
    }

    @Test
    void testBasicTypesViolate() {
        final SOURCE = '''
            class Whatever {
                int column
                byte[] rows
                Byte[] table
                char[] user
                Character[] order
                Serializable group
            }
        '''
        assertViolations(SOURCE,
            violation(3, 'int column', violationMessage('column')),
            violation(4, 'byte[] rows', violationMessage('rows')),
            violation(5, 'Byte[] table', violationMessage('table')),
            violation(6, 'char[] user', violationMessage('user')),
            violation(7, 'Character[] order', violationMessage('order')),
            violation(8, 'Serializable group', violationMessage('group')))
    }

    @Test
    void testRelationshipsDoNotViolate() {
        final SOURCE = '''
            class Whatever {
                Place where
                List<Row> rows
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTransientsDoNotViolate() {
        final SOURCE = '''
            class Whatever {
                String where
                static transients = ['where']
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDefsDoNotViolate() {
        final SOURCE = '''
            class Whatever {
                def where
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticsDoNotViolate() {
        final SOURCE = '''
            class Whatever {
                static String where
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSqlKeywordsOverride() {
        rule.additionalReservedSqlKeywords = 'customKeyword, evenMoreCustomKeyword'
        final SOURCE = '''
            class Whatever {
                String where
                String customKeyword
                String evenMoreCustomKeyword
            }
        '''
        assertViolations(SOURCE,
            violation(3, 'String where', violationMessage('where')),
            violation(4, 'String customKeyword', violationMessage('customKeyword')),
            violation(5, 'String evenMoreCustomKeyword', violationMessage('evenMoreCustomKeyword')))
    }

    @Test
    void testHibernateBasicTypesOverride() {
        rule.additionalHibernateBasicTypes = 'Place, Relation'
        final SOURCE = '''
            class Whatever {
                int rows
                Place where
                Relation order
            }
        '''
        assertViolations(SOURCE,
            violation(3, 'int rows', violationMessage('rows')),
            violation(4, 'Place where', violationMessage('where')),
            violation(5, 'Relation order', violationMessage('order')))
    }

    @Test
    void testNonDomain_WithKeywordName_NoViolations() {
        final SOURCE = '''
            class Order {
            }
        '''
        sourceCodePath = 'project/MyProject/grails-app/service/Order.groovy'
        assertNoViolations(SOURCE)
    }

    private Map violation(int lineNumber, String sourceLineText, String messageText) {
        [lineNumber: lineNumber, sourceLineText: sourceLineText, messageText: messageText]
    }

    private String violationMessage(String fieldName) {
        "'$fieldName' is a reserved SQL keyword and - as such - a problematic domain class' field name."
    }

    protected Rule createRule() {
        new GrailsDomainReservedSqlKeywordNameRule()
    }
}