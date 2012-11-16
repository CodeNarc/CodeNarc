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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Before
import org.junit.Test

/**
 * Tests for ImplementationAsTypeRule
 *
 * @author Chris Mair
  */
class ImplementationAsTypeRuleTest extends AbstractRuleTestCase {
    private static final BAD_TYPES = [
        'java.util.ArrayList',
        'java.util.ArrayList<String>',
        'java.util.GregorianCalendar',
        'java.util.HashMap',
        'java.util.HashMap<String,String>',
        'java.util.HashSet',
        'java.util.HashSet<String>',
        'java.util.Hashtable',
        'java.util.Hashtable<Integer,String>',
        'java.util.LinkedHashMap',
        'java.util.LinkedHashMap<Integer,BigDecimal>',
        'java.util.LinkedHashSet',
        'java.util.LinkedHashSet<Integer>',
        'java.util.LinkedList',
        'java.util.LinkedList<String>',
        'java.util.TreeMap',
        'java.util.TreeMap<String,Integer>',
        'java.util.TreeSet',
        'java.util.TreeSet<String>',
        'java.util.Vector',
        'java.util.Vector<Integer>',
        'java.util.concurrent.CopyOnWriteArrayList',
        'java.util.concurrent.CopyOnWriteArrayList<String>',
        'java.util.concurrent.CopyOnWriteArraySet',
        'java.util.concurrent.ConcurrentHashMap',
        'java.util.concurrent.ArrayBlockingQueue',
        'java.util.concurrent.ConcurrentLinkedQueue',
        'java.util.concurrent.DelayQueue',
        'java.util.concurrent.LinkedBlockingQueue',
        'java.util.concurrent.PriorityBlockingQueue',
        'java.util.concurrent.PriorityQueue',
        'java.util.concurrent.SynchronousQueue',
    ]
    private allBadTypes

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ImplementationAsType'
    }

    @Test
    void testApplyTo_Parameters_NoViolations() {
        final SOURCE = '''
        	def findUnique(Calendar cal, Map map, Set set, List list, def other) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ReturnTypes_NoViolations() {
        final SOURCE = '''
        	Calendar m1() { }
        	Map m2() { }
        	List m3() { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Variables_NoViolations() {
        final SOURCE = '''
        	Calendar v1
        	Map v2
        	List v3
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MethodParameters_Violations() {
        allBadTypes.each { badType ->
            log("Testing for [$badType]")
            final SOURCE = "def doSomething($badType parm) { }"
            assertSingleViolation(SOURCE, 1, SOURCE)
        }
    }

    @Test
    void testApplyTo_ConstructorParameters_Violations() {
        allBadTypes.each { badType ->
            final SOURCE = """
                class MyClass {
                    MyClass($badType parm) { }
                }
                """
            assertSingleViolation(SOURCE, 3, "$badType parm")
        }
    }

    @Test
    void testApplyTo_ClosureParameters_Violations() {
        allBadTypes.each { badType ->
            log("Testing for [$badType]")
            final SOURCE = "def closure = { $badType parm ->  }"
            assertSingleViolation(SOURCE, 1, "$badType parm")
        }
    }

    @Test
    void testApplyTo_ReturnTypes_Violations() {
        allBadTypes.each { badType ->
            final SOURCE = "$badType doSomething() { }"
            assertSingleViolation(SOURCE, 1, SOURCE)
        }
    }

    @Test
    void testApplyTo_VariableTypes_Violations() {
        allBadTypes.each { badType ->
            final SOURCE = """
                def myMethod() {
                    $badType someVariable = null
                }
                """
            assertSingleViolation(SOURCE, 3, "$badType someVariable")
        }
    }

    @Test
    void testApplyTo_MultipleVariables_Violations() {
        final SOURCE = '''
            def myMethod() {
                def (LinkedList v1, Vector v2) = [null, null]
            }
            '''
        assertViolations(SOURCE,
            [lineNumber: 3, sourceLineText:'LinkedList v1'],
            [lineNumber: 3, sourceLineText:'Vector v2'])
    }

    @Test
    void testApplyTo_FieldTypes_Violations() {
        allBadTypes.each { badType ->
            final SOURCE = """
                class MyClass {
                    $badType someField
                }
                """
            assertSingleViolation(SOURCE, 3, "$badType someField")
        }
    }

    @Before
    void setUpImplementationAsTypeRuleTest() {
        def badTypesClassNameOnly = BAD_TYPES.collect { badType ->  classNameOnly(badType) }
        allBadTypes = BAD_TYPES + badTypesClassNameOnly
    }

    protected Rule createRule() {
        new ImplementationAsTypeRule()
    }

    private String classNameOnly(String fullClassName) {
        def index = fullClassName.lastIndexOf('.')
        assert index > -1
        fullClassName[index + 1..-1]
    }

}
