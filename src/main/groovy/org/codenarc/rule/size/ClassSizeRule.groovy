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
package org.codenarc.rule.size

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation
import org.codenarc.util.AstUtil

/**
 * Rule that checks the size of a class.
 * <p/>
 * The <code>maxLines</code> property holds the threshold value for the maximum number of lines. A
 * class length (number of lines) greater than that value is considered a violation. The
 * <code>maxLines</code> property defaults to 1000.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class ClassSizeRule extends AbstractAstVisitorRule {
    String name = 'ClassSize'
    int priority = 3
    Class astVisitorClass = ClassSizeAstVisitor
    int maxLines = 1000
}

class ClassSizeAstVisitor extends AbstractAstVisitor  {
    void visitClassEx(ClassNode classNode) {
        if (!AstUtil.isFromGeneratedSourceCode(classNode)) {
            def numLines = classNode.lastLineNumber - classNode.lineNumber + 1
            if (numLines > rule.maxLines) {
                def className = classNode.name
                def classNameNoPackage = className[(className.lastIndexOf('.') + 1) .. -1]
                violations.add(new Violation(rule:rule, lineNumber:classNode.lineNumber,
                        message:"""Class "$classNameNoPackage" is $numLines lines"""))
            }
        }
        super.visitClassEx(classNode)
    }

}
