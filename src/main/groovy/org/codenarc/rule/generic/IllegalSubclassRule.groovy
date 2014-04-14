/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.generic

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

/**
 * Checks for classes that extend one of the specified set of illegal superclasses.
 *
 * @author Chris Mair
 */
class IllegalSubclassRule extends AbstractAstVisitorRule {

    String name = 'IllegalSubclass'
    int priority = 2
    Class astVisitorClass = IllegalSubclassAstVisitor
    String superclassNames

    boolean isReady() {
        superclassNames
    }
}

class IllegalSubclassAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {
        def wildcard = new WildcardPattern(rule.superclassNames)
        def superclassName = node.superClass?.name
        if (wildcard.matches(superclassName)) {
            addViolation(node, "The class $node.name extends from the illegal superclass $superclassName")
        }
        super.visitClassEx(node)
    }
}
