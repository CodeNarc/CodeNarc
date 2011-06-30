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

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Reports usages of java.util.Random, which can produce very predictable results. If two instances of Random are
 * created with the same seed and sequence of method calls, they will generate the exact same results. Use
 * java.security.SecureRandom instead, which provides a cryptographically strong random number generator.
 * SecureRandom uses PRNG, which means they are using a deterministic algorithm to produce a pseudo-random number
 * from a true random seed. SecureRandom produces non-deterministic output.
 *
 * By default, this rule does not apply to test files.
 *
 * @author Hamlet D'Arcy
 */
class InsecureRandomRule extends AbstractAstVisitorRule {
    String name = 'InsecureRandom'
    int priority = 2
    Class astVisitorClass = InsecureRandomAstVisitor
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
}

class InsecureRandomAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {

        if (AstUtil.classNodeImplementsType(call.type, Random)) {
            addViolation(call, 'Using Random is insecure. Use SecureRandom instead')
        }
        super.visitConstructorCallExpression(call)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodCall(call, 'Math', 'random', 0)) {
            addViolation(call, 'Using Math.random() is insecure. Use SecureRandom instead')
        } else if (AstUtil.isMethodNamed(call, 'random', 0) && isJavaLangMathCall(call)) {
            addViolation(call, 'Using Math.random() is insecure. Use SecureRandom instead')
        }

        super.visitMethodCallExpression(call)
    }

    private static boolean isJavaLangMathCall(MethodCallExpression call) {
        if (AstUtil.isPropertyNamed(call.objectExpression, 'Math')) {
            if (AstUtil.isPropertyNamed(call.objectExpression.objectExpression, 'lang')) {
                if (AstUtil.isVariable(call.objectExpression.objectExpression.objectExpression, 'java')) {
                    return true
                }
            }
        }
        false
    }

}
