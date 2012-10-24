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
package org.codenarc.rule.security

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for InsecureRandomRule
 *
 * @author Hamlet D'Arcy
  */
class InsecureRandomRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'InsecureRandom'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
              new java.security.SecureRandom()
              new SecureRandom()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMathRandom() {
        final SOURCE = '''
          Math.random()
          java.lang.Math.random()
        '''
        assertTwoViolations(SOURCE,
            2, 'Math.random()', 'Using Math.random() is insecure. Use SecureRandom instead',
            3, 'java.lang.Math.random()', 'Using Math.random() is insecure. Use SecureRandom instead')
    }

    @Test
    void testRandom() {
        final SOURCE = '''
              def r1 = new Random()
              def r2 = new java.util.Random()
        '''
        assertTwoViolations(SOURCE,
            2, 'new Random()', 'Using Random is insecure. Use SecureRandom instead',
            3, 'new java.util.Random()', 'Using Random is insecure. Use SecureRandom instead')
    }

    protected Rule createRule() {
        new InsecureRandomRule()
    }
}
