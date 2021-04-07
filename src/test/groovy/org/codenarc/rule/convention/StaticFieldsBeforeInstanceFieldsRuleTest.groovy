/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for StaticFieldsBeforeInstanceFieldsRule
 *
 * @author Chris Mair
 */
class StaticFieldsBeforeInstanceFieldsRuleTest extends AbstractRuleTestCase<StaticFieldsBeforeInstanceFieldsRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'StaticFieldsBeforeInstanceFields'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass {
                public static final int COUNT = 99
                static final String NAME = "ABC"
                static date = new Date()

                public String f1
                protected String f2
                private int f3
                int f4
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            class MyClass {
                public static final int COUNT = 99
                static final String NAME = "ABC"
                static date = new Date()

                public String f1

                public static final String F1 = "xxx"
                protected static final String F2 = "ABC"
                private static final String F3 = "ABC"
                private static String F4
                static F5 = new Date()

                protected String f2
                private int f3
                int f4
            }
        '''
        assertViolations(SOURCE,
            [line:9, source:'public static final String F1 = "xxx"', message:'static field F1'],
            [line:10, source:'protected static final String F2 = "ABC"', message:'static field F2'],
            [line:11, source:'private static final String F3 = "ABC"', message:'static field F3'],
            [line:12, source:'private static String F4', message:'static field F4'],
            [line:13, source:'static F5 = new Date()', message:'static field F5'])
    }

    @Override
    protected StaticFieldsBeforeInstanceFieldsRule createRule() {
        new StaticFieldsBeforeInstanceFieldsRule()
    }

}
