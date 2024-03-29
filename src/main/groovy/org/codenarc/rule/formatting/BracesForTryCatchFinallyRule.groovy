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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks the location of the opening brace ({) for try statements, the location
 * of the 'catch' keyword and corresponding opening braces, and the location of the 'finally'
 * keyword and the corresponding opening braces. By default, requires opening braces on the
 * same line, but the sameLine property can be set to false to override this.
 *
 * By default does not validate catch and finally clauses, to turn this on set properties
 * validateCatch and validateFinally to true respectively. The catch and finally handling
 * defaults to using the sameLine value so if sameLine is true, we expect "} catch(x) {"
 * and "} finally {". For fine grained control, use boolean properties catchOnSameLineAsClosingBrace,
 * catchOnSameLineAsOpeningBrace, finallyOnSameLineAsClosingBrace, finallyOnSameLineAsOpeningBrace
 *
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Hamlet D'Arcy
 * @author <a href="mailto:mbjarland@gmail.com">Matias Bjarland</a>
 * @author Chris Mair
 */
class BracesForTryCatchFinallyRule extends AbstractAstVisitorRule {

    String name = 'BracesForTryCatchFinally'
    int priority = 2
    Class astVisitorClass = BracesForTryCatchFinallyAstVisitor
    boolean sameLine = true

    boolean validateCatch = false
    Boolean catchOnSameLineAsClosingBrace
    Boolean catchOnSameLineAsOpeningBrace

    boolean validateFinally = false
    Boolean finallyOnSameLineAsClosingBrace
    Boolean finallyOnSameLineAsOpeningBrace
}

class BracesForTryCatchFinallyAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {
        // If user has not explicitly set the catch brace settings, 'inherit' them from sameLine
        if (rule.catchOnSameLineAsClosingBrace == null) {
            rule.catchOnSameLineAsClosingBrace = rule.sameLine
        }
        if (rule.catchOnSameLineAsOpeningBrace == null) {
            rule.catchOnSameLineAsOpeningBrace = rule.sameLine
        }

        // If user has not explicitly set the finally brace settings, 'inherit' them from sameLine
        if (rule.finallyOnSameLineAsClosingBrace == null) {
            rule.finallyOnSameLineAsClosingBrace = rule.sameLine
        }
        if (rule.finallyOnSameLineAsOpeningBrace == null) {
            rule.finallyOnSameLineAsOpeningBrace = rule.sameLine
        }

        super.visitClassEx(node)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement node) {
        checkTryBlock(node)
        checkCatch(node)
        checkFinally(node)

        super.visitTryCatchFinally(node)
    }

    private void checkTryBlock(TryCatchStatement node) {
        if (isGeneratedCode(node.tryStatement)) {
            return
        }
        boolean isBraceOnSameLine = node.lineNumber == node.tryStatement.lineNumber

        if (rule.sameLine) {
            if (!isBraceOnSameLine) {
                addViolation(node, "Opening brace should be on the same line as 'try'")
            }
        } else {
            if (isBraceOnSameLine) {
                addViolation(node, "Opening brace should not be on the same line as 'try'")
            }
        }
    }

    private void checkCatch(TryCatchStatement node) {
        if (rule.validateCatch && node.catchStatements) {
            node.catchStatements.each { CatchStatement stmt ->
                def srcLine = sourceLineTrimmed(stmt)

                if (rule.catchOnSameLineAsClosingBrace && !srcLine?.contains('}')) {
                    addViolation(stmt, "'catch' should be on the same line as the closing brace")
                } else if (!rule.catchOnSameLineAsClosingBrace && srcLine?.contains('}')) {
                    addViolation(stmt, "'catch' should not be on the same line as the closing brace")
                }

                boolean isBraceOnSameLine = stmt.lineNumber == stmt.code.lineNumber
                if (rule.catchOnSameLineAsOpeningBrace && !isBraceOnSameLine) {
                    addViolation(stmt, "Opening brace should be on the same line as 'catch'")
                } else if (!rule.catchOnSameLineAsOpeningBrace && isBraceOnSameLine) {
                    addViolation(stmt, "Opening brace should not be on the same line as 'catch'")
                }
            }
        }
    }

    private void checkFinally(TryCatchStatement node) {
        if (rule.validateFinally && node.finallyStatement) {
            def stmt = node.finallyStatement
            def srcLine = sourceLineTrimmed(stmt)

            if (rule.finallyOnSameLineAsClosingBrace && srcLine && !srcLine?.contains('}')) {
                addViolation(stmt, "'finally' should be on the same line as the closing brace")
            } else if (!rule.finallyOnSameLineAsClosingBrace && srcLine?.contains('}')) {
                addViolation(stmt, "'finally' should not be on the same line as the closing brace")
            }

            if (rule.finallyOnSameLineAsOpeningBrace && srcLine && !srcLine?.contains('{')) {
                addViolation(stmt, "Opening brace should be on the same line as 'finally'")
            } else if (!rule.catchOnSameLineAsOpeningBrace && srcLine?.contains('}')) {
                addViolation(stmt, "Opening brace should not be on the same line as 'finally'")
            }
        }
    }
}
