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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryFinalOnPrivateMethodRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryFinalOnPrivateMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryFinalOnPrivateMethod'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	final method1() {}
        	private method2() {}
        	protected final method2() {}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
        	private final method() {}
        '''
        assertSingleViolation(SOURCE, 2, 'private final method()', "The 'method' method is both private and final")
    }

    protected Rule createRule() {
        new UnnecessaryFinalOnPrivateMethodRule()
    }
}
