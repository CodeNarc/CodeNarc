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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation

/**
 * Rule that checks the size of a method.
 * <p/>
 * The <code>maxLines</code> property holds the threshold value for the maximum number of lines. A
 * method length (number of lines) greater than that value is considered a violation. The
 * <code>maxLines</code> property defaults to 100.   
 *
 * @author Chris Mair
 * @version $Revision: 24 $ - $Date: 2009-01-31 07:47:09 -0500 (Sat, 31 Jan 2009) $
 */
class MethodSizeRule extends AbstractAstVisitorRule {
    String name = 'MethodSize'
    int priority = 3
    Class astVisitorClass = MethodSizeAstVisitor
    int maxLines = 100
}

class MethodSizeAstVisitor extends AbstractAstVisitor  {
    void visitMethod(MethodNode methodNode) {
        if (methodNode.lineNumber >= 0) {
            def numLines = methodNode.lastLineNumber - methodNode.lineNumber + 1
            if (numLines > rule.maxLines) {
                def methodName = methodNode.name
                violations.add(new Violation(rule:rule, lineNumber:methodNode.lineNumber, description:"""Method "$methodName" is $numLines lines"""))
            }
        }
        super.visitMethod(methodNode)
    }

}