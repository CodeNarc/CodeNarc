/*
 * Copyright 2009 the original author or authors.
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

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The class has an empty instance initializer. It can safely by removed. 
 *
 * @author 'Hamlet D'Arcy'
 */
class EmptyInstanceInitializerRule extends AbstractAstVisitorRule {
    String name = 'EmptyInstanceInitializer'
    int priority = 2
    Class astVisitorClass = EmptyInstanceInitializerAstVisitor
}

class EmptyInstanceInitializerAstVisitor extends AbstractAstVisitor {
    @Override
    protected void visitObjectInitializerStatements(ClassNode node) {
        if (node.objectInitializerStatements.size() == 1 && AstUtil.isEmptyBlock(node.objectInitializerStatements[0])) {
            def emptyBlock = AstUtil.getEmptyBlock(node.objectInitializerStatements[0])
            if (emptyBlock) {
                addViolation(emptyBlock, "The class $node.name defines an empty instance initializer. It is safe to delete it")
            }
        }
        super.visitObjectInitializerStatements(node)
    }
}
