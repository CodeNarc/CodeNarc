/*
 * Copyright 2017 the original author or authors.
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
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Check indentation for class and method declarations
 *
 * @author Chris Mair
 */
class IndentationRule extends AbstractAstVisitorRule {

    String name = 'Indentation'
    int priority = 3
    Class astVisitorClass = IndentationAstVisitor
    String singleIndentLevel = '    '

    void setSpacesPerIndentLevel(int numSpaces) {
        this.singleIndentLevel = ' ' * numSpaces
    }
}

class IndentationAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {
        if (node.columnNumber != 1) {
            addViolation(node, "The class ${node.getNameWithoutPackage()} is at the incorrect indent level")
        }

        super.visitClassEx(node)
    }

}
