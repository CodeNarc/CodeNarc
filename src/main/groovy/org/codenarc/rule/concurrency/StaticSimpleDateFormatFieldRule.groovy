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
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier
import java.text.SimpleDateFormat

/**
 * SimpleDateFormat objects should not be used as static fields. SimpleDateFormat are inherently unsafe for
 * multi-threaded use. Sharing a single instance across thread boundaries without proper synchronization will
 * result in erratic behavior of the application.
 *
 * @author Chris Mair
 */
class StaticSimpleDateFormatFieldRule extends AbstractAstVisitorRule {
    String name = 'StaticSimpleDateFormatField'
    int priority = 2
    Class astVisitorClass = StaticSimpleDateFormatFieldAstVisitor
}

class StaticSimpleDateFormatFieldAstVisitor extends AbstractFieldVisitor {

    @Override
    void visitField(FieldNode node) {
        if (Modifier.isStatic(node.modifiers) && AstUtil.classNodeImplementsType(node.type, SimpleDateFormat)) {
            addSimpleDateFormatViolation(node, node.name)
        }
        else {
            if (Modifier.isStatic(node.modifiers) && node.initialValueExpression && isSimpleDateFormatConstructorCall(node.initialValueExpression)) {
                addSimpleDateFormatViolation(node, node.name)
            }
        }
    }

    private static boolean isSimpleDateFormatConstructorCall(Expression expression) {
        AstUtil.isConstructorCall(expression, ['SimpleDateFormat', 'java.text.SimpleDateFormat'])
    }

    private void addSimpleDateFormatViolation(FieldNode node, String fieldName) {
        addViolation(node, "SimpleDateFormat instances are not thread safe. Wrap the SimpleDateFormat field $fieldName in a ThreadLocal or make it an instance field")
    }
}
