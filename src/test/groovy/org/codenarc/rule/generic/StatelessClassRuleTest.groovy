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
import org.junit.Before
import org.junit.Test

/**
 * Tests for StatelessClassRule
 *
 * @author Chris Mair
  */
class StatelessClassRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'StatelessClass'
    }

    @Test
    void testDefaultConfiguration_NoViolations() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        assertNoViolations(SOURCE)
        assert !rule.ready
    }

    @Test
    void testApplyTo_HasFields() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        rule.applyToClassNames = 'MyClass'
        assertTwoViolations(SOURCE, 3, 'BigDecimal depositAmount', 4, 'def other')
    }

    @Test
    void testApplyTo_IgnoresClassesWithImmutableAnnotation() {
        final SOURCE = '''
            @Immutable class Coordinates {
                Double latitude, longitude
            }
        '''
        rule.applyToClassNames = '*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoresFieldsWithInjectAnnotation() {
        final SOURCE = '''
          class MyClass {
            @Inject
            BigDecimal depositAmount
          }
        '''
        rule.applyToClassNames = '*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_FinalField() {
        final SOURCE = '''
          class MyClass {
            final value = 5
          }
        '''
        rule.applyToFileNames = '*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticField() {
        final SOURCE = '''
          class MyClass {
            static depositCount = 5
            def other
          }
        '''
        rule.applyToFilesMatching = /.*/
        assertTwoViolations(SOURCE, 3, 'static depositCount = 5', 4, 'def other')
    }

    @Test
    void testApplyTo_StaticFinalField() {
        final SOURCE = '''
          class MyClass {
            static final DEFAULT_NAME = 'ABC'
          }
        '''
        rule.applyToClassNames = '*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreFieldNames_OneExactName() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        rule.applyToClassNames = '*'
        rule.ignoreFieldNames = 'other'
        assertSingleViolation(SOURCE, 3, 'BigDecimal depositAmount', "Violation in class MyClass. The class is marked as stateless but contains the non-final field 'depositAmount'")
    }

    @Test
    void testApplyTo_IgnoreFieldNames_TwoExactNames() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        rule.applyToClassNames = '*'
        rule.ignoreFieldNames = 'other,depositAmount'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreFieldNames_Wildcards() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
            int count
            long otherMax
          }
        '''
        rule.applyToClassNames = '*'
        rule.ignoreFieldNames = 'oth*,xxxx'
        rule.addToIgnoreFieldNames = 'yyy,deposit??ount'
        assertSingleViolation(SOURCE, 5, 'int count')
    }

    @Test
    void testApplyTo_IgnoreFieldTypes_OneExactName() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
          }
        '''
        rule.applyToClassNames = '*'
        rule.ignoreFieldTypes = 'BigDecimal'
        assertSingleViolation(SOURCE, 4, 'def other')
    }

    @Test
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
        rule.applyToClassNames = '*'
        rule.ignoreFieldTypes = '*Decimal,java.lang.Object,l?n?'
        assertTwoViolations(SOURCE, 5, 'int count = 23', 7, 'Object lock = new Object()')
    }

    @Test
    void testApplyTo_IgnoreFieldNamesAndIgnoreFieldTypes() {
        final SOURCE = '''
          class MyClass {
            BigDecimal depositAmount
            def other
            int count
            long otherMax
          }
        '''
        rule.applyToClassNames = '*'
        rule.ignoreFieldNames = 'oth*,XXX'
        rule.ignoreFieldTypes = '*Decimal,YYY,int,l?n?'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Script_HasField() {
        final SOURCE = '''
            BigDecimal depositAmount        // not considered a field
            xxx = 23                        // not considered a field
            println 'ok'
        '''
        rule.applyToFileNames = '*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoFieldDefinition() {
        final SOURCE = ' class MyClass { } '
        rule.applyToClassNames = '*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testSetAddToIgnoreFieldNames_IgnoreFieldNamesIsNull() {
        rule.setAddToIgnoreFieldNames('abc')
        assert rule.ignoreFieldNames == 'abc'
    }

    @Test
    void testSetAddToIgnoreFieldNames_IgnoreFieldNamesAlreadySet() {
        rule.ignoreFieldNames = 'abc'
        rule.setAddToIgnoreFieldNames('def,ghi')
        assert rule.ignoreFieldNames == 'abc,def,ghi'
    }

    @Test
    void testSetAddToIgnoreFieldNames_MultipleCalls_AddToIgnoreFieldNames() {
        rule.setAddToIgnoreFieldNames('abc,d*f')
        rule.addToIgnoreFieldNames = 'gh?'
        assert rule.ignoreFieldNames == 'abc,d*f,gh?'
    }

    @Before
    void setUpStatelessClassRuleTest() {
        sourceCodeName = 'MyClass.groovy'
        sourceCodePath = "/$sourceCodeName"
    }

    protected Rule createRule() {
        new StatelessClassRule()
    }

}
