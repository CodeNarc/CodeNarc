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
package org.codenarc.rule.design

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for LocaleSetDefaultRule
 *
 * @author mingzhi.huang, rob.patrick
 * @author Chris Mair
 */
class LocaleSetDefaultRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'LocaleSetDefault'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	Locale.getAvailableLocales()
        	Other.setDefault(Locale.US)
        	Other.setDefault(Locale.Category.DISPLAY, Locale.US)

        	println Locale.getDefault()
        	println Locale.default
        	println Other.default
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testLocaleSetDefault_Violations() {
        final SOURCE = '''
            java.util.Locale.setDefault(Locale.FRANCE)
            Locale.setDefault(Locale.UK)
            Locale.setDefault(Locale.Category.DISPLAY, Locale.JAPAN)
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'java.util.Locale.setDefault', messageText:'Avoid explicit calls to Locale.setDefault'],
            [lineNumber:3, sourceLineText:'Locale.setDefault', messageText:'Avoid explicit calls to Locale.setDefault'],
            [lineNumber:4, sourceLineText:'Locale.setDefault', messageText:'Avoid explicit calls to Locale.setDefault'])
    }

    @Test
    void testLocaleDefaultEquals_Violations() {
        final SOURCE = '''
            java.util.Locale.default  = Locale.FRANCE
            Locale.default = Locale.UK
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'java.util.Locale.default', messageText:'Avoid explicit assignment to Locale.default'],
            [lineNumber:3, sourceLineText:'Locale.default', messageText:'Avoid explicit assignment to Locale.default'])
    }

    protected Rule createRule() {
        new LocaleSetDefaultRule()
    }
}
