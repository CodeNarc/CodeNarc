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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Synchronization on a String field can lead to deadlock because Strings are interned by the JVM and can be shared. 
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class SynchronizedOnStringRule extends AbstractAstVisitorRule {
    String name = 'SynchronizedOnString'
    int priority = 2
    Class astVisitorClass = SynchronizedOnStringAstVisitor
}

class SynchronizedOnStringAstVisitor extends AbstractAstVisitor {

    private final static Map<ClassNode, List<String>> CLASSNODE_TO_STRING_FIELDS = [:]
    private final static Map<ClassNode, List<String>> CLASSNODE_TO_OTHER_FIELDS = [:]
    private final static LOCK = new Object[0]
    
    ClassNode currentClassNode = null

    @Override
    protected void visitClassEx(ClassNode node) {

        synchronized(LOCK) {
            def stringFieldNames = []
            def otherFieldNames = []
            CLASSNODE_TO_STRING_FIELDS[node] = stringFieldNames
            CLASSNODE_TO_OTHER_FIELDS[node] = otherFieldNames
            currentClassNode = node

            // build list of String fields
            node.fields.each { FieldNode it ->
                if (it.initialExpression instanceof ConstantExpression && it.initialExpression.value instanceof String) {
                    stringFieldNames << it.name
                } else {
                    otherFieldNames << it.name
                }
            }
        }
        super.visitClassEx(node)
    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement) {
        if (statement.expression instanceof VariableExpression) {
            def varName = statement.expression.variable
            if (isStringField(varName)) {
                addViolation(statement, "Synchronizing on the constant String field $statement.expression.variable is unsafe. Do not synchronize on interned strings")
            }
        }
        super.visitSynchronizedStatement(statement)
    }

    private boolean isStringField(String fieldName) {

        ClassNode current = currentClassNode

        synchronized(LOCK) {
            while (current) {
                if (CLASSNODE_TO_STRING_FIELDS[current]?.contains(fieldName)) {
                    return true
                } else if (CLASSNODE_TO_OTHER_FIELDS[current]?.contains(fieldName)) {
                    return false        // stop searching in parent
                }
                current = current.outerClass
            }
        }
        false
    }
}
