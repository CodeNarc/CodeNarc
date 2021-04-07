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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UnnecessaryCastRule
 *
 * @author Chris Mair
 */
class UnnecessaryCastRuleTest extends AbstractRuleTestCase<UnnecessaryCastRule> {

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
            [line:2, source:'int count = (int)123', message:'The cast (int) 123 in class None is unnecessary'],
            [line:3, source:'def longValue = (long)123456L', message:'The cast (long) 123456 in class None is unnecessary'],
            [line:4, source:'def bigDecimal = (BigDecimal)1234.56', message:'The cast (BigDecimal) 1234.56 in class None is unnecessary'],
            [line:5, source:'String name = (String) "Joe"', message:'The cast (String) Joe in class None is unnecessary'],
            [line:6, source:'def list = (List)[1, 2, 3]', message:'The cast (List) [1, 2, 3] in class None is unnecessary'],
            [line:7, source:'def map = (Map)[a:1]', message:'The cast (Map) [a:1] in class None is unnecessary'],
            [line:8, source:'def theClass = ((BigDecimal)123.45).class', message:'The cast (BigDecimal) 123.45 in class None is unnecessary'],
        )
    }

    @Override
    protected UnnecessaryCastRule createRule() {
        new UnnecessaryCastRule()
    }
}
