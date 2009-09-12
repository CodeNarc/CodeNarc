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
 * Tests for AbcComplexityCalculator
 *
 * @author Chris Mair
 * @version $Revision: 120 $ - $Date: 2009-04-06 12:58:09 -0400 (Mon, 06 Apr 2009) $
 */
class AbcComplexityCalculator_ClassTest extends AbstractAbcTest {

    private calculator

    void testCalculate_EmptyResultsForClassWithNoMethods() {
        final SOURCE = """
            int myValue
        """
        assertCalculateForClass(SOURCE, [0, 0, 0], null)
    }

    void testCalculate_ResultsForClassWithOneMethod() {
        final SOURCE = """
            def a() {
                def x = 1               // A=1
            }
        """
        assertCalculateForClass(SOURCE, [0, 0, 0], [a:[1, 0, 0]])
    }

//    void testCalculate_ResultsForClassWithSeveralMethods() {
//        final SOURCE = """
//            def a() {
//                def x = 1               // A=1
//            }
//        """
//        assertCalculateForClass(SOURCE, [0, 0, 0], [a:[1, 0, 0]])
//    }

    void setUp() {
        super.setUp()
        calculator = new AbcComplexityCalculator()
    }

    private void assertCalculateForClass(String source, List classValues, Map methodValues) {
        def classNode = parseClass(source)
        def results = calculator.calculate(classNode)
        log("results=$results")
        def abcVector = results.value
        assert results.name == classNode.name
        assertEquals(abcVector, classValues)

        def methodNames = methodValues?.keySet()
        log("methodNames=$methodNames")
        methodNames.eachWithIndex { methodName, index ->
            def methodAbcVector = results.children[index].value
            assertEquals(methodAbcVector, methodValues[methodName])
        }
    }

 }