/*
 * Copyright 2009 the original author or authors.
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

import org.codenarc.rule.Rule
import org.codenarc.rule.Violation
import org.codenarc.ruleset.RuleSet
import org.codenarc.source.CustomCompilerPhaseSourceDecorator
import org.codenarc.source.SourceCode

import java.util.concurrent.ConcurrentHashMap

/**
 * Common functionality for SourceAnalyzers.
 */
@SuppressWarnings(['AbstractClassWithoutAbstractMethod', 'Println'])
abstract class AbstractSourceAnalyzer implements SourceAnalyzer {

    protected Map<Rule, Long> ruleProcessingTimes = new ConcurrentHashMap<>()

    protected List<Violation> collectViolations(SourceCode sourceCode, RuleSet ruleSet) {
        def allViolations = []
        def suppressionService = sourceCode.suppressionAnalyzer

        def validRules = ruleSet.rules.findAll { !suppressionService.isRuleSuppressed(it) }
        def sourceAfterPhase = [(SourceCode.DEFAULT_COMPILER_PHASE): sourceCode].withDefault { phase ->
            new CustomCompilerPhaseSourceDecorator(sourceCode, phase)
        }

        for (Rule rule: validRules) {
            measureRuleProcessingTime(rule) {
                def sourceAfterRequiredPhase = sourceAfterPhase[rule.compilerPhase]
                def violations = rule.applyTo(sourceAfterRequiredPhase)
                violations.removeAll { suppressionService.isViolationSuppressed(it) }
                allViolations.addAll(violations)
            }
        }

        allViolations.sort { it.lineNumber }
        allViolations
    }

    protected void measureRuleProcessingTime(Rule rule, Closure closure) {
        long startTime = System.nanoTime()
        closure(rule)
        long endTime = System.nanoTime()
        long elapsedMillis = (endTime - startTime) / 1_000_000L
        long oldValue = ruleProcessingTimes.getOrDefault(rule, 0L)
        ruleProcessingTimes[rule] = oldValue + elapsedMillis
    }

    protected void printRuleProcessingTimes() {
        println 'Rule Processing Times (ms):'
        List<Rule> rules = new ArrayList<>(ruleProcessingTimes.keySet())
        Collections.sort(rules) { r1, r2 -> (ruleProcessingTimes[r2] <=> ruleProcessingTimes[r1]) }
        rules.each { rule ->
            println '  * ' + rule.name + ': ' + ruleProcessingTimes[rule]
        }
    }

}
