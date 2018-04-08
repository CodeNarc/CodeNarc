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
            [lineNumber:3, sourceLineText:'public static final NAME = "joe"', messageText:'The type is not specified for field "NAME"'],
            [lineNumber:4, sourceLineText:'private static count = 0', messageText:'The type is not specified for field "count"'],
            [lineNumber:6, sourceLineText:'private def name, phobia', messageText:'The type is not specified for field "name"'],
            [lineNumber:6, sourceLineText:'private def name, phobia', messageText:'The type is not specified for field "phobia"'],
            [lineNumber:7, sourceLineText:'protected final date = new Date()', messageText:'The type is not specified for field "date"'],
            [lineNumber:9, sourceLineText:'def defaultName', messageText:'The type is not specified for field "defaultName"'],
            [lineNumber:10, sourceLineText:'def maxSoFar = -1L', messageText:'The type is not specified for field "maxSoFar"'],
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
                [lineNumber:3, sourceLineText:'public static final NAME = "joe"', messageText:'The type is not specified for field "NAME"'],
                [lineNumber:4, sourceLineText:'protected final date = new Date()', messageText:'The type is not specified for field "date"'],
                [lineNumber:5, sourceLineText:'def defaultName', messageText:'The type is not specified for field "defaultName"'],
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
                [lineNumber:4, sourceLineText:'protected final date = new Date()', messageText:'The type is not specified for field "date"'],
        )
    }

    @Override
    protected FieldTypeRequiredRule createRule() {
        new FieldTypeRequiredRule()
    }
}
