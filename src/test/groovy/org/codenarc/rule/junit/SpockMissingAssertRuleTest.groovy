/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 *
 * @author Jean André Gauthier
 * @author Daniel Clausen
  */
class SpockMissingAssertRuleTest extends AbstractRuleTestCase<SpockMissingAssertRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SpockMissingAssert'
    }

    @Test
    void testTopLevelExpression_NoViolations() {
        final SOURCE = '''
            public class MySpec extends spock.lang.Specification {
                def "testTopLevelExpression_NoViolations"() {
                    expect:
                    "123"
                    123
                    false
                    assert false
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfStatement_NoViolations() {
        final SOURCE = '''
            public class MySpec extends spock.lang.Specification {
                def "testIfStatement_NoViolations"() {
                    expect:
                    if (true) {
                        "123"
                        123
                        assert false
                    }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfStatement_SingleViolation() {
        final SOURCE = '''
            public class MySpec extends spock.lang.Specification {
                def "testIfStatement_NoViolations"() {
                    expect:
                    if (true) {
                        "123"
                        123
                        false
                    }
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 8, "false", "'expect:' contains a boolean expression in a nested statement, which is not implicitly asserted")
    }

    @Override
    protected SpockMissingAssertRule createRule() {
        new SpockMissingAssertRule()
    }
}
