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
package org.codenarc.rule.exceptions

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for classes that extend Throwable. Custom exception classes should subclass Exception or one of its descendants.
 *
 * @author Chris Mair
 */
class ExceptionExtendsThrowableRule extends AbstractAstVisitorRule {

    String name = 'ExceptionExtendsThrowable'
    int priority = 2
    Class astVisitorClass = ExceptionExtendsThrowableAstVisitor
}

class ExceptionExtendsThrowableAstVisitor extends AbstractAstVisitor {

    private final ClassNode throwableClassNode = ClassHelper.make(Throwable)

    @Override
    protected void visitClassEx(ClassNode node) {
        if (node.isDerivedFrom(throwableClassNode) || node.superClass?.name == 'Throwable') {
            addViolation(node, "The class $node.name extends Throwable. Custom exception classes should subclass Exception or one of its descendants.")
        }
        super.visitClassEx(node)
    }

}
