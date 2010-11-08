/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for GStringAsMapKeyRule
 *
 * @author @Hackergarten
 * @version $Revision$ - $Date$
 */
class GStringAsMapKeyRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "GStringAsMapKey"
    }

    void testSimpleMapIsOK() {
        final SOURCE = '''
        	Map map = [string: 'thats ok']
        '''
        assertNoViolations(SOURCE)
    }

    void testAnyGStringIsDisallowedAsKey() {
        final SOURCE = '''
            Map map = ["${ someRef }" : 'invalid' ]
        '''
        assertSingleViolation( SOURCE,
                2, '["${ someRef }" :')
    }

    void testNestedGStringInValueIsCalled() {
        final SOURCE = '''
            def x = 'something'
            Map map = ["outer $x" :
                          ["nested $x" : 'invalid']
             ]
        '''
        assertTwoViolations( SOURCE,
                3, '["outer $x" ',
                4, '["nested $x" ')
    }

    void testNestedGStringInKeyIsCalled() {
        final SOURCE = '''
            def x = 'something'
            Map map = [ ["outer $x" : 'foo'] : 'invalid' ]
        '''
        assertSingleViolation( SOURCE, 3, '["outer $x" ' )
    }

    protected Rule createRule() {
        new GStringAsMapKeyRule()
    }

}
