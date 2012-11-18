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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.stmt.IfStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks the location of the opening brace ({) for if statements and optionally closing and opening braces
 * for else statements. By default, requires them on the same line, but the sameLine property can be set to
 * false to override this. To enable else checking, set the validateElse property to true. For more fine grained
 * control, use boolean properties elseOnSameLineAsClosingBrace and elseOnSameLineAsOpeningBrace
 *
 * Example:
 *
 * <pre>
 * if (x) {     //A
 *
 * } else if (y) { //B
 *
 * } else {    //C
 *
 * }
 * </pre>
 *
 * A - checked and defaults to no errors since the brace is on the same line <br />
 * B - checked if option validateElse is set to true. Validation result controlled by boolean
 * options elseOnSameLineAsClosingBrace and elseOnSameLineAsOpeningBrace
 * C - same as B
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author <a href="mailto:mbjarland@gmail.com">Matias Bjarland</a>
 */
class BracesForIfElseRule extends AbstractAstVisitorRule {
    String name = 'BracesForIfElse'
    int priority = 2
    Class astVisitorClass = BracesForIfElseAstVisitor
    boolean sameLine = true

    boolean validateElse = false
    Boolean elseOnSameLineAsClosingBrace
    Boolean elseOnSameLineAsOpeningBrace

}

class BracesForIfElseAstVisitor extends AbstractAstVisitor {

    @Override
    void visitIfElse(IfStatement node) {
        BracesForIfElseRule myRule = rule as BracesForIfElseRule

        if (isFirstVisit(node) && AstUtil.isBlock(node.ifBlock)) {
            if (myRule.sameLine) {
                if(!lastSourceLineTrimmed(node.booleanExpression)?.contains('{')) {
                    addViolation(node, "Opening brace should be on the same line as 'if'")
                }
            } else {
                if(lastSourceLineTrimmed(node.booleanExpression)?.contains('{')) {
                    addViolation(node, "Opening brace should not be on the same line as 'if'")
                }
            }
        }

        visitElse(myRule, node)

        super.visitIfElse(node)
    }

    void visitElse(BracesForIfElseRule myRule, IfStatement node) {
        //TODO: Understand isFirstVisit and apply them as appropriate to the below block
        if (myRule.validateElse && node.elseBlock) {
            //if user has not explicitly set the else brace settings, 'inherit' them from sameLine
            if (myRule.elseOnSameLineAsClosingBrace == null) {
                myRule.elseOnSameLineAsClosingBrace = myRule.sameLine
            }
            if (myRule.elseOnSameLineAsOpeningBrace == null) {
                myRule.elseOnSameLineAsOpeningBrace = myRule.sameLine
            }

            def srcLine = sourceLineTrimmed(node.elseBlock)

            visitElseClosingBrace(myRule, node, srcLine)
            visitElseOpeningBrace(myRule, node, srcLine)
        }
    }

    void visitElseClosingBrace(BracesForIfElseRule myRule, IfStatement node, String srcLine) {
        //only test for else closing curlies if the if statement has curlies to test for (i.e. is not one-line)
        if (AstUtil.isBlock(node.ifBlock)) {
            if (myRule.elseOnSameLineAsClosingBrace && srcLine && !(srcLine?.contains('else') && srcLine?.contains('}'))) {
                addViolation(node.elseBlock, "'else' should be on the same line as the closing brace")
            } else if (!myRule.elseOnSameLineAsClosingBrace && srcLine?.contains('else') && srcLine?.contains('}')) {
                addViolation(node.elseBlock, "'else' should not be on the same line as the closing brace")
            }
        }
    }

    void visitElseOpeningBrace(BracesForIfElseRule myRule, IfStatement node, String srcLine) {
        //only test for else opening curlies if the else statement has curlies to test for (i.e. is not one-line)
        if (AstUtil.isBlock(node.elseBlock)) {
            if (myRule.elseOnSameLineAsOpeningBrace && srcLine && !(srcLine?.contains('else') && srcLine?.contains('{'))) {
                addViolation(node.elseBlock, "Opening brace should be on the same line as 'else'")
            } else if (!myRule.elseOnSameLineAsOpeningBrace && srcLine?.contains('else') && srcLine?.contains('{')) {
                addViolation(node.elseBlock, "Opening brace should not be on the same line as 'else'")
            }
        }
    }
}
