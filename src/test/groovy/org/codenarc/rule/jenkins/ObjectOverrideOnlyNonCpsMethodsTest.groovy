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
                void wait() {
                }
                
                @Override
                void wait(long timeout) {
                }
                
                @Override
                void wait(Long timeout, Integer nanos) {
                }
                
                @Override
                protected Object clone() {
                }
                
                @Override
                protected void finalize() {
                }
                
                @Override
                Class<?> getClass() {
                }
                
                @Override
                int hashCode() {
                }
                
                @Override
                void notify() {
                }
                
                @Override
                void notifyAll() {
                }
                
                String toText() {
                    return ''
                }
            }
        '''
        assertViolations(SOURCE,
            [line: 5, source: 'String toString() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 10, source: 'boolean equals(Object o) {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 15, source: 'void wait() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 19, source: 'void wait(long timeout) {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 23, source: 'void wait(Long timeout, Integer nanos) {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 27, source: 'protected Object clone() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 31, source: 'protected void finalize() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 35, source: 'Class<?> getClass() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 39, source: 'int hashCode() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 43, source: 'void notify() {', message: 'Overridden methods from Object should not be CPS transformed'],
            [line: 47, source: 'void notifyAll() {', message: 'Overridden methods from Object should not be CPS transformed'],
        )
    }

    @Test
    void testObjectOverridesNonCps_NoViolation() {
        final SOURCE = '''
            import org.codenarc.rule.jenkins.NonCPS
            class Test {
            
                @Override
                @NonCPS
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
