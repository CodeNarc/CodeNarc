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

import org.codehaus.groovy.ast.*
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import java.util.regex.Pattern

/**
 * The 'public' modifier is not required on methods, constructors or classes.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class UnnecessaryPublicModifierRule extends AbstractAstVisitorRule {

    String name = 'UnnecessaryPublicModifier'
    int priority = 3
    Class astVisitorClass = UnnecessaryPublicModifierAstVisitor
}

class UnnecessaryPublicModifierAstVisitor extends AbstractAstVisitor {

    private static final String REQUIRED_WHITESPACE = '\\s+'
    private static final String OPTIONAL_WHITESPACE = '\\s*'
    private static final String PUBLIC_REGEX = 'public' + REQUIRED_WHITESPACE

    @Override
    protected void visitClassEx(ClassNode node) {
        if (!node.isScript()) {
            def regex = 'class' + REQUIRED_WHITESPACE + Pattern.quote(node.name)
            checkDeclaration(node, regex, 'classes')
        }
        super.visitClassEx(node)
    }

    @Override
    void visitMethodEx(MethodNode node) {
        if (node.genericsTypes == null || node.genericsTypes.length == 0) {
            def regex = Pattern.quote(node.name) + OPTIONAL_WHITESPACE + '\\('
            checkDeclaration(node, regex, 'methods')
        }
        super.visitMethodEx(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        def regex = Pattern.quote(node.declaringClass.name) + OPTIONAL_WHITESPACE + '\\('
        checkDeclaration(node, regex, 'constructors')
        super.visitConstructor(node)
    }

    private void checkDeclaration(ASTNode node, String regex, String nodeType) {
        String declarationLine = findLineWithDeclaration(node, regex)
        if (declarationLine =~ PUBLIC_REGEX) {
            addViolation(node, "The public keyword is unnecessary for $nodeType")
        }
    }

    private String findLineWithDeclaration(ASTNode node, String regex) {
        if (node.lineNumber < 0) {
            return ''
        }

        def current = node.lineNumber - 1

        if (node instanceof AnnotatedNode && !node.annotations.empty) {
            // Start checking from the end of the last annotation
            def lastAnnotation = node.annotations.last()
            current = lastAnnotation.lastLineNumber - 1
        }

        while (current <= node.lastLineNumber) {
            def line = sourceCode.line(current)
            def matcher = line =~ regex
            if (matcher) {
                return line.substring(0, matcher.start())
            }
            current++
        }
        return ''
    }

}
