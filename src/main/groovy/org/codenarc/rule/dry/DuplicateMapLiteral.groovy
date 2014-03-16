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
package org.codenarc.rule.dry

import org.codehaus.groovy.ast.expr.MapExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Check for multiple instances of the same Map literal, limited to Maps where the keys
 * and values are all constants or literals.
 *
 * @author Chris Mair
 */
class DuplicateMapLiteralRule extends AbstractAstVisitorRule {
    String name = 'DuplicateMapLiteral'
    int priority = 3
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
    Class astVisitorClass = DuplicateMapLiteralAstVisitor
}

class DuplicateMapLiteralAstVisitor extends AbstractAstVisitor {

    private final Collection<Map> mapLiterals = []

    @Override
    void visitMapExpression(MapExpression expression) {

        if (expression.mapEntryExpressions.isEmpty()) {
            return
        }

        if(isFirstVisit(expression)) {
            if (AstUtil.isMapLiteralWithOnlyConstantValues(expression)) {
                def isDuplicate = mapLiterals.find { mapLiteral -> DryUtil.areTheSameConstantOrLiteralMaps(mapLiteral, expression)  }

                if (isDuplicate) {
                    addViolation(expression, "Map ${expression.text} is duplicated.")
                    return  // Ignore duplicate sub-Maps
                }
                mapLiterals.add(expression)
            }
            super.visitMapExpression(expression)
        }
    }

}
