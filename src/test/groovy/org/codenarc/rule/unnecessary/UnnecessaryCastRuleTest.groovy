/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for UnnecessaryCastRule
 *
 * @author Chris Mair
 */
class UnnecessaryCastRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnnecessaryCast'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	int count = (int) 123L
        	String id = (String) 123L
        	def theClass = ((BigDecimal)123L).class
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            int count = (int)123
            def longValue = (long)123456L
            def bigDecimal = (BigDecimal)1234.56
            String name = (String) "Joe"
            def list = (List)[1, 2, 3]
            def map = (Map)[a:1]
            def theClass = ((BigDecimal)123.45).class
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'int count = (int)123', messageText:'The cast (int) 123 in class None is unnecessary'],
            [lineNumber:3, sourceLineText:'def longValue = (long)123456L', messageText:'The cast (long) 123456 in class None is unnecessary'],
            [lineNumber:4, sourceLineText:'def bigDecimal = (BigDecimal)1234.56', messageText:'The cast (BigDecimal) 1234.56 in class None is unnecessary'],
            [lineNumber:5, sourceLineText:'String name = (String) "Joe"', messageText:'The cast (String) Joe in class None is unnecessary'],
            [lineNumber:6, sourceLineText:'def list = (List)[1, 2, 3]', messageText:'The cast (List) [1, 2, 3] in class None is unnecessary'],
            [lineNumber:7, sourceLineText:'def map = (Map)[a:1]', messageText:'The cast (Map) [a:1] in class None is unnecessary'],
            [lineNumber:8, sourceLineText:'def theClass = ((BigDecimal)123.45).class', messageText:'The cast (BigDecimal) 123.45 in class None is unnecessary'],
        )
    }

    protected Rule createRule() {
        new UnnecessaryCastRule()
    }
}
