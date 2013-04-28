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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Reports constructors passing the 'this' reference to other methods. 
 * This equals exposing a half-baked objects and can lead to race conditions during initialization.
 * For reference, see <a href='http://www.slideshare.net/alexmiller/java-concurrency-gotchas-3666977/38'>
 * Java Concurrency Gotchas</a> by Alex Miller and <a href='http://www.ibm.com/developerworks/java/library/j-jtp0618/index.html'>
 * Java theory and practice: Safe construction techniques</a> by Brian Goetz.
 *
 * @author Artur Gajowy
 */
class ThisReferenceEscapesConstructorRule extends AbstractAstVisitorRule {
    String name = 'ThisReferenceEscapesConstructor'
    int priority = 2
    Class astVisitorClass = ThisReferenceEscapesConstructorAstVisitor
}

class ThisReferenceEscapesConstructorAstVisitor extends AbstractAstVisitor {
    
    private boolean withinConstructor = false
    
    @Override
    void visitConstructor(ConstructorNode node) {
        withinConstructor = true
        super.visitConstructor(node)
        withinConstructor = false
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        findViolations(call)
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        findViolations(call)
    }

    private void findViolations(Expression methodOrConstructorCall) {
        if (withinConstructor && argumentsContainThis(methodOrConstructorCall.arguments)) {
            addViolation(methodOrConstructorCall, 'The `this` reference escapes constructor. ' +
                'This equals exposing a half-baked object and can lead to race conditions.')
        }
    }

    private boolean argumentsContainThis(TupleExpression argumentsTuple) {
        argumentsTuple.expressions.any { 
            isThisExpression(it) || isNamedArgumentsListContainingThis(it) 
        }
    }

    private boolean isThisExpression(Expression expression) {
        expression instanceof VariableExpression && expression.isThisExpression()
    }

    private boolean isNamedArgumentsListContainingThis(Expression expression) {
        expression instanceof NamedArgumentListExpression && 
            expression.mapEntryExpressions*.valueExpression.any { isThisExpression(it) }
    }
}
