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
package org.codenarc.rule.logging

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor

import java.lang.reflect.Modifier

/**
 * Logger objects should be declared private, static and final.
 * The exception is, when derived classes should use logger objects. Then they should be declared protected, non-static and final.
 * This rule find loggers that are not declared with these modifiers.
 *
 * @author Hamlet D'Arcy
 * @author René Scheibe
  */
class LoggerWithWrongModifiersRule extends AbstractAstVisitorRule {

    String name = 'LoggerWithWrongModifiers'
    int priority = 2
    boolean allowProtectedLogger = false
    boolean allowNonStaticLogger = false
    Class astVisitorClass = LoggerWithWrongModifiersAstVisitor

}

class LoggerWithWrongModifiersAstVisitor extends AbstractFieldVisitor {

    @Override
    void visitField(FieldNode fieldNode) {
        if (LogUtil.isMatchingLoggerDefinition(fieldNode.getInitialExpression())) {
            int modifiers = fieldNode.modifiers

            boolean isPrivate = Modifier.isPrivate(modifiers)
            boolean isProtected = Modifier.isProtected(modifiers)
            boolean isFinal = Modifier.isFinal(modifiers)
            boolean isStatic = Modifier.isStatic(modifiers)

            if (!isPrivate && !rule.allowProtectedLogger) {
                addViolationForField(fieldNode)
            } else if ((!isPrivate && !isProtected) && rule.allowProtectedLogger) {
                addViolationForField(fieldNode)
            } else if (!isFinal) {
                addViolationForField(fieldNode)
            } else if (!isStatic && !rule.allowNonStaticLogger) {
                addViolationForField(fieldNode)
            } else if ((!isPrivate && !isProtected) && rule.allowProtectedLogger) {
                addViolationForField(fieldNode)
            }
        }
    }

    private addViolationForField(FieldNode fieldNode) {

        def visibility = rule.allowProtectedLogger ? 'private (or protected)' : 'private'
        def staticness = rule.allowNonStaticLogger ? '' : ', static'
        addViolation(fieldNode, "The Logger field $fieldNode.name should be ${visibility}${staticness} and final")
    }
}
