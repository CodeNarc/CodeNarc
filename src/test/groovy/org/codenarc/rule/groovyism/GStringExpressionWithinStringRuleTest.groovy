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
package org.codenarc.rule.groovyism

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for GStringExpressionWithinStringRule
 *
 * @author Chris Mair
 */
class GStringExpressionWithinStringRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GStringExpressionWithinString'
    }

    @Test
    void testSingleQuoteStrings_WithoutGStringExpressions_NoViolations() {
        final SOURCE = '''
        	def str1 = '123'
        	def str2 = 'abc def ghi'
        	def str3 = 'abc ${ ghu'
        	def str4 = 'abc $ghu    }'
        	def str5 = 'abc {123}'
        	def str6 = 'abc $}'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoubleQuoteStrings_NoViolations() {
        final SOURCE = '''
        	def str1 = "123"
        	def str2 = "abc def ghi"
        	def str3 = "abc ${count}"
        	def str4 = "abc $count    }"
        	def str5 = "abc {123}"
        	def str6 = "abc ${}"
        	def str7 = "total: ${count * 25}"
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleQuoteStrings_WithGStringExpression_Violations() {
        final SOURCE = '''
            def str1 = 'total: ${count}'
            def str2 = 'average: ${total / count}'
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:"def str1 = 'total: \${count}'", messageText:'\'${count}\''],
            [lineNumber:3, sourceLineText:"def str2 = 'average: \${total / count}'", messageText:'\'${total / count}\''])
    }

    protected Rule createRule() {
        new GStringExpressionWithinStringRule()
    }
}