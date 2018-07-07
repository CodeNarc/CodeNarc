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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * When working with Dates and times perfer the java.time.* packages instead of java.util.Date()
 *
 * @author Eric Helgeson
 */
class NoUtilDateRule extends AbstractAstVisitorRule {
    String name = 'NoUtilDateRule'
    int priority = 2
    Class astVisitorClass = NoUtilDateAstVisitor
}

class NoUtilDateAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (isFirstVisit(call) && call.type.name in ['Date', 'java.util.Date']) {
            addViolation(call, 'Created an instance of java.util.Date(), prefer java.time.* package.')
        }

        super.visitConstructorCallExpression(call)
    }
}
