/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Do not use java.util.Date. Prefer the classes in the java.time.* packages. This rule checks for
 * construction of new java.util.Date objects.
 *
 * @author Eric Helgeson
 * @author Chris Mair
 */
class NoJavaUtilDateRule extends AbstractAstVisitorRule {

    String name = 'NoJavaUtilDate'
    int priority = 2
    Class astVisitorClass = NoJavaUtilDateAstVisitor
}

class NoJavaUtilDateAstVisitor extends AbstractAstVisitor {

    protected static final String VIOLATION_MESSAGE = 'Do not use java.util.Date. Prefer the classes in the java.time.* packages.'

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call) && call.type.name in ['Date', 'java.util.Date']) {
            addViolation(call, VIOLATION_MESSAGE)
        }

        super.visitConstructorCallExpression(call)
    }

}
