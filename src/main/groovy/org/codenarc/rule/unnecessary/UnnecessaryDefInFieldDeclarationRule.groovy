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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

/**
 * If a field has a visibility modifier or a type declaration, then the def keyword is unneeded. For instance,
 * 'static def constraints = {}' is redundant and can be simplified to 'static constraints = {}.
 *
 * @author Hamlet D'Arcy
 */
class UnnecessaryDefInFieldDeclarationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryDefInFieldDeclaration'
    int priority = 3
    Class astVisitorClass = UnnecessaryDefInFieldDeclarationAstVisitor
}

class UnnecessaryDefInFieldDeclarationAstVisitor extends AbstractFieldVisitor {

    @Override
    void visitField(FieldNode node) {
        String declaration = AstUtil.getDeclaration(node, sourceCode)
        def definitionStart = declaration.indexOf('=')
        if (definitionStart != -1) {
            declaration = declaration[0 .. definitionStart - 1]
        }

        if (contains(declaration, 'def')) {
            if (contains(declaration, 'private')) {
                addViolation(node, 'The def keyword is unneeded when a field is marked private')
            } else if (contains(declaration, 'protected')) {
                addViolation(node, 'The def keyword is unneeded when a field is marked protected')
            } else if (contains(declaration, 'public')) {
                addViolation(node, 'The def keyword is unneeded when a field is marked public')
            } else if (contains(declaration, 'static')) {
                addViolation(node, 'The def keyword is unneeded when a field is marked static')
            } else if (contains(declaration, 'final')) {
                addViolation(node, 'The def keyword is unneeded when a field is marked final')
            } else if (contains(declaration, 'strictfp')) {
                addViolation(node, 'The def keyword is unneeded when a field is marked strictfp')
            } else if (contains(declaration, 'Object')) {
                addViolation(node, 'The def keyword is unneeded when a field is specified Object type')
            } else if (node.type != ClassHelper.DYNAMIC_TYPE) {
                addViolation(node, 'The def keyword is unneeded when a field type is specified')
            }
        }
    }

    private static boolean contains(String declaration, String modifier) {
        declaration?.startsWith(modifier) || declaration?.contains(' ' + modifier + ' ')
    }
}
