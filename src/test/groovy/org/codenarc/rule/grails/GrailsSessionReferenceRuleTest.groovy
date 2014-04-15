/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.grails

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Before
import org.junit.Test

/**
 * Tests for GrailsSessionReferenceRule
 *
 * @author Chris Mair
  */
class GrailsSessionReferenceRuleTest extends AbstractRuleTestCase {

    private static final CONTROLLER_PATH = 'project/MyProject/grails-app/controllers/com/xxx/MyController.groovy'
    private static final TAGLIB_PATH = 'project/MyProject/grails-app/taglib/MyTagLib.groovy'
    private static final OTHER_PATH = 'project/MyProject/src/groovy/MyHelper.groovy'

    @Test
    void testDisabledByDefault() {
        assert !new GrailsSessionReferenceRule().enabled
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsSessionReference'
    }

    @Test
    void testApplyTo_AssignmentToSessionProperty() {
        final SOURCE = '''
            class MyClass {
                int someField
                private void doSomething() {
                    session.count = 23
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'session.count = 23')
    }

    @Test
    void testApplyTo_SimpleReferenceToSession() {
        final SOURCE = '''
            class MyClass {
                def edit = {
                    println session
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'println session')
    }

    @Test
    void testApplyTo_ReferenceWithinMethodCallArgument() {
        final SOURCE = '''
            class MyClass {
                def edit = {
                    doSomething(1, 'abc', session)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, "doSomething(1, 'abc', session)")
    }

    @Test
    void testApplyTo_ReferenceWithinFieldInitializer() {
        final SOURCE = '''
            class MyClass {
                def mySession = session

                def edit = {
                    println "amount=${session.amount}"
                }
            }
        '''
        assertTwoViolations(SOURCE, 3, 'def mySession = session', 6, 'println "amount=${session.amount}"')
    }

    @Test
    void testApplyTo_ReferenceWithinTagLib() {
        final SOURCE = '''
            class SimpleTagLib {
	            def simple = { attrs, body -> session.amount = attrs.amount }
            }
        '''
        sourceCodePath = TAGLIB_PATH
        assertSingleViolation(SOURCE, 3, 'def simple = { attrs, body -> session.amount = attrs.amount }')
    }

    @Test
    void testApplyTo_ReferenceWithinNonControllerClass() {
        final SOURCE = '''
            class MyClass {
                def edit = {
                    println session
                }
            }
        '''
        sourceCodePath = OTHER_PATH
        assertNoViolations(SOURCE)
    }

    @Before
    void setUpGrailsSessionReferenceRuleTest() {
        sourceCodePath = CONTROLLER_PATH
    }

    protected Rule createRule() {
        new GrailsSessionReferenceRule(enabled:true)
    }
}
