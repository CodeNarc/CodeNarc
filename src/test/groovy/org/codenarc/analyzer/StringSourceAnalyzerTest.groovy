/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.analyzer

import org.codenarc.rule.MockRule
import org.codenarc.rule.Violation
import org.codenarc.rule.design.PublicInstanceFieldRule
import org.codenarc.rule.unnecessary.UnnecessaryDefInFieldDeclarationRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for StringSourceAnalyzer
 */
class StringSourceAnalyzerTest extends AbstractTestCase {

    @Test
    void testSuppressWarningsOnPackage() {
        final SOURCE = '''
            @SuppressWarnings('rule1')
            package foo

        	class Person { }
        '''
        def analyzer = new StringSourceAnalyzer(SOURCE)

        def results = analyzer.analyze(new ListRuleSet(
            [
                new MockRule(name: 'rule1', applyTo: { fail('Rule should be suppressed') }),
                new MockRule(name: 'rule2', applyTo: { [new Violation()] })
            ]
        ))
        assert results.violations.size() == 1
    }

    @Test
    void testTwoRules() {
        final SOURCE = '''
        	class Person { }
        '''
        def analyzer = new StringSourceAnalyzer(SOURCE)

        def results = analyzer.analyze(new ListRuleSet(
            [
                new MockRule(name: 'rule1', applyTo: { [new Violation()] }),
                new MockRule(name: 'rule2', applyTo: { [new Violation()] })
            ]
        ))
        assert results.violations.size() == 2
    }

    @Test
    void testFieldRules() {
        final SOURCE = '''
        	class Person {
                def String name // should cause violation
                public String address  // should cause violation
            }
        '''
        def analyzer = new StringSourceAnalyzer(SOURCE)

        def results = analyzer.analyze(new ListRuleSet(
            [
                new UnnecessaryDefInFieldDeclarationRule(),
                new PublicInstanceFieldRule(),
            ]
        ))
        assert results.violations.size() == 2
    }
}
