package org.codenarc.analyzer

import org.codenarc.results.Results
import org.codenarc.ruleset.RuleSetUtil

/**
 * Test for SourceStringAnalyzer.
 * @author Hamlet D'Arcy
 */
class SourceStringAnalyzerTest {
    private static final RULESET_FILES = 'RunCodeNarcAgainstProjectSourceCode.ruleset'

    void testRunAgainstString() {
        def source = '''
            def x = '123'
            def y = '123'
'''
        def ruleset = RuleSetUtil.loadRuleSetFile(RULESET_FILES)
        Results results = new StringSourceAnalyzer(source).analyze(ruleset)
        assert !results.getViolationsWithPriority(0)
        assert !results.getViolationsWithPriority(1)
        assert 2 == results.getViolationsWithPriority(2).size()
        assert 1 == results.getViolationsWithPriority(3).size()
    }
}
