/*
 * Copyright 2020 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for OptionalFieldRule
 *
 * @author Chris Mair
 */
class OptionalFieldRuleTest extends AbstractRuleTestCase<OptionalFieldRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'OptionalField'
    }

    @Test
    void test_RegularField_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def count;
                public String name;
                private String alias = "nobody"
                static protected Object lock
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OtherUsesOfOptional_NoViolations() {
        final SOURCE = '''
            class MyClass {
                Optional<Integer> getCount() { }
                void setAlias(Optional<String> alias) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OptionalField_Violations() {
        final SOURCE = '''
            class MyClass {
                Optional<Integer> count;
                public String name;
                public Optional<String> alias = Optional.of("nobody")
                protected static Optional<Object> lock
                private Optional something
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'Optional<Integer> count;', message:'The field count in class MyClass is an Optional'],
            [line:5, source:'public Optional<String> alias = Optional.of("nobody")', message:'The field alias in class MyClass is an Optional'],
            [line:6, source:'protected static Optional<Object> lock', message:'The field lock in class MyClass is an Optional'],
            [line:7, source:'private Optional something', message:'The field something in class MyClass is an Optional'])
    }

    @Override
    protected OptionalFieldRule createRule() {
        new OptionalFieldRule()
    }
}
