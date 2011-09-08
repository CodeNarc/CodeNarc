/*
 * Copyright 2009 the original author or authors.
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

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * This rule traps classes named exception that do not inherit from exception.
 *
 * @author Hamlet D'Arcy
  */
class ConfusingClassNamedExceptionRule extends AbstractAstVisitorRule {
    String name = 'ConfusingClassNamedException'
    int priority = 2
    Class astVisitorClass = ConfusingClassNamedExceptionAstVisitor
}

class ConfusingClassNamedExceptionAstVisitor extends AbstractAstVisitor {

    void visitClassEx(ClassNode node) {

        if (node.name.endsWith('Exception') && !AstUtil.classNodeImplementsType(node, Exception)) {
            if (!(node.superClass.name == 'Throwable') && !node.superClass.name.endsWith('Exception')) {
                addViolation node, "Found a class named $node.name that does not extend Exception."
            }
        }
        super.visitClassEx(node)
    }

}
