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
 * Tests for OptionalMethodParameterRule
 *
 * @author Chris Mair
 */
class OptionalMethodParameterRuleTest extends AbstractRuleTestCase<OptionalMethodParameterRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'OptionalMethodParameter'
    }

    @Test
    void test_RegularMethodParameters_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def getCount() { }
                public String getName(int count, String prefix) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OtherUsesOfOptional_NoViolations() {
        final SOURCE = '''
            class MyClass {
                Optional<String> alias
                Optional<Integer> getCount() { return Optional.of(3) }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OptionalMethodParameter_Violations() {
        final SOURCE = '''
            class MyClass {
                void doStuff(Optional<Integer> count) { }
                public String getName() { return 'abc' }
                protected int countForAlias(Optional<String> alias, Optional<Integer> total) { }
                private doSomething(Optional something) { }
            }
        '''
        assertViolations(SOURCE,
                [line:3, source:'void doStuff(Optional<Integer> count)', message:'The parameter named count of method doStuff in class MyClass is an Optional'],
                [line:5, source:'protected int countForAlias(Optional<String> alias, Optional<Integer> total)', message:'The parameter named alias of method countForAlias in class MyClass is an Optional'],
                [line:5, source:'protected int countForAlias(Optional<String> alias, Optional<Integer> total)', message:'The parameter named total of method countForAlias in class MyClass is an Optional'],
                [line:6, source:'private doSomething(Optional something)', message:'The parameter named something of method doSomething in class MyClass is an Optional'])
    }

    @Test
    void test_OptionalConstructorParameter_Violations() {
        final SOURCE = '''
            class MyClass {
                MyClass(Optional<Integer> count) { }
                public MyClass() { }
                protected MyClass(Optional<String> alias, Optional<Integer> total) { }
                private MyClass(Optional something) { }
            }
        '''
        assertViolations(SOURCE,
                [line:3, source:'MyClass(Optional<Integer> count)', message:'The parameter named count of method <init> in class MyClass is an Optional'],
                [line:5, source:'protected MyClass(Optional<String> alias, Optional<Integer> total)', message:'The parameter named alias of method <init> in class MyClass is an Optional'],
                [line:5, source:'protected MyClass(Optional<String> alias, Optional<Integer> total)', message:'The parameter named total of method <init> in class MyClass is an Optional'],
                [line:6, source:'private MyClass(Optional something)', message:'The parameter named something of method <init> in class MyClass is an Optional'])
    }

    @Override
    protected OptionalMethodParameterRule createRule() {
        new OptionalMethodParameterRule()
    }
}
