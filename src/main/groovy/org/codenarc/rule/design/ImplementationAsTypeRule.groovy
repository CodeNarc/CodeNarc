/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks for use of the following concrete classes when specifying the type of a method
 * parameter, closure parameter, constructor parameter, method return type or field
 * type (the associated interfaces should be used to specify the type instead):
 * <ul>
 *   <li>java.util.ArrayList</li>
 *   <li>java.util.GregorianCalendar</li>
 *   <li>java.util.HashMap</li>
 *   <li>java.util.HashSet</li>
 *   <li>java.util.Hashtable</li>
 *   <li>java.util.LinkedHashMap</li>
 *   <li>java.util.LinkedHashSet</li>
 *   <li>java.util.LinkedList</li>
 *   <li>java.util.TreeMap</li>
 *   <li>java.util.TreeSet</li>
 *   <li>java.util.Vector</li>
 *   <li>java.util.concurrent.ArrayBlockingQueue</li>
 *   <li>java.util.concurrent.ConcurrentHashMap</li>
 *   <li>java.util.concurrent.ConcurrentLinkedQueue</li>
 *   <li>java.util.concurrent.CopyOnWriteArrayList</li>
 *   <li>java.util.concurrent.CopyOnWriteArraySet</li>
 *   <li>java.util.concurrent.DelayQueue</li>
 *   <li>java.util.concurrent.LinkedBlockingQueue</li>
 *   <li>java.util.concurrent.PriorityBlockingQueue</li>
 *   <li>java.util.concurrent.PriorityQueue</li>
 *   <li>java.util.concurrent.SynchronousQueue</li>
 * </ul>
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class ImplementationAsTypeRule extends AbstractAstVisitorRule {
    String name = 'ImplementationAsType'
    int priority = 2
    Class astVisitorClass = ImplementationAsTypeAstVisitor
}

class ImplementationAsTypeAstVisitor extends AbstractAstVisitor {

    private static final TYPES = [
        'ArrayList', 'java.util.ArrayList',
        'GregorianCalendar', 'java.util.GregorianCalendar',
        'HashMap', 'java.util.HashMap',
        'HashSet', 'java.util.HashSet',
        'Hashtable', 'java.util.Hashtable',
        'LinkedHashMap', 'java.util.LinkedHashMap',
        'LinkedHashSet', 'java.util.LinkedHashSet',
        'LinkedList', 'java.util.LinkedList',
        'TreeMap', 'java.util.TreeMap',
        'TreeSet', 'java.util.TreeSet',
        'Vector', 'java.util.Vector',
        'CopyOnWriteArrayList', 'java.util.concurrent.CopyOnWriteArrayList',
        'CopyOnWriteArraySet', 'java.util.concurrent.CopyOnWriteArraySet',
        'ConcurrentHashMap', 'java.util.concurrent.ConcurrentHashMap',
        'ArrayBlockingQueue', 'java.util.concurrent.ArrayBlockingQueue',
        'ConcurrentLinkedQueue', 'java.util.concurrent.ConcurrentLinkedQueue',
        'DelayQueue', 'java.util.concurrent.DelayQueue',
        'LinkedBlockingQueue', 'java.util.concurrent.LinkedBlockingQueue',
        'PriorityBlockingQueue', 'java.util.concurrent.PriorityBlockingQueue',
        'PriorityQueue', 'java.util.concurrent.PriorityQueue',
        'SynchronousQueue', 'java.util.concurrent.SynchronousQueue',
    ]

    void visitMethodEx(MethodNode methodNode) {
        processParameters(methodNode.parameters)
        processType(methodNode.returnType, "The return type $methodNode.returnType.name should be replaced with an interface or more general parent class")
        super.visitMethodEx(methodNode)
    }

    @Override
    void visitConstructor(ConstructorNode constructorNode) {
        processParameters(constructorNode.parameters)
        super.visitConstructor(constructorNode)
    }

    void visitClosureExpression(ClosureExpression closureExpression) {
        if (isFirstVisit(closureExpression)) {
            processParameters(closureExpression.parameters)
        }
        super.visitClosureExpression(closureExpression)
    }

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        if (isFirstVisit(declarationExpression)) {
            def varExpressions = AstUtil.getVariableExpressions(declarationExpression)
            varExpressions.each { varExpression ->
                def msg = varExpressions.size() > 1 ? "Variable type: [$varExpression.type]" : null
                processType(varExpression.type, msg)
            }
        }
        super.visitDeclarationExpression(declarationExpression)
    }

    void visitField(FieldNode fieldNode) {
        processType(fieldNode.type, "The type $fieldNode.type.name should be replaced with an interface or more general parent class")
        super.visitField(fieldNode)
    }

    private void processParameters(parameters) {
        parameters.each { parameter ->
            processType(parameter.type, "The type $parameter.type.name should be replaced with an interface or more general parent class")
        }
    }

    private void processType(typeNode, String message) {
        String typeName = typeNode.name
        if (typeNode.lineNumber >= 0 && (TYPES.contains(typeName))) {
            addViolation(typeNode, message)
        }
    }

}
