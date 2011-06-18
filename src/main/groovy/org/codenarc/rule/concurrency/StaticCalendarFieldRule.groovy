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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.util.AstUtil
import java.lang.reflect.Modifier

/**
 * Calendar objects should not be used as static fields. Calendars are inherently unsafe for multithreaded use. Sharing
 * a single instance across thread boundaries without proper synchronization will result in erratic behavior of the application.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class StaticCalendarFieldRule extends AbstractAstVisitorRule {
    String name = 'StaticCalendarField'
    int priority = 2
    Class astVisitorClass = StaticCalendarFieldAstVisitor
}

class StaticCalendarFieldAstVisitor extends AbstractAstVisitor {
    @Override
    void visitFieldEx(FieldNode node) {

        if (Modifier.isStatic(node.modifiers) && AstUtil.classNodeImplementsType(node.type, Calendar)) {
            addCalendarViolation(node, node.name)
        }
        else {
            if (Modifier.isStatic(node.modifiers) && node.initialValueExpression && isCalendarFactoryMethodCall(node.initialValueExpression)) {
                addCalendarViolation(node, node.name)
            }
        }
        super.visitFieldEx(node)
    }

    private boolean isCalendarFactoryMethodCall(expression) {
        AstUtil.isMethodCall(expression, 'Calendar', 'getInstance')
    }

    private void addCalendarViolation(node, String fieldName) {
        addViolation(node, "Calendar instances are not thread safe. Wrap the Calendar field $fieldName in a ThreadLocal or make it an instance field")
    }
}
