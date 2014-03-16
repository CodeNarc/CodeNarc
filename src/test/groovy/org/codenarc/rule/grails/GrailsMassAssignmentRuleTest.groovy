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
package org.codenarc.rule.grails

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for GrailsMassAssignmentRule
 *
 * @author Brian Soby
 */
class GrailsMassAssignmentRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'GrailsMassAssignment'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	class Person {
                String name
                Boolean isAdmin
            }
            def bindingMap = [name: 'John', isAdmin: true]
            def person = new Person()
            def p2 = new Person("It is currently ${ new Date() }")
            def p3 = new Person(bindingMap)
            person.name = bindingMap['name']
            person.isAdmin = bindingMap.isAdmin
            person.properties = "It is currently ${ new Date() }"
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDomainObjectNoViolations() {
        // grails specific
        final SOURCE = '''
            class Person {
                String name
                Boolean isAdmin
            }
            def params = [name: 'John', isAdmin: true]
            def person = new Person()
            def staticData = [name: 'John']
            person.properties = staticData
            bindData(person,params,include=['name'])
            bindData(person,params,exclude=['isAdmin'])
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class Person {
                String name
                Boolean isAdmin
            }
            params = [name: 'John', isAdmin: true]
            def person = new Person(params)
        '''
        assertSingleViolation(SOURCE, 7, 'def person = new Person(params)')
    }

    @Test
    void testSingleDomainObjectViolation() {
        final SOURCE = '''
            class Person {
                String name
                Boolean isAdmin
            }
            def params = [name: 'John', isAdmin: true]
            def person = Person.get(1)
            person.properties = params
        '''
        assertSingleViolation(SOURCE, 8, 'person.properties = params')
    }

    @Test
    void testSingleClosureViolation() {
        final SOURCE = '''
            class Person {
                String name
                Boolean isAdmin
            }
            a = {
                def params = [name: 'John', isAdmin: true]
                def person = Person.get(1)
                person.properties = params
            }
            a()
        '''
        assertSingleViolation(SOURCE, 9, 'person.properties = params')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class Person {
                String name
                Boolean isAdmin
            }
            def params = [name: 'John', isAdmin: true]
            def person = new Person(params)
            def person2 = new Person()
            person2.properties = params
        '''
        assertViolations(SOURCE,
            [lineNumber:7, sourceLineText:'def person = new Person(params)'],
            [lineNumber:9, sourceLineText:'person2.properties = params'])
    }

    protected Rule createRule() {
        new GrailsMassAssignmentRule()
    }
}
