/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codenarc.rule.AbstractAstVisitor

/**
 * Abstract superclass for AST Visitor classes that check for conditions within a finally block
 *
 * @author Chris Mair
 */
abstract class AbstractFinallyAstVisitor extends AbstractAstVisitor {

    private final finallyLineRanges = []

    // Known pathology: if there is another statement on the same line as the beginning or
    // end of the finally block, but outside the block.

    void visitTryCatchFinally(TryCatchStatement tryCatchStatement) {
        if (tryCatchStatement.finallyStatement) {
            def f = tryCatchStatement.finallyStatement
            finallyLineRanges.add(f.lineNumber..f.lastLineNumber)
        }
        super.visitTryCatchFinally(tryCatchStatement)
    }

    /**
     * @return true if the specified statement is within a finally block
     */
    protected boolean isStatementWithinFinally(Statement statement) {
        finallyLineRanges.find { statement.lineNumber in it }
    }

}
