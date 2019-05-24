/*
 * Copyright 2019 the original author or authors.
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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Enforces classes are annotated either with one of the CompileStatic, GrailsCompileStatic or CompileDynamic
 * annotations
 *
 * @Author Sudhir Nimavat
 */
class CompileStaticRule extends AbstractAstVisitorRule {

    int priority = 2
    String name = 'CompileStatic'
    Class astVisitorClass = CompileStaticlVisitor
}

class CompileStaticlVisitor extends AbstractAstVisitor {

    private static final String GRAILS_COMPILE_STATIC = 'GrailsCompileStatic'
    private static final String COMPILE_STATIC = 'CompileStatic'
    private static final String COMPILE_DYNAMIC = 'CompileDynamic'

    private static final String ERROR_MSG = 'Class should be marked with one of @GrailsCompileStatic, @CompileStatic or @CompileDynamic'

    @Override
    void visitClassEx(ClassNode classNode) {
        boolean isExplicitlyMarked = false

        if (!classNode.isInterface() && !(classNode instanceof InnerClassNode)) {
            for (AnnotationNode annotationNode : classNode.annotations) {
                String annotation = annotationNode.classNode.text
                if (annotation in [GRAILS_COMPILE_STATIC, COMPILE_STATIC, COMPILE_DYNAMIC]) {
                    isExplicitlyMarked = true
                }
            }

            if (!isExplicitlyMarked) {
                addViolation(classNode, ERROR_MSG)
            }
        }

        super.visitClassEx(classNode)
    }

}
