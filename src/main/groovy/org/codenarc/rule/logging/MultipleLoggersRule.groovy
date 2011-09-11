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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor

/**
 * This rule catches classes that have more than one logger object defined. Typically, a class has zero or one logger objects. 
 *
 * @author 'Hamlet D'Arcy'
  */
class MultipleLoggersRule extends AbstractAstVisitorRule {
    String name = 'MultipleLoggers'
    int priority = 2
    Class astVisitorClass = MultipleLoggersAstVisitor
}

class MultipleLoggersAstVisitor extends AbstractFieldVisitor {

    Map<ClassNode, List<String>> classNodeToFieldNames = [:]

    @Override
    void visitField(FieldNode fieldNode) {
        if (LogUtil.isMatchingLoggerDefinition(fieldNode.getInitialExpression())) {

            List<String> logFields = classNodeToFieldNames[fieldNode.declaringClass]
            if (logFields) {
                logFields.add(fieldNode.name)
                addViolation(fieldNode, 'The class defines multiple loggers: ' + logFields.join(', '))
            } else {
                classNodeToFieldNames[fieldNode.declaringClass] = [fieldNode.name]
            }
        }
    }
}
