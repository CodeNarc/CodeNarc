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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation

/**
 * Rule that verifies that the package name of a class matches a regular expression. By default it checks that the
 * package name consists of only lowercase letters and numbers, separated by periods.
 * <p/>
 * The <code>regex</code> property specifies the regular expression to check the package name against. It is
 * required and cannot be null or empty. It defaults to '[a-z]+[a-z0-9]*(\.[a-z0-9]+)*'.
 * <p/>
 * The <code>packageNameRequired</code> property indicates whether a package name declaration is required for
 * all classes. It defaults to false.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class PackageNameRule extends AbstractAstVisitorRule {
    String name = 'PackageName'
    int priority = 2
    Class astVisitorClass = PackageNameAstVisitor
    String regex = /[a-z]+[a-z0-9]*(\.[a-z0-9]+)*/
    boolean packageNameRequired = false
}

class PackageNameAstVisitor extends AbstractAstVisitor  {
    void visitClassEx(ClassNode classNode) {
        assert rule.regex

        if (classNode.packageName != null && !(classNode.packageName ==~ rule.regex)) {
            violations.add(new Violation(rule:rule, message:"package=$classNode.packageName"))
        }

        if (rule.packageNameRequired && classNode.packageName == null) {
            addViolation(classNode, 'Required package declaration is missing for class')
        }
        super.visitClassEx(classNode)
    }

}
