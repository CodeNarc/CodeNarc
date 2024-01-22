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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for ForbiddenCallInCpsMethodRule
 *
 * @author Daniel Zänker
 */
class ForbiddenCallInCpsMethodRuleTest extends AbstractRuleTestCase<ForbiddenCallInCpsMethodRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 1
        assert rule.name == 'ForbiddenCallInCpsMethod'
    }

    @Test
    void testForbiddenMethodsWithDifferentParameters_NoViolation() {
        final SOURCE = '''
            void run() {
                List l = [4,1,3]
                l.sort()
                l.toSorted()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testForbiddenMethodsInNonCps_NoViolation() {
        final SOURCE = '''
            import com.cloudbees.groovy.cps.NonCPS
            
            @NonCPS
            void runSafe() {
                List l = [4,1,3]
                l.sort { a, b -> a > b } 
                
                l.toSorted { a, b -> a > b } 
                
                "hello".eachLine { line, number -> println(line) }
                "hello".eachLine(2) { line, number -> println(line) }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testForbiddenMethodsInCps_Violation() {
        final SOURCE = '''
            void run() {
                List l = [4,1,3]
                l.sort { a, b -> a > b } 
                
                l.toSorted { a, b -> a > b } 
                
                "hello".eachLine { line, number -> println(line) }
                "hello".eachLine(2) { line, number -> println(line) }
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'l.sort { a, b -> a > b }', message: 'Method java.lang.Iterable.sort(groovy.lang.Closure) is forbidden in CPS transformed methods'],
            [line: 6, source: 'l.toSorted { a, b -> a > b }', message: 'Method java.lang.Iterable.toSorted(groovy.lang.Closure) is forbidden in CPS transformed methods'],
            [line: 8, source: '"hello".eachLine { line, number -> println(line) }', message: 'Method java.lang.CharSequence.eachLine(groovy.lang.Closure) is forbidden in CPS transformed methods'],
            [line: 9, source: '"hello".eachLine(2) { line, number -> println(line) }', message: 'Method java.lang.CharSequence.eachLine(java.lang.Integer, groovy.lang.Closure) is forbidden in CPS transformed methods'])
    }

    @Override
    protected ForbiddenCallInCpsMethodRule createRule() {
        new ForbiddenCallInCpsMethodRule()
    }
}
