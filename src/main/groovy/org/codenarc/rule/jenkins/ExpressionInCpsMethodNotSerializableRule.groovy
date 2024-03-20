/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.serialization.SerializationUtil

/**
 * Every expression/variable in a CPS transformed method in Jenkins can potentially be serialized and should therefore implement the Serializable interface
 *
 * @author Daniel Zänker
 */
class ExpressionInCpsMethodNotSerializableRule extends AbstractAstVisitorRule {

    String name = 'ExpressionInCpsMethodNotSerializable'
    int priority = 2
    Class astVisitorClass = ExpressionInCpsMethodNotSerializableAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
}

class ExpressionInCpsMethodNotSerializableAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitConstructorOrMethod(MethodNode method, boolean isConstructor) {
        boolean isCpsContext = JenkinsUtil.isCpsMethod(method, isConstructor)
        if (isCpsContext) {
            super.visitConstructorOrMethod(method, isConstructor)
        }
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression declaration) {
        if (declaration.isMultipleAssignmentDeclaration()) {
            // handle something like: def (int i, String j) = [10, 'foo']
            TupleExpression tuple = declaration.tupleExpression
            if (tuple) {
                tuple.findAll { it instanceof VariableExpression && !SerializationUtil.isSerializableOrDynamicType(it.type) }
                    .each {
                        VariableExpression variable = (VariableExpression) it
                        addVariableViolation(variable, variable.name)
                    }
            }
        } else {
            VariableExpression variable = declaration.variableExpression

            if (variable && !SerializationUtil.isSerializableOrDynamicType(variable.type)) {
                addVariableViolation(variable, variable.name)
            }
        }
        super.visitDeclarationExpression(declaration)
    }

    private void addVariableViolation(ASTNode node, String name) {
        addViolation(node, "Variable ${name} is not Serializable and used in CPS transformed code")
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        if (!SerializationUtil.isSerializableOrDynamicType(forLoop.variableType)) {
            addVariableViolation(forLoop.variable, forLoop.variable.name)
        }
        if (!SerializationUtil.isSerializableOrDynamicType(forLoop.collectionExpression.type)) {
            addViolation(forLoop, 'The type of the collection that is iterated over is not Serializable and used in CPS transformed code')
        }
        super.visitForLoop(forLoop)
    }
}
