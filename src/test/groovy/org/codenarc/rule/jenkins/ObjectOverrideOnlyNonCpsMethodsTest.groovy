/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.junit.jupiter.api.Test

/**
 * Tests for ObjectOverrideOnlyNonCpsMethods
 *
 * Note: Usage of com.cloudbees.groovy.cps.NonCPS intentionally commented out for test purposes to mark where it would be used in a real use case.
 * This is necessary because we don't want to add this to the test dependencies.
 *
 * @author Daniel Zänker
 */
class ObjectOverrideOnlyNonCpsMethodsTest extends AbstractJenkinsRuleTestCase<ObjectOverrideOnlyNonCpsMethods> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ObjectOverrideOnlyNonCpsMethods'
    }

    @Test
    void testObjectOverrideNotNonCps_Violation() {
        final SOURCE = '''
            class Test {
             
                @Override
                String toString() {
                    return ''
                }
                
                @Override
                boolean equals(Object o) {
                    return true
                }
                
                @Override
                void wait(long timeout) {
                }
                
                @Override
                void wait(Long timeout, Integer nanos) {
                }
                
                String toText() {
                    return ''
                }
            }
        '''
        assertViolations(SOURCE,
            [line: 5, source: 'String toString() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 10, source: 'boolean equals(Object o) {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 15, source: 'void wait(long timeout) {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 19, source: 'void wait(Long timeout, Integer nanos) {', message: 'Overridden methods from Object should not be CPS transformed']
        )
    }

    @Test
    void testObjectOverridesNonCps_NoViolation() {
        addNonCPSMethod('toString')

        final SOURCE = '''
            //import com.cloudbees.groovy.cps.NonCPS
            class Test {
            
                @Override
                //@NonCPS
                String toString() {
                    return ''
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoObjectMethod_NoViolation() {
        final SOURCE = '''
            class Base {
                String toText() {
                    return ''
                }
            }
            class Test extends Base {
            
                @Override
                String toText() {
                    return ''
                }
                
                void method() {
                
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected ObjectOverrideOnlyNonCpsMethods createRule() {
        new ObjectOverrideOnlyNonCpsMethods()
    }
}
