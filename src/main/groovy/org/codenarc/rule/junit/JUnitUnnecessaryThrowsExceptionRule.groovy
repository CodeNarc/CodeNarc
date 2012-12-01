/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.util.AstUtil

/**
 * Check for throws clauses on JUnit test methods. That is not necessary in Groovy.
 *
 * This rule sets the default value of the <applyToClassNames> property to only match class names
 * ending in 'Test', 'Tests' or 'TestCase'.
 *
 * @author Chris Mair
 */
class JUnitUnnecessaryThrowsExceptionRule extends AbstractAstVisitorRule {

    String name = 'JUnitUnnecessaryThrowsException'
    int priority = 3
    Class astVisitorClass = JUnitUnnecessaryThrowsExceptionAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitUnnecessaryThrowsExceptionAstVisitor extends AbstractAstVisitor {

    private static final JUNIT4_ANNOTATIONS = ['Test', 'Before', 'BeforeClass', 'AfterClass', 'After', 'Ignore']

    @Override
    protected void visitMethodEx(MethodNode node) {
        if (node.exceptions && node.parameters.size() == 0 && node.isPublic() && !node.isStatic() && node.isVoidMethod() &&
                (isJUnit3MatchingMethod(node) || hasJUnit4Annotation(node)) )  {
            addViolation(node, "The ${node.name} method in class $currentClassName declares thrown exceptions, which is not necessary")
        }
        super.visitMethodEx(node)
    }

    private boolean isJUnit3MatchingMethod(MethodNode node) {
        return node.name.startsWith('test') || node.name in ['setUp', 'tearDown']
    }

    private boolean hasJUnit4Annotation(MethodNode node) {
        return JUNIT4_ANNOTATIONS.find { annotation -> AstUtil.hasAnnotation(node, annotation) }
    }

}
