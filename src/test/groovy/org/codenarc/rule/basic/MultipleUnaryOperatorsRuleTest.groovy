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
package org.codenarc.rule.basic

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for MultipleUnaryOperatorsRule
 *
 * @author Chris Mair
 */
class MultipleUnaryOperatorsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'MultipleUnaryOperators'
    }

    @Test
    void testSingleUnaryOperators_NoViolations() {
        final SOURCE = '''
            int i = ~2
            int j = -1
            int z = +2
            boolean b = !true
            boolean c = !false

            int i = ~-2     // KNOWN LIMITATION; parses as BitwiseNegation+Constant
            int j = ~+9     // KNOWN LIMITATION; parses as BitwiseNegation+Constant
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultipleUnaryOperators_Violations() {
        final SOURCE = '''
            int z = ~~2
            boolean b = !!true
            boolean c = !!!false
            int j = -~7
            int k = +~8
            boolean d = !~1
        '''
        def message = 'The expression %s in class None contains confusing multiple consecutive unary operators'
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'int z = ~~2', messageText:String.format(message, '(~~2)')],
            [lineNumber:3, sourceLineText:'boolean b = !!true', messageText:String.format(message, '(!!true)')],
            [lineNumber:4, sourceLineText:'boolean c = !!!false', messageText:String.format(message, '(!!false)')],
            [lineNumber:4, sourceLineText:'boolean c = !!!false', messageText:String.format(message, '(!!false)')],
            [lineNumber:5, sourceLineText:'int j = -~7', messageText:String.format(message, '(-~7)')],
            [lineNumber:6, sourceLineText:'int k = +~8', messageText:String.format(message, '(+~8)')],
            [lineNumber:7, sourceLineText:'boolean d = !~1', messageText:String.format(message, '(!~1)')])
    }

    protected Rule createRule() {
        new MultipleUnaryOperatorsRule()
    }
}
