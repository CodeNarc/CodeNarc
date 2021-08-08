/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for NoTabCharacterRule
 *
 * @author Yuriy Chulovskyy
 */
// codenarc-disable NoTabCharacter
class NoTabCharacterRuleTest extends AbstractRuleTestCase<NoTabCharacterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'NoTabCharacter'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            // the code below doesn't contain the tab character
            class MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            // the code below contains the tab character
            class MyClass  {	}
        '''
        assertSingleViolation(SOURCE, 3, null, 'The tab character is not allowed in source files')
    }

    @Override
    protected NoTabCharacterRule createRule() {
        new NoTabCharacterRule()
    }
}
