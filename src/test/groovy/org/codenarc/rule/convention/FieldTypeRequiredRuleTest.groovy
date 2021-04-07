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
 * Tests for FieldTypeRequiredRule
 *
 * @author Chris Mair
 */
class FieldTypeRequiredRuleTest extends AbstractRuleTestCase<FieldTypeRequiredRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'FieldTypeRequired'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass {
                public static final String NAME = "joe"
                private static int count = 0

                private String name = NAME
                protected final Date date = new Date()

                String defaultName
                long maxSoFar
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_FieldTypesNotSpecified_Violations() {
        final SOURCE = '''
            class MyClass {
                public static final NAME = "joe"
                private static count = 0

                private def name, phobia                // multiple fields declared
                protected final date = new Date()

                def defaultName
                def maxSoFar = -1L
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'public static final NAME = "joe"', message:'The type is not specified for field "NAME"'],
            [line:4, source:'private static count = 0', message:'The type is not specified for field "count"'],
            [line:6, source:'private def name, phobia', message:'The type is not specified for field "name"'],
            [line:6, source:'private def name, phobia', message:'The type is not specified for field "phobia"'],
            [line:7, source:'protected final date = new Date()', message:'The type is not specified for field "date"'],
            [line:9, source:'def defaultName', message:'The type is not specified for field "defaultName"'],
            [line:10, source:'def maxSoFar = -1L', message:'The type is not specified for field "maxSoFar"'],
        )
    }

    @Test
    void test_ignoreFieldNames_MatchesNoName_Violations() {
        final SOURCE = '''
            class MyClass {
                public static final NAME = "joe"
                protected final date = new Date()
                def defaultName
            }
        '''
        rule.ignoreFieldNames = 'other'
        assertViolations(SOURCE,
                [line:3, source:'public static final NAME = "joe"', message:'The type is not specified for field "NAME"'],
                [line:4, source:'protected final date = new Date()', message:'The type is not specified for field "date"'],
                [line:5, source:'def defaultName', message:'The type is not specified for field "defaultName"'],
        )
    }

    @Test
    void test_ignoreFieldNames_MatchesNames() {
        final SOURCE = '''
            class MyClass {
                public static final NAME = "joe"
                protected final date = new Date()
                def defaultName
            }
        '''
        rule.ignoreFieldNames = 'other, def*Name, NAME, abc'
        assertViolations(SOURCE,
                [line:4, source:'protected final date = new Date()', message:'The type is not specified for field "date"'],
        )
    }

    @Override
    protected FieldTypeRequiredRule createRule() {
        new FieldTypeRequiredRule()
    }
}
