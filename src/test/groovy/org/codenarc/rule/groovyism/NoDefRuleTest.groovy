package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * @author Dominik Przybysz
 */
public class NoDefRuleTest extends AbstractRuleTestCase {

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            List l = [1, 2, 3, 4]
            l.flatten()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            def l = [1, 2, 3, 4]
            l.flatten()
        '''
        assertSingleViolation SOURCE, 1, 'def l = [1, 2, 3, 4]', NoDefRule.MESSAGE
    }

    @Test
    void testTwoViolation() {
        final SOURCE = '''
            def test(def l){
                int k = 3
                def i = 5
            }
        '''
        assertTwoViolations (SOURCE,
                1, 'def test(def l){', NoDefRule.MESSAGE,
                3, 'def i = 5', NoDefRule.MESSAGE)
    }

    @Test
    void testExcludesNoViolation() {
        rule.excludePattern = /((setup|cleanup)(|Spec)|"[^"].*")\(\)/ //spock methods
        final SOURCE = '''
            def setup(){}
            def setupSpec(){}
            def cleanup(){}
            def cleanupSpec(){}
            def "should send"(){}
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected Rule createRule() {
        new NoDefRule()
    }
}