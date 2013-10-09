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
package org.codenarc.rule.security

import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Reports incomplete interface implementations created by map-to-interface coercions.
 *
 * Example: 
 * [hasNext: { ... }] as Iterator 
 * (Not all Iterator methods are implemented. An UnsupportedOperationException will be thrown upon call to e.g. next().)
 *
 * By default, this rule does not apply to test files.
 *
 * @author Artur Gajowy
 */
class UnsafeImplementationAsMapRule extends AbstractAstVisitorRule {
    String name = 'UnsafeImplementationAsMap'
    int priority = 2
    Class astVisitorClass = UnsafeImplementationAsMapAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
}

class UnsafeImplementationAsMapAstVisitor extends AbstractAstVisitor {

    @Override
    void visitCastExpression(CastExpression cast) {
        if (isFirstVisit(cast) && cast.type.isInterface() && cast.expression instanceof MapExpression) {
            def interfaceMethods = cast.type.abstractMethods*.name as Set
            def implementedMethods = getMethodsImplementedByCoercion(cast.expression)
            def unimplementedMethods = (interfaceMethods - implementedMethods).sort()
            if (unimplementedMethods) {
                addViolation(cast, "Incomplete interface implementation. The following methods of $cast.type.name" +
                    " are not implemented by this map-to-interface coercion: $unimplementedMethods. Please note that" +
                    ' calling any of these methods on this implementation will cause' +
                    ' an UnsupportedOperationException, which is likely not intended.')
            }
        }
    }

    private List<String> getMethodsImplementedByCoercion(MapExpression methodMap) {
        List<ConstantExpression> methodNames = methodMap.mapEntryExpressions*.keyExpression.grep(ConstantExpression)
        methodNames*.value
    }
}
