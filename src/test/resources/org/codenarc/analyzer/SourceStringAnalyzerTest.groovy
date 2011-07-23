package org.codenarc.analyzer

import org.codenarc.results.Results
import org.codenarc.rule.naming.ClassNameRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase

/**
 * Test for SourceStringAnalyzer.
 * @author Hamlet D'Arcy
 */
class SourceStringAnalyzerTest extends AbstractTestCase {

    void testRunAgainstString() {
        def source = '''
            class badName { }
        '''
        def ruleSet = new ListRuleSet([new ClassNameRule()])
        Results results = new StringSourceAnalyzer(source).analyze(ruleSet)
        log(results.violations)
        assert results.violations*.rule.name == ['ClassName']
    }
}
