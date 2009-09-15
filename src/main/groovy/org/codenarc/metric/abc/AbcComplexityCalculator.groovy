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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.source.SourceCode
import org.codenarc.metric.ClassResults
import org.codenarc.metric.MethodResults

/**
 * Calculate the ABC Metric for a class/method.
 *
 * The ABC Counting Rules for Groovy:
 * <pre>
 *   1. Add one to the assignment count for each occurrence of an assignment operator, excluding constant declarations:
 *      = *= /= %= += <<= >>= &= |= ^= >>>=
 *   2. Add one to the assignment count for each occurrence of an increment or decrement operator (prefix or postfix):
 *      ++ --
 *   3. Add one to the branch count for each function call or class method call.
 *   4. Add one to the branch count for each occurrence of the new operator.
 *   5. Add one to the condition count for each use of a conditional operator:
 *      == != <= >= < > <=> =~ ==~
 *   6. Add one to the condition count for each use of the following keywords:
 *      else case default try catch ?
 *   7. Add one to the condition count for each unary conditional expression.
 * </pre>
 *
 * Additional notes:
 * <ul>
 *   <li>A property access is treated like a method call (and thus increments the branch count)</li>
 * </ul>
 *
 * See http://www.softwarerenovation.com/ABCMetric.pdf
 *
 * @author Chris Mair
 * @version $Revision: 120 $ - $Date: 2009-04-06 12:58:09 -0400 (Mon, 06 Apr 2009) $
 */
class AbcComplexityCalculator {
    SourceCode sourceCode

    def calculate(ClassNode classNode) {
        def abcVectorAggregate = new AbcVectorAggregate()
        def children = []
        def realMethods = classNode.methods.findAll { it.lineNumber >= 0 }
        realMethods.each { methodNode ->
            def methodResults = calculate(methodNode)
            children << methodResults
            abcVectorAggregate.add(methodResults.value)
        }

        def totalAbcVector = abcVectorAggregate.getSumAbcVector()
        def averageAbcVector = abcVectorAggregate.getAverageAbcVector()

        return new ClassResults(name:classNode.name, totalValue:totalAbcVector, averageValue:averageAbcVector, children: children)
    }

    def calculate(MethodNode methodNode) {
        def visitor = new AbcComplexityAstVisitor(sourceCode:sourceCode)
        visitor.visitMethod(methodNode)
        def abcVector = new AbcVector(visitor.numberOfAssignments, visitor.numberOfBranches, visitor.numberOfConditions)
        return new MethodResults(name:methodNode.name, value:abcVector)
    }
}
