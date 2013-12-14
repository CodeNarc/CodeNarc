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
package org.codenarc.analyzer

import org.codehaus.groovy.control.Phases
import org.codenarc.rule.MockRule
import org.codenarc.rule.Rule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.source.SourceCode
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for AbstractSourceAnalyzer
 */
class AbstractSourceAnalyzerTest extends AbstractTestCase {

    private AbstractSourceAnalyzer analyzer = new StringSourceAnalyzer('class MyClass {}')
    
    @Test
    void testProvidesRulesWithSourceCodeOfRequiredAstCompilationPhase() {
        def results = analyzer.analyze(new ListRuleSet([
            astCompilerPhaseAssertingRule(Phases.CONVERSION),
            astCompilerPhaseAssertingRule(Phases.SEMANTIC_ANALYSIS),
            astCompilerPhaseAssertingRule(Phases.CANONICALIZATION)
        ]))
        assert results.violations.isEmpty()
    }

    private Rule astCompilerPhaseAssertingRule(int compilerPhase) {
        return new MockRule(
            compilerPhase: compilerPhase,
            applyTo: { SourceCode source -> 
                assert source.astCompilerPhase == compilerPhase
                []
            }
        )
    }
}
