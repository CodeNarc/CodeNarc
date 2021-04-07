/*
 * Copyright 2019 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for NoFloatRule
 *
 * @author Chris Mair
 */
class NoFloatRuleTest extends AbstractRuleTestCase<NoFloatRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NoFloat'
    }

    @Test
    void test_float_Violations() {
        final SOURCE = '''
            class MyClass {
                int count
                float floatProperty                                 // Property (field) type
                private float floatField = 1.2                      // Field type

                private float calculateAverage() { return 0 }       // Method return type

                protected void setAverage(float average) { }        // Method parameter type

                MyClass(int count, float rating, float factor) {    // Constructor parameter
                    String name = 'abc'
                    float floatVar = calculateAverage()             // Variable
                    float float1, float2 = 0                        // Variable
                }
            }
        '''
        assertViolations(SOURCE,
                [line:4, source:'float floatProperty', message:'The field floatProperty in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:5, source:'private float floatField = 1.2', message:'The field floatField in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:7, source:'private float calculateAverage() { return 0 }', message:'The method calculateAverage in class MyClass has a return type of float/Float. Prefer using BigDecimal.'],
                [line:9, source:'protected void setAverage(float average) { }', message:'The parameter named average in method setAverage of class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, float rating, float factor) {', message:'The parameter named rating in method <init> of class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, float rating, float factor) {', message:'The parameter named factor in method <init> of class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:13, source:'float floatVar = calculateAverage()', message:'The variable floatVar in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:14, source:'float float1, float2 = 0', message:'The variable float1 in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:14, source:'float float1, float2 = 0', message:'The variable float2 in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                )
    }

    @Test
    void test_FloatWrapperType_Violations() {
        final SOURCE = '''
            class MyClass {
                int count
                Float floatProperty                                 // Property (field) type
                private Float floatField = 1.2                      // Field type

                private Float calculateAverage() { return 0 }       // Method return type

                protected void setAverage(Float average) { }        // Method parameter type

                MyClass(int count, Float rating, Float factor) {    // Constructor parameter
                    String name = 'abc'
                    Float floatVar = calculateAverage()             // Variable
                    Float float1, float2 = 0                        // Variable
                }
            }
        '''
        assertViolations(SOURCE,
                [line:4, source:'Float floatProperty', message:'The field floatProperty in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:5, source:'private Float floatField = 1.2', message:'The field floatField in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:7, source:'private Float calculateAverage() { return 0 }', message:'The method calculateAverage in class MyClass has a return type of float/Float. Prefer using BigDecimal.'],
                [line:9, source:'protected void setAverage(Float average) { }', message:'The parameter named average in method setAverage of class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, Float rating, Float factor) {', message:'The parameter named rating in method <init> of class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, Float rating, Float factor) {', message:'The parameter named factor in method <init> of class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:13, source:'Float floatVar = calculateAverage()', message:'The variable floatVar in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:14, source:'Float float1, float2 = 0', message:'The variable float1 in class MyClass is of type float/Float. Prefer using BigDecimal.'],
                [line:14, source:'Float float1, float2 = 0', message:'The variable float2 in class MyClass is of type float/Float. Prefer using BigDecimal.'],
        )
    }

    @Override
    protected NoFloatRule createRule() {
        new NoFloatRule()
    }
}
