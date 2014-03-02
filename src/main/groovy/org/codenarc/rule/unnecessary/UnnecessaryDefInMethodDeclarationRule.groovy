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
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * If a method has a visibility modifier or a type declaration, then the def keyword is unneeded.
 * For instance 'def private method() {}' is redundant and can be simplified to 'private method() {}'.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryDefInMethodDeclarationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryDefInMethodDeclaration'
    int priority = 3
    Class astVisitorClass = UnnecessaryDefInMethodDeclarationAstVisitor
}

class UnnecessaryDefInMethodDeclarationAstVisitor extends AbstractAstVisitor {

    private static final PATTERNS_OF_DISTRACTING_DECLARATION_PARTS = [
        "'", // method name with single quotes, e.g.: def 'some method'() { ... }
        '"', // method name with double quotes, e.g.: def "some method"() { ... }
        '(', // method with parameters, e.g.: def method(def x) { ... }
    ]

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        String declaration = removeDistractingParts(AstUtil.getDeclaration(node, sourceCode))

        if (contains(declaration, 'def') && !declaration.contains('<')) {
            if (isConstructor) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded on constructors")
            } else if (contains(declaration, 'private')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked private")
            } else if (contains(declaration, 'protected')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked protected")
            } else if (contains(declaration, 'public')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked public")
            } else if (contains(declaration, 'static')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked static")
            } else if (contains(declaration, 'final')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked final")
            } else if (contains(declaration, 'synchronized')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked synchronized")
            } else if (contains(declaration, 'abstract')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked abstract")
            } else if (contains(declaration, 'strictfp')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method is marked strictfp")
            } else if (contains(declaration, 'Object')) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method returns the Object type")
            } else if (node.returnType != ClassHelper.DYNAMIC_TYPE) {
                addViolation(node, "Violation in class $currentClassName. The def keyword is unneeded when a method specifies a return type")
            }
        }

        super.visitConstructorOrMethod(node, isConstructor)
    }

    private static removeDistractingParts(declaration) {
        def resultDeclaration = declaration
        for (pattern in PATTERNS_OF_DISTRACTING_DECLARATION_PARTS) {
            if (resultDeclaration.contains(pattern)) {
                resultDeclaration = resultDeclaration[0..<resultDeclaration.indexOf(pattern)]
            }
        }
        resultDeclaration
    }

    private static boolean contains(String declaration, String modifier) {
        declaration.startsWith(modifier) || declaration.contains(' ' + modifier + ' ')
    }
}
