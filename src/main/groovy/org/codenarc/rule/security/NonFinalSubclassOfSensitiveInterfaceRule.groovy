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
package org.codenarc.rule.security

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier
import java.security.BasicPermission
import java.security.Permission
import java.security.PrivilegedAction
import java.security.PrivilegedActionException

/**
 * The permissions classes such as java.security.Permission and java.security.BasicPermission are designed to be extended.
 * Classes that derive from these permissions classes, however, must prohibit extension. This prohibition ensures that
 * malicious subclasses cannot change the properties of the derived class. Classes that implement sensitive interfaces
 * such as java.security.PrivilegedAction and java.security.PrivilegedActionException must also be declared final for
 * analogous reasons.
 *
 * @author Hamlet D'Arcy
  */
class NonFinalSubclassOfSensitiveInterfaceRule extends AbstractAstVisitorRule {
    String name = 'NonFinalSubclassOfSensitiveInterface'
    int priority = 2
    Class astVisitorClass = NonFinalSubclassOfSensitiveInterfaceAstVisitor
}

class NonFinalSubclassOfSensitiveInterfaceAstVisitor extends AbstractAstVisitor {
    @Override
    protected void visitClassEx(ClassNode node) {

        if (!Modifier.isFinal(node.modifiers)) {
            if (AstUtil.classNodeImplementsType(node, Permission)) {
                addViolation(node, "The class $node.nameWithoutPackage extends java.security.Permission but is not final")
            } else if (AstUtil.classNodeImplementsType(node, BasicPermission)) {
                addViolation(node, "The class $node.nameWithoutPackage extends java.security.BasicPermission but is not final")
            } else if (AstUtil.classNodeImplementsType(node, PrivilegedAction)) {
                addViolation(node, "The class $node.nameWithoutPackage implements java.security.PrivilegedAction but is not final")
            } else if (AstUtil.classNodeImplementsType(node, PrivilegedActionException)) {
                addViolation(node, "The class $node.nameWithoutPackage extends java.security.PrivilegedActionException but is not final")
            }
        }

        super.visitClassEx(node)
    }

}
