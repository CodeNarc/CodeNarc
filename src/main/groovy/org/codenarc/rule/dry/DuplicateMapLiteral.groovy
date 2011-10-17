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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*

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

    private Collection<Map> mapLiterals = []

    @Override
    void visitMapExpression(MapExpression expression) {

        if (expression.mapEntryExpressions.isEmpty()) {
            return
        }

        if(isFirstVisit(expression)) {
            if (AstUtil.isMapLiteralWithOnlyConstantValues(expression)) {
                def isDuplicate = mapLiterals.find { mapLiteral -> areTheSameMaps(mapLiteral, expression)  }

                if (isDuplicate) {
                    addViolation(expression, "Map ${expression.text} is duplicated.")
                    return  // Ignore duplicate sub-Maps
                }
                mapLiterals.add(expression)
            }
            super.visitMapExpression(expression)
        }
    }

    // TODO Move these methods into AstUtil

    private boolean areTheSameMaps(MapExpression mapExpression1, MapExpression mapExpression2) {
        def mapEntryExpressions1 = mapExpression1.mapEntryExpressions
        def mapEntryExpressions2 = mapExpression2.mapEntryExpressions

        if (mapEntryExpressions1.size() == mapEntryExpressions2.size()) {
            for (int index in 0..mapEntryExpressions1.size()-1) {
                if (!areTheSameMapEntryExpression(mapEntryExpressions1[index], mapEntryExpressions2[index])) {
                    return false
                }
            }
            return true     // all entries matched
        }
        return false
    }

    private boolean areTheSameMapEntryExpression(MapEntryExpression mapEntryExpression1, MapEntryExpression mapEntryExpression2) {
        return hasTheSameConstantOrLiteralValue(mapEntryExpression1.keyExpression, mapEntryExpression2.keyExpression) &&
               hasTheSameConstantOrLiteralValue(mapEntryExpression1.valueExpression, mapEntryExpression2.valueExpression)
    }

    private boolean hasTheSameConstantOrLiteralValue(Expression expression1, Expression expression2) {
        if (expression1.class != expression2.class) {
            return false
        }

        boolean isTheSameValue =
            hasTheSameConstantValue(expression1, expression2) ||
            hasTheSameConstantPropertyExpression(expression1, expression2) ||
            hasTheSameMapLiteralValue(expression1, expression2) ||
            hasTheSameListLiteralValue(expression1, expression2)

        return isTheSameValue
    }

    private boolean hasTheSameMapLiteralValue(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof MapExpression && expression2 instanceof MapExpression)) {
            return false
        }
        return areTheSameMaps(expression1, expression2)
    }

    private boolean hasTheSameListLiteralValue(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof ListExpression && expression2 instanceof ListExpression)) {
            return false
        }
        return areTheSameLists(expression1, expression2)
    }

    private boolean areTheSameLists(ListExpression listExpression1, ListExpression listExpression2) {
        def expressions1 = listExpression1.expressions
        def expressions2 = listExpression2.expressions

        if (expressions1.size() == expressions2.size()) {
            for (int index in 0..expressions1.size()-1) {
                if (!hasTheSameConstantOrLiteralValue(expressions1[index], expressions2[index])) {
                    return false
                }
            }
            return true     // all entries matched
        }
        return false
    }

    private boolean hasTheSameConstantValue(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof ConstantExpression && expression2 instanceof ConstantExpression)) {
            return false
        }
        return expression1.value == expression2.value
    }

    private boolean hasTheSameConstantPropertyExpression(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof PropertyExpression && expression2 instanceof PropertyExpression)) {
            return false
        }

        Expression object1 = ((PropertyExpression) expression1).getObjectExpression();
        Expression property1 = ((PropertyExpression) expression1).getProperty();
        Expression object2 = ((PropertyExpression) expression2).getObjectExpression();
        Expression property2 = ((PropertyExpression) expression2).getProperty();

        boolean isTheSame = false

        if (object1 instanceof VariableExpression) {
            isTheSame = object1.getName() == object2.getName() &&
                property1.getText() == property2.getText()
        }

        return isTheSame
    }

}