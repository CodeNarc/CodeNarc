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
 * Tests for AbcComplexityCalculator - calculate ABC complexity for methods
 *
 * @author Chris Mair
 * @version $Revision: 120 $ - $Date: 2009-04-06 12:58:09 -0400 (Mon, 06 Apr 2009) $
 */
class AbcComplexityCalculator_MethodTest extends AbstractAbcTest {

    void testCalculate_ZeroResultForEmptyMethod() {
        final SOURCE = """
                def myMethod() { }
        """
        assert calculateForMethod(SOURCE) == ZERO_VECTOR
    }

    void testCalculate_CountsAssignmentsForVariableDeclarations() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    def x = 1               // A=1
                    int y                   // A=1 - implicit assignment to null
                }
            }
        """
        assert calculateForMethod(SOURCE) == [2, 0, 0]
    }

    void testCalculate_IgnoresAssignmentsForConstantDeclarations() {
        final SOURCE = """
            def myMethod() {
                final CONST = 'abc'     // A=0
                String x = 'def'        // A=1
                final int C2 = 99       // A=0
            }
        """
        assert calculateForMethod(SOURCE) == [1, 0, 0]
    }

    void testCalculate_CountsAssignmentsForIncrementAndDecrement() {
        final SOURCE = """
            def myMethod() {
                x ++                    // A=1
                y --                    // A=1
                ++y; --x                // A=2
            }
        """
        assert calculateForMethod(SOURCE) == [4, 0, 0]
    }

    void testCalculate_CountsAssignmentsForArithmeticOperatorAssignment() {
        final SOURCE = """
            def myMethod() {
                y += 23                 // A=1
                x -= 23                 // A=1
                x /= 2; y*=3; x%=2      // A=3
            }
        """
        assert calculateForMethod(SOURCE) == [5, 0, 0]
    }

    void testCalculate_CountsAssignmentsForShiftOperatorAssignment() {
        final SOURCE = """
            def myMethod() {
                y >>= 2; x<<=3;     // A=2
                y>>>=4              // A=1
            }
        """
        assert calculateForMethod(SOURCE) == [3, 0, 0]
    }

    void testCalculate_CountsAssignmentsForBitwiseOperatorAssignment() {
        final SOURCE = """
            def myMethod() {
                x &= 2; y|=4; y^=3      // A=3
            }
        """
        assert calculateForMethod(SOURCE) == [3, 0, 0]
    }

    void testCalculate_CountsBranchesForMethodCalls() {
        final SOURCE = """
            def myMethod() {
                println 'ok'                    // B=1
                someInstance.someMethod()       // B=1
                SomeClass.someStaticMethod(23)  // B=1
                other.method().getSomething()   // B=2
            }
        """
        assert calculateForMethod(SOURCE) == [0, 5, 0]
    }

    void testCalculate_CountsBranchesForConstructorCalls() {
        final SOURCE = """
            def myMethod() {
                new SomeClass(99)               // B=1
                new SomeClass()                 // B=1
            }
        """
        assert calculateForMethod(SOURCE) == [0, 2, 0]
    }

    void testCalculate_CountsBranchesForPropertyAccess() {
        final SOURCE = """
            def myMethod() {
                myObject.value              // B=1
            }
        """
        assert calculateForMethod(SOURCE) == [0, 1, 0]
    }

    void testCalculate_CountsBranchesForNullSafeDereference() {
        final SOURCE = """
            def myMethod() {
                return x?.y                 // B=1                         
            }
        """
        // NOTE: Should this be counted as a condition instead of, or in addition to, a branch?
        assert calculateForMethod(SOURCE) == [0, 1, 0]
    }

    void testCalculate_CountsConditionsForComparisonOperators() {
        final SOURCE = """
            def myMethod() {
                x < 23              // C=1
                x <= 11             // C=1
                x > 99              // C=1
                x >= 22             // C=1
                x == 44             // C=1
                x != 1              // C=1
                x <=> y             // C=1
                x =~ /abc/          // C=1
                x ==~ /abc/         // C=1
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 9]
    }

    void testCalculate_CountsConditionsForIfOnly() {
        final SOURCE = """
            def myMethod() {
                if (x < 23) {
                }
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 1]
    }

    void testCalculate_CountsConditionsForIfElse() {
        final SOURCE = """
            def myMethod() {
                if (x < 23) {
                }
                else { }
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 2]
    }

    void testCalculate_CountsConditionsForSwitchWithDefault() {
        final SOURCE = """
            def myMethod() {
                switch(x) {
                    case 1: break
                    case 3: break
                    default: break
                }
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 3]
    }

    void testCalculate_CountsConditionsForSwitchWithNoDefault() {
        final SOURCE = """
            def myMethod() {
                switch(x) {
                    case 1: break
                    case 3: break
                }
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 2]
    }

    void testCalculate_CountsConditionsForTryWithCatch() {
        final SOURCE = """
            def myMethod() {
                try {
                }
                catch(Exception e) { }
                catch(Throwable t) { }
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 3]
    }

    void testCalculate_CountsConditionsForTryWithoutCatch() {
        final SOURCE = """
            def myMethod() {
                try {
                }
                finally { }
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 1]
    }

    void testCalculate_CountsConditionsForTernaryOperator() {
        final SOURCE = """
            def myMethod() {
                return !(x < 23) ? 0 : 1
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 2]
    }

    void testCalculate_CountsConditionsForElvisOperator() {
        final SOURCE = """
            def myMethod() {
                return x ?: 1           // C=1 (for unary x) + 1 (for ?)
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 2]
    }

    void testCalculate_CountsConditionsForUnaryConditionals() {
        final SOURCE = """
            def myMethod(x = 0) {
                if (x || !y || z) {
                    23
                }
                if (y) { 99 }
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 4]
    }

    void testCalculate_CountsConditionsForMultipleBooleanConditionals() {
        final SOURCE = """
            def myMethod(x = 0) {
                return x && x > 0 && x < 100 && !ready      // C=4
            }
        """
        assert calculateForMethod(SOURCE) == [0, 0, 4]
    }

    void testCalculate_CountsForConstructor() {
        final SOURCE = """
            class MyClass {
                MyClass() {
                    def x = 1; x++                         // A=2
                    doSomething()                          // B=1
                    if (x == 23) return 99 else return 0   // C=2
                }
            }
        """
        assert calculateForConstructor(SOURCE) == [2, 1, 2]
    }

    private calculateForMethod(String source) {
        def classNode = parseClass(source)
        def methodNode = classNode.methods.find { it.lineNumber >= 0 }
        assert methodNode
        return calculate(methodNode)
    }

    private calculateForConstructor(String source) {
        def classNode = parseClass(source)
        def constructorNode = classNode.declaredConstructors[0]
        assert constructorNode
        return calculate(constructorNode)
    }

}