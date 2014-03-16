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
import org.codenarc.rule.AbstractMethodVisitor
import org.codehaus.groovy.ast.MethodNode
import java.lang.reflect.Modifier
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.ClassNode

/**
 * Rule that checks if a JUnit 4 test class contains public, instance, void, no-arg methods
 * named test*() that are NOT annotated with @Test.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
  */
class JUnitLostTestRule extends AbstractAstVisitorRule {

    String name = 'JUnitLostTest'
    int priority = 2
    Class astVisitorClass = JUnitLostTestAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitLostTestAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitClass(ClassNode node) {
        def imports = sourceCode.ast.imports + sourceCode.ast.starImports
        def junit4TestClass = imports.find { importNode ->
            def importName = importNode.className ?: importNode.packageName
            importName.startsWith('org.junit.')
        }

        if (junit4TestClass) {
            super.visitClass(node)
        }
    }

    @Override
    void visitMethod(MethodNode methodNode) {
        if (Modifier.isPublic(methodNode.modifiers)
            && (methodNode.isVoidMethod())
            && (methodNode.parameters?.length == 0)
            && (methodNode.name?.startsWith('test'))
            && !(Modifier.isStatic(methodNode.modifiers))
            && !AstUtil.hasAnnotation(methodNode, 'Test') ) {

                addViolation(methodNode, "The method ${methodNode.name} is a public, instance, void, no-arg method  named test*() that is not annotated with @Test.")
        }
    }

}
