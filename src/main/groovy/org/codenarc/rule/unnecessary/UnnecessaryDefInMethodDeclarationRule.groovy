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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * If a method has a visibility modifier or a type declaration, then the def keyword is unneeded.
 * For instance 'def private method() {}' is redundant and can be simplified to 'private method() {}'.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class UnnecessaryDefInMethodDeclarationRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryDefInMethodDeclaration'
    int priority = 3
    Class astVisitorClass = UnnecessaryDefInMethodDeclarationAstVisitor
}

class UnnecessaryDefInMethodDeclarationAstVisitor extends AbstractAstVisitor {
    @Override
    void visitMethodEx(MethodNode node) {
        String declaration = getDeclaration(node)

        if (contains(declaration, 'def')) {
            if (contains(declaration, 'private')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked private')
            } else if (contains(declaration, 'protected')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked protected')
            } else if (contains(declaration, 'public')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked public')
            } else if (contains(declaration, 'static')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked static')
            } else if (contains(declaration, 'final')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked final')
            } else if (contains(declaration, 'synchronized')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked synchronized')
            } else if (contains(declaration, 'abstract')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked abstract')
            } else if (contains(declaration, 'strictfp')) {
                addViolation(node, 'The def keyword is unneeded when a method is marked strictfp')
            } else if (contains(declaration, 'Object')) {
                addViolation(node, 'The def keyword is unneeded when a method returns the Object type')
            }
        }

        super.visitMethodEx(node)
    }

    private String getDeclaration(MethodNode node) {
        if ([node.lineNumber, node.lastLineNumber, node.columnNumber, node.lastColumnNumber].any{ it < 1 }) {
            return ''
        }

        String acc = ''
        for (lineIndex in (node.lineNumber-1 .. node.lastLineNumber-1)) {
            // the raw line is required to apply columnNumber and lastColumnNumber
            def line = AstUtil.getRawLine(sourceCode, lineIndex)

            // extract the relevant part of the first line
            if (lineIndex == node.lineNumber - 1) {
                int nonRelevantColumns = node.columnNumber - 1
                line = line.replaceFirst(".{$nonRelevantColumns}", ' ' * nonRelevantColumns) // retain the line length as it's important when using lastColumnNumber
            }

            // extract the relevant part of the last line
            if (lineIndex == node.lastLineNumber - 1) {
                def stopIndex = node.lastColumnNumber < line.size() ? node.lastColumnNumber - 2 : line.size() - 1
                line = line[0..stopIndex]
            }

            if (line.contains('{')) {
                acc += line[0..<line.indexOf('{')]
                break
            } else {
                acc += line + ' '
            }
        }
        acc
    }

    private boolean contains(String declaration, String modifier) {
        declaration?.startsWith(modifier) || declaration?.contains(' ' + modifier + ' ')
    }
}