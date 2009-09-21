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

import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * Tests for AbcComplexityCalculator for fields initialized to a Closure
 *
 * @author Chris Mair
 * @version $Revision: 120 $ - $Date: 2009-04-06 12:58:09 -0400 (Mon, 06 Apr 2009) $
 */
class AbcComplexityCalculator_ClosureFieldTest extends AbstractAbcTest {

    void testCalculate_ZeroResultForEmptyClosure() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                }
            }
        """
        assert calculateForField(SOURCE) == ZERO_VECTOR
    }

    void testCalculate_CountsForClosureField() {
        final SOURCE = """
            class MyClass {
                def myClosure = {
                    def x = 1; x++                         // A=2
                    doSomething()                          // B=1
                    if (x == 23) return 99 else return 0   // C=2
                }
            }
        """
        assert calculateForField(SOURCE) == [2, 1, 2]
    }

    void testCalculate_ThrowsExceptionIfFieldInitialExpressionIsNotAClosure() {
        final SOURCE = """
            class MyClass {
                def myField = 23
            }
        """
        def fieldNode = findFirstField(SOURCE)
        shouldFailWithMessageContaining('ClosureExpression') { calculator.calculate(fieldNode) } 
    }

    private calculateForField(String source) {
        def fieldNode = findFirstField(source)
        assert fieldNode.initialExpression
        assert fieldNode.initialExpression instanceof ClosureExpression
        return calculate(fieldNode)
    }

    private findFirstField(String source) {
        def classNode = parseClass(source)
        return classNode.fields.find { it.lineNumber >= 0 }
    }

 }