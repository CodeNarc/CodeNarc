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
        assertNoViolations('''
        	class Whatever {
        	}
        ''')
    }

    @Test
    void testSingleViolation() {
        assertInlineViolations('''
            class Order {           #'Order' is a reserved SQL keyword and - as such - a problematic domain class name.
            }
        ''')
    }

    @Test
    void testTwoViolations() {
        assertInlineViolations("""
            class Whatever {
                String where        ${violation('where')}
                Integer rows        ${violation('rows')}
            }
        """)
    }

    @Test
    void testBasicTypesViolate() {
        assertInlineViolations("""
            class Whatever {
                int column              ${violation('column')}
                byte[] rows             ${violation('rows')}
                Byte[] table            ${violation('table')}
                char[] user             ${violation('user')}
                Character[] order       ${violation('order')}
                Serializable group      ${violation('group')}
            }
        """)
    }

    @Test
    void testRelationshipsDoNotViolate() {
        assertNoViolations('''
            class Whatever {
                Place where
                List<Row> rows
            }
        ''')
    }

    @Test
    void testTransientsDoNotViolate() {
        assertNoViolations('''
            class Whatever {
                String where
                static transients = ['where']
            }
        ''')
    }

    @Test
    void testDefsDoNotViolate() {
        assertNoViolations('''
            class Whatever {
                def where
            }
        ''')
    }

    @Test
    void testStaticsDoNotViolate() {
        assertNoViolations('''
            class Whatever {
                static String where
            }
        ''')
    }

    @Test
    void testSqlKeywordsOverride() {
        rule.additionalReservedSqlKeywords = 'customKeyword, evenMoreCustomKeyword'
        assertInlineViolations("""
            class Whatever {
                String where                    ${violation('where')} 
                String customKeyword            ${violation('customKeyword')}
                String evenMoreCustomKeyword    ${violation('evenMoreCustomKeyword')}
            }
        """)
    }

    @Test
    void testHibernateBasicTypesOverride() {
        rule.additionalHibernateBasicTypes = 'Place, Relation'
        assertInlineViolations("""
            class Whatever {
                int rows            ${violation('rows')}
                Place where         ${violation('where')}
                Relation order      ${violation('order')}
            }
        """)
    }

    @Test
    void testNonDomain_WithKeywordName_NoViolations() {
        sourceCodePath = 'project/MyProject/grails-app/service/Order.groovy'
        assertNoViolations('''
            class Order {
            }
        ''')
    }

    private violation(String fieldName) {
        inlineViolation("'$fieldName' is a reserved SQL keyword and - as such - a problematic domain class' field name.")
    }
    
    protected Rule createRule() {
        new GrailsDomainReservedSqlKeywordNameRule()
    }
}
