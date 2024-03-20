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

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Closures are CPS transformed in Jenkins and will cause an error at runtime when used in GStrings. Typically, they can be replaced by variable interpolation
 *
 * @author Daniel Zänker
 */
class ClosureInGStringRule extends AbstractAstVisitorRule {

    String name = 'ClosureInGString'
    int priority = 1
    Class astVisitorClass = ClosureInGStringAstVisitor
    String applyToFileNames = 'Jenkinsfile'
}

class ClosureInGStringAstVisitor extends AbstractAstVisitor {

    @Override
    void visitGStringExpression(GStringExpression gString) {
        for (Expression value in gString.values) {
            if (value instanceof ClosureExpression) {
                addViolation(value, 'GString contains a closure. Use variable interpolation instead')
            }
        }
    }
}
