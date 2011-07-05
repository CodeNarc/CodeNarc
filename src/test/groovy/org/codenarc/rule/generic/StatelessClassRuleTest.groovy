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
package org.codenarc.rule.generic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for StatelessClassRule
 *
 * @author Chris Mair
  */
class StatelessClassRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StatelessClass'
    }

    void testApplyTo_HasFields() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        assertTwoViolations(SOURCE, 3, 'BigDecimal depositAmount', 4, 'def other')
    }

    void testApplyTo_IgnoresClassesWithImmutableAnnotation() {
        final SOURCE = '''
            @Immutable class Coordinates {
                Double latitude, longitude
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_FinalField() {
        final SOURCE = '''
          class MyClass {
            final value = 5
          }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_StaticField() {
        final SOURCE = '''
          class MyClass {
            static depositCount = 5
            def other
          }
        '''
        assertTwoViolations(SOURCE, 3, 'static depositCount = 5', 4, 'def other')
    }

    void testApplyTo_StaticFinalField() {
        final SOURCE = '''
          class MyClass {
            static final DEFAULT_NAME = 'ABC'
          }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_IgnoreFieldNames_OneExactName() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        rule.ignoreFieldNames = 'other'
        assertSingleViolation(SOURCE, 3, 'BigDecimal depositAmount', 'The class MyClass is marked as stateless but contains the non-final field "depositAmount"')
    }

    void testApplyTo_IgnoreFieldNames_TwoExactNames() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        rule.ignoreFieldNames = 'other,depositAmount'
        assertNoViolations(SOURCE)
    }

    void testApplyTo_IgnoreFieldNames_Wildcards() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
            int count
            long otherMax
          }
        '''
        rule.ignoreFieldNames = 'oth*,xxxx'
        rule.addToIgnoreFieldNames = 'yyy,deposit??ount'
        assertSingleViolation(SOURCE, 5, 'int count')
    }

    void testApplyTo_IgnoreFieldTypes_OneExactName() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        rule.ignoreFieldTypes = 'BigDecimal'
        assertSingleViolation(SOURCE, 4, 'def other')
    }

    void testApplyTo_IgnoreFieldTypes_Wildcards() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
            int count = 23
            long otherMax
            Object lock = new Object()
          }
        '''
        rule.ignoreFieldTypes = '*Decimal,java.lang.Object,l?n?'
        assertTwoViolations(SOURCE, 5, 'int count = 23', 7, 'Object lock = new Object()')
    }

    void testApplyTo_IgnoreFieldNamesAndIgnoreFieldTypes() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
            int count
            long otherMax
          }
        '''
        rule.ignoreFieldNames = 'oth*,XXX'
        rule.ignoreFieldTypes = '*Decimal,YYY,int,l?n?'
        assertNoViolations(SOURCE)
    }


    void testApplyTo_Script_HasField() {
        final SOURCE = '''
            BigDecimal depositAmount        // not considered a field
            xxx = 23                        // not considered a field
            println 'ok'
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoFieldDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    void testSetAddToIgnoreFieldNames_IgnoreFieldNamesIsNull() {
        rule.setAddToIgnoreFieldNames('abc')
        assert rule.ignoreFieldNames == 'abc'
    }

    void testSetAddToIgnoreFieldNames_IgnoreFieldNamesAlreadySet() {
        rule.ignoreFieldNames = 'abc'
        rule.setAddToIgnoreFieldNames('def,ghi')
        assert rule.ignoreFieldNames == 'abc,def,ghi'
    }

    void testSetAddToIgnoreFieldNames_MultipleCalls_AddToIgnoreFieldNames() {
        rule.setAddToIgnoreFieldNames('abc,d*f')
        rule.addToIgnoreFieldNames = 'gh?'
        assert rule.ignoreFieldNames == 'abc,d*f,gh?'
    }

    void setUp() {
        super.setUp()
        sourceCodeName = 'MyClass.groovy'
        sourceCodePath = "/$sourceCodeName"
    }

    protected Rule createRule() {
        new StatelessClassRule()
    }

}