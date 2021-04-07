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
 * Tests for NoDoubleRule
 *
 * @author Chris Mair
 */
class NoDoubleRuleTest extends AbstractRuleTestCase<NoDoubleRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NoDouble'
    }

    @Test
    void test_double_Violations() {
        final SOURCE = '''
            class MyClass {
                int count
                double doubleProperty                               // Property (field) type
                private double doubleField = 1.2                    // Field type

                private double calculateAverage() { return 0 }      // Method return type

                protected void setAverage(double average) { }       // Method parameter type

                MyClass(int count, double rating, double factor) {  // Constructor parameter
                    String name = 'abc'
                    double doubleVar = calculateAverage()           // Variable
                    double double1, double2 = 0                      // Variable
                }
            }
        '''
        assertViolations(SOURCE,
                [line:4, source:'double doubleProperty', message:'The field doubleProperty in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:5, source:'private double doubleField = 1.2', message:'The field doubleField in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:7, source:'private double calculateAverage() { return 0 }', message:'The method calculateAverage in class MyClass has a return type of double/Double. Prefer using BigDecimal.'],
                [line:9, source:'protected void setAverage(double average) { }', message:'The parameter named average in method setAverage of class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, double rating, double factor) {', message:'The parameter named rating in method <init> of class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, double rating, double factor) {', message:'The parameter named factor in method <init> of class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:13, source:'double doubleVar = calculateAverage()', message:'The variable doubleVar in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:14, source:'double double1, double2 = 0', message:'The variable double1 in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:14, source:'double double1, double2 = 0', message:'The variable double2 in class MyClass is of type double/Double. Prefer using BigDecimal.'],
        )
    }

    @Test
    void test_DoubleWrapperType_Violations() {
        final SOURCE = '''
            class MyClass {
                int count
                Double doubleProperty                               // Property (field) type
                private Double doubleField = 1.2                    // Field type

                private Double calculateAverage() { return 0 }      // Method return type

                protected void setAverage(Double average) { }       // Method parameter type

                MyClass(int count, Double rating, Double factor) {  // Constructor parameter
                    String name = 'abc'
                    Double doubleVar = calculateAverage()           // Variable
                    Double double1, double2 = 0                     // Variable
                }
            }
        '''
        assertViolations(SOURCE,
                [line:4, source:'Double doubleProperty', message:'The field doubleProperty in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:5, source:'private Double doubleField = 1.2', message:'The field doubleField in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:7, source:'private Double calculateAverage() { return 0 }', message:'The method calculateAverage in class MyClass has a return type of double/Double. Prefer using BigDecimal.'],
                [line:9, source:'protected void setAverage(Double average) { }', message:'The parameter named average in method setAverage of class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, Double rating, Double factor) {', message:'The parameter named rating in method <init> of class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:11, source:'MyClass(int count, Double rating, Double factor) {', message:'The parameter named factor in method <init> of class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:13, source:'Double doubleVar = calculateAverage()', message:'The variable doubleVar in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:14, source:'Double double1, double2 = 0', message:'The variable double1 in class MyClass is of type double/Double. Prefer using BigDecimal.'],
                [line:14, source:'Double double1, double2 = 0', message:'The variable double2 in class MyClass is of type double/Double. Prefer using BigDecimal.'],
        )
    }

    @Override
    protected NoDoubleRule createRule() {
        new NoDoubleRule()
    }
}
