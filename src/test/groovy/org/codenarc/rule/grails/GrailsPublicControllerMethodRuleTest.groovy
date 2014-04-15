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
 * Tests for GrailsPublicControllerMethodRule
 *
 * @author Chris Mair
  */
class GrailsPublicControllerMethodRuleTest extends AbstractRuleTestCase {

    private static final CONTROLLER_PATH = 'project/MyProject/grails-app/controllers/com/xxx/MyController.groovy'
    private static final OTHER_PATH = 'project/MyProject/src/groovy/MyHelper.groovy'

    @Test
    void testDisabledByDefault() {
        assert !new GrailsPublicControllerMethodRule().enabled
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsPublicControllerMethod'
    }

    @Test
    void testApplyTo_PublicMethod() {
        final SOURCE = '''
            class MyController {
                int someField
                def list = {
                    [books: Book.list() ]
                }
                void doSomething() {
                    // this does not need to be public
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'void doSomething() {')
    }

    @Test
    void testApplyTo_TwoPublicMethods() {
        final SOURCE = '''
            class MyController {
                boolean isThisNecessary() { false }
                def list = { [books: Book.list() ] }
                void doSomething() {
                    // this does not need to be public
                }
            }
        '''
        assertTwoViolations(SOURCE, 3, 'boolean isThisNecessary() { false }', 5, 'void doSomething() {')
    }

    @Test
    void testApplyTo_PublicStaticMethods() {
        final SOURCE = '''
            class MyClass {
                static int calculate() { 23 }
                public static boolean isReady() { true }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoPublicMethods() {
        final SOURCE = '''
            class MyController {
                def list = {
                    [books: Book.list() ]
                }
                protected boolean isReady() { true }
                def show = {
	 	            [ book : Book.get( params.id ) ]
	            }
                private int calculate() { 23 }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PublicMethodsWithinNonControllerClass() {
        final SOURCE = '''
            class MyHelper {
                int calculate() { 23 }
                protected boolean isReady() { true }
                public String getName() { 'abc' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PublicMethodsWithinNonControllerPath() {
        final SOURCE = '''
            class MyController {
                int calculate() { 23 }
                protected boolean isReady() { true }
                public String getName() { 'abc' }
            }
        '''
        sourceCodePath = OTHER_PATH
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesSingleName() {
        final SOURCE = '''
            class MyController {
                void myMethod() { }
            }
        '''
        rule.ignoreMethodNames = 'myMethod'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        final SOURCE = '''
            class MyController {
                void myMethod() { }
            }
        '''
        rule.ignoreMethodNames = 'otherMethod'
        assertViolations(SOURCE, [lineNumber:3, sourceLineText:'void myMethod()'])
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
            class MyController {
                boolean isReady() { }
                def doOtherStuff() { }
                void myMethod() { }
            }
        '''
        rule.ignoreMethodNames = 'is*,doO??erSt*ff,other'
        assertViolations(SOURCE, [lineNumber:5, sourceLineText:'void myMethod()'])
    }

    @Before
    void setUpGrailsPublicControllerMethodRuleTest() {
        sourceCodePath = CONTROLLER_PATH
    }

    protected Rule createRule() {
        new GrailsPublicControllerMethodRule(enabled:true)
    }
}
