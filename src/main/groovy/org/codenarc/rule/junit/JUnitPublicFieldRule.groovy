/*
 * Copyright 2013 the original author or authors.
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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

/**
 * Checks for public field on a JUnit test class. Ignores fields with the @Rule annotation.
 * <p/>
 * This rule ignores interfaces.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
 */
class JUnitPublicFieldRule extends AbstractAstVisitorRule {
    String name = 'JUnitPublicField'
    int priority = 3
    Class astVisitorClass = JUnitPublicFieldAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitPublicFieldAstVisitor extends AbstractFieldVisitor {

    @Override
    void visitClass(ClassNode node) {
        if (!node.isInterface()) {
            super.visitClass(node)
        }
    }

    @Override
    void visitField(FieldNode node) {
        if (node.isPublic() && !AstUtil.hasAnnotation(node, 'Rule') ) {
            addViolation(node, "The field $node.name is public. There is usually no reason to have a public field (even a constant) on a test class.")
        }
    }
}
