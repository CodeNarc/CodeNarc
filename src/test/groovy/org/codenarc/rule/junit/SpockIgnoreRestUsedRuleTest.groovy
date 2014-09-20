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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SpockIgnoreRestUsedRule
 *
 * @author Jan Ahrens
 * @auther Stefan Armbruster
  */
class SpockIgnoreRestUsedRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SpockIgnoreRestUsed'
        assert rule.specificationSuperclassNames == '*Specification'
        assert rule.specificationClassNames == null
    }

    @Test
    void testNoIgnoreRest_NoViolations() {
        final SOURCE = '''
            public class MySpec extends spock.lang.Specification {
                def "my first feature"() {
                    expect: false
                }

                def "my second feature"() {
                    given: def a = 2

                    when: a *= 2

                    then: a == 4
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoreRestWithNonSpecificationClass_NoViolations() {
        final SOURCE = '''
            public class SomeFancyClass  { // I'm not a spec
                def "my first feature"() {
                    expect: false
                }

                @IgnoreRest
                def "my second feature"() {
                    given: def a = 2

                    when: a *= 2

                    then: a == 4
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''\
            import spock.lang.*
            public class MySpec extends Specification {
                @IgnoreRest
                def "my first feature"() {
                    expect: false
                }

                def "my second feature"() {
                    given: def a = 2

                    when: a *= 2

                    then: a == 4
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 4, 'def "my first feature"() {', "The method 'my first feature' in class MySpec uses @IgnoreRest")
    }

    @Test
    void testSingleViolationFullSpecificationClassName() {
        final SOURCE = '''\
            public class MySpec extends spock.lang.Specification {
                @spock.lang.IgnoreRest
                def "my first feature"() {
                    expect: false
                }

                def "my second feature"() {
                    given: def a = 2

                    when: a *= 2

                    then: a == 4
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 3, 'def "my first feature"() {', "The method 'my first feature' in class MySpec uses @IgnoreRest")
    }

    @Test
    void testIgnoreRest_specificationSuperclassNames_Violation() {
        final SOURCE = '''\
            class MySpec extends OtherBaseClass {
                @spock.lang.IgnoreRest
                def "my first feature"() {
                    expect: false
                }
            }
        '''.stripIndent()
        rule.specificationSuperclassNames = 'OtherBaseClass'
        assertSingleViolation(SOURCE, 3, 'def "my first feature"() {', "The method 'my first feature' in class MySpec uses @IgnoreRest")
    }

    @Test
    void testIgnoreRest_specificationClassNames_DoesNotMatch_NoViolations() {
        final SOURCE = '''\
            class MySpec extends OtherBaseClass {
                @spock.lang.IgnoreRest
                def "my first feature"() {
                    expect: false
                }
            }
        '''.stripIndent()
        rule.specificationClassNames = 'OtherName'
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoreRest_specificationClassNames_Matches_Violation() {
        final SOURCE = '''\
            class MySpec extends OtherBaseClass {
                @spock.lang.IgnoreRest
                def "my first feature"() {
                    expect: false
                }
            }
        '''.stripIndent()
        rule.specificationClassNames = '*Spec'
        assertSingleViolation(SOURCE, 3, 'def "my first feature"() {', "The method 'my first feature' in class MySpec uses @IgnoreRest")
    }

    protected Rule createRule() {
        new SpockIgnoreRestUsedRule()
    }
}
