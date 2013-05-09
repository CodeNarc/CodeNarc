/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.junit.Assert

/**
 * Reports usages of org.junit.Assert.assertEquals([message,] expected, actual) where the 'actual' parameter
 * is a constant or a literal. Most likely it was intended to be the 'expected' value.
 *
 * @author Artur Gajowy
 */
class JUnitAssertEqualsConstantActualValueRule extends AbstractAstVisitorRule {
    String name = 'JUnitAssertEqualsConstantActualValue'
    int priority = 2
    Class astVisitorClass = JUnitAssertEqualsConstantActualValueAstVisitor
    String applyToFilesMatching = DEFAULT_TEST_FILES
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
}

class JUnitAssertEqualsConstantActualValueAstVisitor extends AbstractAstVisitor {
    
    private static final ClassNode ASSERT_TYPE = ClassHelper.make(Assert)
    private static final String ASSERT_EQUALS = 'assertEquals'

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (call.objectExpression.type == ASSERT_TYPE && AstUtil.isMethodNamed(call, ASSERT_EQUALS)) {
            findViolations(call)
        }
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        if (call.ownerType == ASSERT_TYPE && call.method == ASSERT_EQUALS) {
            findViolations(call)
        }
    }

    private void findViolations(def methodCall) {
        Expression actualArgument = getActualArgument(methodCall)
        if (actualArgument && AstUtil.isConstantOrConstantLiteral(actualArgument)) {
            addViolation(methodCall, VIOLATION_MESSAGE)
        }
    }

    private Expression getActualArgument(def methodCall) {
        List<? extends Expression> arguments = AstUtil.getMethodArguments(methodCall)
        int actualArgumentIndex = getActualArgumentIndex(arguments)
        return actualArgumentIndex == -1 ? null : arguments[actualArgumentIndex]
    }

    //see org.junit.Assert.assertEquals methods' signatures
    private int getActualArgumentIndex(List<? extends Expression> arguments) {
        def actualArgumentIndex = -1
        if (arguments.size() == 2) {
            actualArgumentIndex = 1
        } else if (arguments.size() == 4) {
            actualArgumentIndex = 2
        } else if (arguments.size() == 3) {
            if (arguments.first().type == ClassHelper.make(String)) {
                actualArgumentIndex = 2
            } else if (isConvertibleToDouble(arguments.first().type)) {
                actualArgumentIndex = 1
            }
        }
        actualArgumentIndex
    }

    private boolean isConvertibleToDouble(ClassNode classNode) {
        return classNode.typeClass in [int, Integer, long, Long, float, Float, double, Double, BigInteger, BigDecimal]
    }

    private static final String VIOLATION_MESSAGE = 'Found `assertEquals` with literal or constant `actual` parameter. ' +
        'Most likely it was intended to be the `expected` value.'

}
