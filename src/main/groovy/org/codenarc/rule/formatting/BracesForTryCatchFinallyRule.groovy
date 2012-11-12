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
    void visitTryCatchFinally(TryCatchStatement node) {
        BracesForTryCatchFinallyRule myRule = rule as BracesForTryCatchFinallyRule
        if (myRule.sameLine) {
            if(!sourceLineTrimmed(node)?.contains('{')) {
                addViolation(node, "Opening brace should be on the same line as 'try'")
            }
        } else {
            if(sourceLineTrimmed(node)?.contains('{')) {
                addViolation(node, "Opening brace should not be on the same line as 'try'")
            }
        }
        
        visitCatch(myRule, node)
        visitFinally(myRule, node)
        
        super.visitTryCatchFinally(node)
    }
    
    void visitCatch(BracesForTryCatchFinallyRule myRule, TryCatchStatement node) {
        //TODO: Understand AstUtil.isBlock and isFirstVisit and apply them as appropriate to the below block
        if (myRule.validateCatch && node.catchStatements) {
            //if user has not explicitly set the catch brace settings, 'inherit' them from sameLine
            if (myRule.catchOnSameLineAsClosingBrace == null) {
                myRule.catchOnSameLineAsClosingBrace = myRule.sameLine
            }
            if (myRule.catchOnSameLineAsOpeningBrace == null) {
                myRule.catchOnSameLineAsOpeningBrace = myRule.sameLine
            }

            node.catchStatements.each { CatchStatement stmt ->
                def srcLine = sourceLineTrimmed(stmt)

                if (myRule.catchOnSameLineAsClosingBrace && !srcLine?.contains('}')) {
                    addViolation(stmt, "'catch' should be on the same line as the closing brace")
                } else if (!myRule.catchOnSameLineAsClosingBrace && srcLine?.contains('}')) {
                    addViolation(stmt, "'catch' should not be on the same line as the closing brace")
                }

                if (myRule.catchOnSameLineAsOpeningBrace && !srcLine?.contains('{')) {
                    addViolation(stmt, "Opening brace should be on the same line as 'catch'")
                } else if (!myRule.catchOnSameLineAsOpeningBrace && srcLine?.contains('}')) {
                    addViolation(stmt, "Opening brace should not be on the same line as 'catch'")
                }
            }
        }    
    }
    
    void visitFinally(BracesForTryCatchFinallyRule myRule, TryCatchStatement node) {
        //TODO: Understand AstUtil.isBlock and isFirstVisit and apply them as appropriate to the below block
        //if user has not explicitly set the finally brace settings, 'inherit' them from sameLine
        if (myRule.finallyOnSameLineAsClosingBrace == null) {
            myRule.finallyOnSameLineAsClosingBrace = myRule.sameLine
        }
        if (myRule.finallyOnSameLineAsOpeningBrace == null) {
            myRule.finallyOnSameLineAsOpeningBrace = myRule.sameLine
        }

        if (myRule.validateFinally && node.finallyStatement) {
            def stmt = node.finallyStatement
            def srcLine = sourceLineTrimmed(stmt)

            if (myRule.finallyOnSameLineAsClosingBrace && srcLine && !srcLine?.contains('}')) {
                addViolation(stmt, "'finally' should be on the same line as the closing brace")
            } else if (!myRule.finallyOnSameLineAsClosingBrace && srcLine?.contains('}')) {
                addViolation(stmt, "'finally' should not be on the same line as the closing brace")
            }

            if (myRule.finallyOnSameLineAsOpeningBrace && srcLine && !srcLine?.contains('{')) {
                addViolation(stmt, "Opening brace should be on the same line as 'finally'")
            } else if (!myRule.catchOnSameLineAsOpeningBrace && srcLine?.contains('}')) {
                addViolation(stmt, "Opening brace should not be on the same line as 'finally'")
            }
        }
    }
}
