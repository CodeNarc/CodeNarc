/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.rule.AbstractAstVisitor

/**
 * Abstract superclass for AstVisitor classes that need to determine whether a Statement is
 * the last statement within a block.
 *
 * @author Chris Mair
  */
abstract class AbstractLastStatementInBlockAstVisitor extends AbstractAstVisitor {
    
    private final lastStatements = [] as Set

    protected boolean isLastStatementInBlock(Statement statement) {
        lastStatements.contains(statement)
    }

    void visitBlockStatement(BlockStatement block) {
        if (block.statements) {
            lastStatements << block.statements.last()
        }
        super.visitBlockStatement(block)
    }

}
