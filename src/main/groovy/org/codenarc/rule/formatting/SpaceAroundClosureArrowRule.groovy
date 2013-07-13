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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks that there is whitespace around the closure arrow (->) symbol
 *
 * @author Chris Mair
 */
class SpaceAroundClosureArrowRule extends AbstractAstVisitorRule {
    String name = 'SpaceAroundClosureArrow'
    int priority = 3
    Class astVisitorClass = SpaceAroundClosureArrowAstVisitor
}

class SpaceAroundClosureArrowAstVisitor extends AbstractAstVisitor {

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        def line = sourceLine(expression) + ' '     // add trailing space in case -> is at end of line
        if (line.contains('->') && !(line =~ /\s\-\>\s/)) {
            addViolation(expression, "The closure arrow (->) within class $currentClassName is not surrounded by a space or whitespace")
                //"The operator \"?\" within class $currentClassName is not followed by a space or whitespace")
        }
        super.visitClosureExpression(expression)
    }
}
