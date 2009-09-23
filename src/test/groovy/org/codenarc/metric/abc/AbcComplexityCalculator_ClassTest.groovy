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
package org.codenarc.metric.abc

/**
 * Tests for AbcComplexityCalculator class metrics
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbcComplexityCalculator_ClassTest extends AbstractAbcTest {

    void testCalculate_EmptyResultsForClassWithNoMethods() {
        final SOURCE = """
            int myValue
        """
        assertCalculateForClass(SOURCE, ZERO_VECTOR, ZERO_VECTOR, null)
    }

    void testCalculate_ResultsForClassWithOneMethod() {
        final SOURCE = """
            def a() {
                def x = 1               // A=1
            }
        """
        assertCalculateForClass(SOURCE, [1, 0, 0], [1,0,0], [a:[1, 0, 0]])
    }

    void testCalculate_ResultsForClassWithSeveralMethods() {
        final SOURCE = """
            def a() {
                def x = 1; y = x            // A=2
            }
            def b() {
                new SomeClass(99)           // B=1
                new SomeClass().run()       // B=2
                x++                         // A=1
            }
            def c() {
                switch(x) {
                    case 1: break           // C=1
                    case 3: break           // C=1
                }
                return x && x > 0 && x < 100 && !ready      // C=4
            }
        """
        assertCalculateForClass(SOURCE, [3,3,6], [1,1,2], [a:[2,0,0], b:[1,3,0], c:[0,0,6]])
    }

    void testCalculate_ResultsForClassWithOneClosureField() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    def x = 1; x++                         // A=2
                    doSomething()                          // B=1
                    if (x == 23) return 99 else return 0   // C=2
                }
            }
        """
        assertCalculateForClass(SOURCE, [2,1,2], [2,1,2], [myClosure:[2,1,2]])
    }

    private void assertCalculateForClass(String source, List classTotalValues, List classAverageValues, Map methodValues) {
        def classNode = parseClass(source)
        def results = calculator.calculate(classNode)
        log("results=$results")
//        assert results.name == classNode.name
        assertEquals(results.averageAbcVector, classAverageValues)
        assertEquals(results.totalAbcVector, classTotalValues)

        def methodNames = methodValues?.keySet()
        methodNames.each { methodName ->
            def methodAbcVector = results.children[methodName].abcVector
            assertEquals(methodAbcVector, methodValues[methodName])
        }
    }

 }