/*
 * Copyright 2013 the original author or authors.
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

import org.codenarc.rule.Rule
import org.junit.Before
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for JUnitAssertEqualsConstantActualValueRule
 *
 * @author Artur Gajowy
 */
class JUnitAssertEqualsConstantActualValueRuleTest extends AbstractRuleTestCase {

    @Before
    void setup() {
        sourceCodePath = '/src/test/SampleTest.groovy'
    }
    
    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitAssertEqualsConstantActualValue'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            import org.junit.Test
            import static org.junit.Assert.assertEquals
        
        	class SampleTest {
                @Test
                def shouldFoo() {
                    //when
                    int result = 2
        
                    //then       
                    assertEquals(2, result)
                    assertEquals("Message", 2, result)
                    assertEquals(2.3d, result, 0.5d)
                    assertEquals("Message", 2.3d, result, 0.5d)
                }	
        	}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testProductionSourceNotChecked() {
        sourceCodePath = '/src/main/ProductionCode.groovy'
        assertNoViolations(SOURCE_WITH_SINGLE_VIOLATION)
    }
    
    @Test
    void testSingleViolation() {
        assertSingleViolation(SOURCE_WITH_SINGLE_VIOLATION, 4, 'Assert.assertEquals(sum, 2)', VIOLATION_MESSAGE)
    }

    private static final String SOURCE_WITH_SINGLE_VIOLATION = '''
        import org.junit.Assert
        def sum = 1 + 1
        Assert.assertEquals(sum, 2)
    '''

    @Test
    void testMultipleViolations() {
        final SOURCE = '''     
            import org.junit.Test
            import static org.junit.Assert.assertEquals
        
        	class SampleTest {
                @Test
                def shouldFoo() {
                    //when
                    int result = 2
        
                    //then       
                    assertEquals(result, 2)
                    assertEquals("Message", result, 2)
                    assertEquals(result, 2.3d, 0.5d)
                    assertEquals("Message", result, 2.3d, 0.5d)
                }	
        	}
        '''
        assertViolations(SOURCE,
            [lineNumber: 12, sourceLineText: 'assertEquals(result, 2)',                     messageText: VIOLATION_MESSAGE],
            [lineNumber: 13, sourceLineText: 'assertEquals("Message", result, 2)',          messageText: VIOLATION_MESSAGE],
            [lineNumber: 14, sourceLineText: 'assertEquals(result, 2.3d, 0.5d)',            messageText: VIOLATION_MESSAGE],
            [lineNumber: 15, sourceLineText: 'assertEquals("Message", result, 2.3d, 0.5d)', messageText: VIOLATION_MESSAGE]
        )
    }

    private static final String VIOLATION_MESSAGE = 'Found `assertEquals` with literal or constant `actual` parameter. ' +
        'Most likely it was intended to be the `expected` value.'

    protected Rule createRule() {
        new JUnitAssertEqualsConstantActualValueRule()
    }
}
