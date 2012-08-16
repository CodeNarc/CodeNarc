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

import org.codehaus.groovy.ast.expr.*

/**
 * Utility methods for the DRY rule classes
 *
 * @author Chris Mair
 */
class DryUtil {

    /**
     * @return true only if both MapExpressions have the same set of constant or literal keys and values, in the same order.
     */
    static boolean areTheSameConstantOrLiteralMaps(MapExpression mapExpression1, MapExpression mapExpression2) {
        def mapEntryExpressions1 = mapExpression1.mapEntryExpressions
        def mapEntryExpressions2 = mapExpression2.mapEntryExpressions

        if (mapEntryExpressions1.size() == mapEntryExpressions2.size()) {
            for (int index = 0; index < mapEntryExpressions1.size(); index++) { // may be empty
                if (!areTheSameConstantOrLiteralMapEntryExpression(mapEntryExpressions1[index], mapEntryExpressions2[index])) {
                    return false
                }
            }
            return true     // all entries matched
        }
        return false
    }

    /**
     * @return true only if both MapEntryExpressions have the same constant or literal key and value
     */
    static boolean areTheSameConstantOrLiteralMapEntryExpression(MapEntryExpression mapEntryExpression1, MapEntryExpression mapEntryExpression2) {
        if (mapEntryExpression1 == null || mapEntryExpression2 == null) {
            return mapEntryExpression1 == mapEntryExpression2
        }
        return haveTheSameConstantOrLiteralValue(mapEntryExpression1.keyExpression, mapEntryExpression2.keyExpression) &&
               haveTheSameConstantOrLiteralValue(mapEntryExpression1.valueExpression, mapEntryExpression2.valueExpression)
    }

    /**
     * @return true only if both Expressions have the same constant or literal value
     */
    static boolean haveTheSameConstantOrLiteralValue(Expression expression1, Expression expression2) {
        if (expression1.class != expression2.class) {
            return false
        }

        boolean isTheSameValue =
            haveTheSameConstantValue(expression1, expression2) ||
            haveTheSameConstantPropertyExpression(expression1, expression2) ||
            haveTheSameMapLiteralValue(expression1, expression2) ||
            haveTheSameListLiteralValue(expression1, expression2)

        return isTheSameValue
    }

    /**
     * @return true only if both Expressions are MapExpressions and both have the same set of constant or
     *      literal keys and values, in the same order.
     */
    static boolean haveTheSameMapLiteralValue(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof MapExpression && expression2 instanceof MapExpression)) {
            return false
        }
        return areTheSameConstantOrLiteralMaps(expression1, expression2)
    }

    /**
     * @return true only if both Expressions are ListExpressions and both have the same set of constant or literal values, in the same order.
     */
    static boolean haveTheSameListLiteralValue(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof ListExpression && expression2 instanceof ListExpression)) {
            return false
        }
        return areTheSameConstantOrLiteralLists(expression1, expression2)
    }

    /**
     * @return true only if both ListExpressions have the same set of constant or literal values, in the same order.
     */
    static boolean areTheSameConstantOrLiteralLists(ListExpression listExpression1, ListExpression listExpression2) {
        def expressions1 = listExpression1.expressions
        def expressions2 = listExpression2.expressions

        if (expressions1.size() == expressions2.size()) {
            for (int index = 0; index < expressions1.size(); index++) { // may be empty
                if (!haveTheSameConstantOrLiteralValue(expressions1[index], expressions2[index])) {
                    return false
                }
            }
            return true     // all entries matched
        }
        return false
    }

    /**
     * @return true only if both Expressions have the same constant or literal values
     */
    static boolean haveTheSameConstantValue(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof ConstantExpression && expression2 instanceof ConstantExpression)) {
            return false
        }
        return expression1.value == expression2.value
    }

    /**
     * @return true only if both Expressions have the same constant property expression (e.g., Object.Property)
     */
    static boolean haveTheSameConstantPropertyExpression(Expression expression1, Expression expression2) {
        if (!(expression1 instanceof PropertyExpression && expression2 instanceof PropertyExpression)) {
            return false
        }

        Expression object1 = ((PropertyExpression) expression1).getObjectExpression()
        Expression property1 = ((PropertyExpression) expression1).getProperty()
        Expression object2 = ((PropertyExpression) expression2).getObjectExpression()
        Expression property2 = ((PropertyExpression) expression2).getProperty()

        boolean isTheSame = false

        if (object1 instanceof VariableExpression) {
            isTheSame = object1.getName() == object2.getName() &&
                property1.getText() == property2.getText()
        }

        return isTheSame
    }
}
