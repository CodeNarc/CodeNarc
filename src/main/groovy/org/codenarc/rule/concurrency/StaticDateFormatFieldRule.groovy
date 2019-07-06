/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier
import java.text.DateFormat

/**
 * DateFormat objects should not be used as static fields. DateFormat are inherently unsafe for multithreaded use. Sharing a
 * single instance across thread boundaries without proper synchronization will result in erratic behavior of the application.
 *
 * @author 'Hamlet D'Arcy'
 * @author Chris Mair
 */
class StaticDateFormatFieldRule extends AbstractAstVisitorRule {
    String name = 'StaticDateFormatField'
    int priority = 2
    Class astVisitorClass = StaticDateFormatFieldAstVisitor
}

class StaticDateFormatFieldAstVisitor extends AbstractFieldVisitor {

    @Override
    void visitField(FieldNode node) {
        if (Modifier.isStatic(node.modifiers) && AstUtil.classNodeImplementsType(node.type, DateFormat)) {
            addDateFormatViolation(node, node.name)
        }
        else {
            if (Modifier.isStatic(node.modifiers) && node.initialValueExpression && isDateFormatFactoryMethodCall(node.initialValueExpression)) {
                addDateFormatViolation(node, node.name)
            }
        }
    }

    private static boolean isDateFormatFactoryMethodCall(Expression expression) {
        expression instanceof MethodCallExpression &&
            AstUtil.isMethodCall(expression, ['DateFormat', /java\.text\.DateFormat/], ['getDateInstance', 'getDateTimeInstance', 'getTimeInstance'])
    }

    private void addDateFormatViolation(FieldNode node, String fieldName) {
        addViolation(node, "DateFormat instances are not thread safe. Wrap the DateFormat field $fieldName in a ThreadLocal or make it an instance field")
    }
}
