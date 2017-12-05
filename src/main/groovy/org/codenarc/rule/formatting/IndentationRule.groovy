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

import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
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
    int spacesPerIndentLevel = 4

}

class IndentationAstVisitor extends AbstractAstVisitor {

    private int indentLevel = 0

    @Override
    protected void visitClassEx(ClassNode node) {
        indentLevel = nestingLevelForClass(node)

        boolean isInnerClass = node instanceof InnerClassNode
        println "visitClassEX: $node; isInnerClass=$isInnerClass; class=${node.class}"
        boolean isAnonymous = isInnerClass && node.anonymous
        if (!isAnonymous) {
            int expectedColumn = columnForIndentLevel(indentLevel)
            if (node.columnNumber != expectedColumn) {
                addViolation(node, "The class ${node.getNameWithoutPackage()} is at the incorrect indent level: Expected column $expectedColumn but was ${node.columnNumber}")
            }
        }
        indentLevel++
        super.visitClassEx(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        int expectedColumn = columnForIndentLevel(indentLevel)
        if (node.columnNumber != expectedColumn) {
            addViolation(node, "The method ${node.name} in class ${currentClassName} is at the incorrect indent level: Expected column $expectedColumn but was ${node.columnNumber}")
        }

        super.visitMethodEx(node)
    }

    private int nestingLevelForClass(ClassNode node) {
        // If this is a nested class, then add one to the outer class level
        int level = node.outerClass ? nestingLevelForClass(node.outerClass) + 1 : 0

        // If this class is defined within a method, add one to the level
        level += node.enclosingMethod ? 1 : 0

        return level
    }

    private int columnForIndentLevel(int indentLevel) {
        return indentLevel * rule.spacesPerIndentLevel + 1
    }

}
