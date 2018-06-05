/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.grails

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

import java.lang.reflect.Modifier

/**
 * String properties in Grails domain classes have to define maximum size otherwise the property is mapped to VARCHAR(255) causing runtime exceptions to occur
 *
 * @author Vladimir Orany
 */
class GrailsDomainStringPropertyMaxSizeRule extends AbstractAstVisitorRule {

    String name = 'GrailsDomainStringPropertyMaxSize'
    int priority = 2
    Class astVisitorClass = GrailsDomainStringPropertyMaxSizeAstVisitor
    String applyToFilesMatching = GrailsUtil.DOMAIN_FILES
}

class GrailsDomainStringPropertyMaxSizeAstVisitor extends AbstractAstVisitor {
    private final Map<String, ClassConstraintsAndMapping> constraintsAndMappings = [:]
            .withDefault { new ClassConstraintsAndMapping() }

    @Override
    void visitField(FieldNode node) {
        ClassConstraintsAndMapping constraintsAndMapping = constraintsAndMappings[node.declaringClass.name]
        if (node.name == 'constraints') {
            constraintsAndMapping.constraints = getMethodCallsAndMapKeys(node.initialValueExpression)
            super.visitField(node)
        } else if (node.name == 'mapping') {
            constraintsAndMapping.mapping = getMethodCallsAndMapKeys(node.initialValueExpression)
            super.visitField(node)
        }
    }

    @Override
    void visitProperty(PropertyNode node) {
        ClassConstraintsAndMapping constraintsAndMapping = constraintsAndMappings[node.declaringClass.name]
        if (node.type.name == 'String'
                && !Modifier.isStatic(node.modifiers)
                && !Modifier.isTransient(node.modifiers)) {
            constraintsAndMapping.stringProperties.add(node.name)
        }
        super.visitProperty(node)
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        super.visitClassComplete(node)

        ClassConstraintsAndMapping constraintsAndMapping = constraintsAndMappings[node.name]

        constraintsAndMapping.stringProperties.each {
            Set<String> constraint = constraintsAndMapping.constraints[it]
            Set<String> mapping = constraintsAndMapping.mapping[it]

            if (!constraint.intersect(['maxSize', 'size']) && !mapping.contains('type')) {
                addViolation(node.getProperty(it), "There is no constraint on the size of String property '$it' which will result in applying database defaults")
            }
        }
    }

    private static Map<String, Set<String>> getMethodCallsAndMapKeys(Expression expression) {
        if (!expression || !(expression instanceof ClosureExpression)) {
            return Collections.emptyMap()
        }

        ClosureExpression closure = expression as ClosureExpression

        if (!closure.code || !(closure.code instanceof BlockStatement)) {
            return Collections.emptyMap()
        }

        Map<String, Set<String>> result = [:].withDefault { [] as Set<String> }

        BlockStatement code = closure.code as BlockStatement
        code.statements.findAll {
            it instanceof ExpressionStatement && it.expression instanceof MethodCallExpression
        }.each { ExpressionStatement exp ->
            collectMapKeys(exp.expression as MethodCallExpression, result)
        }

        result
    }

    private static void collectMapKeys(MethodCallExpression methodCallExpression,  Map<String, Set<String>> result) {
        String methodName = methodCallExpression.methodAsString

        if (methodName == 'importFrom') {
            collectIncludedProperties(methodCallExpression).each {
                // assume size constraint applied if the constraints are imported from different class
                // as the source class is also validated for the presence of the size constraint
                result[it].add('size')
            }
        } else if (methodCallExpression.arguments instanceof TupleExpression) {
            TupleExpression arguments = methodCallExpression.arguments as TupleExpression

            if (arguments.expressions.size() == 1 && arguments.expressions.first() instanceof NamedArgumentListExpression) {
                NamedArgumentListExpression namedArgumentListExpression = arguments.expressions.first() as NamedArgumentListExpression
                result[methodName].addAll namedArgumentListExpression.mapEntryExpressions*.keyExpression*.text
            }
        }
    }

    private static Set<String> collectIncludedProperties(MethodCallExpression call) {
        if (call.arguments instanceof ArgumentListExpression) {
            ArgumentListExpression argumentList = call.arguments as ArgumentListExpression
            MapExpression mapExpression = argumentList.expressions.find { it instanceof MapExpression }
            if (mapExpression) {
                return collectIncludedProperties(mapExpression)
            }
        }
        return Collections.emptySet()
    }

    private static Set<String> collectIncludedProperties(MapExpression mapExpression) {
        MapEntryExpression entryExpression = mapExpression.mapEntryExpressions.find {
            it.keyExpression.text == 'include'
        }
        if (entryExpression.valueExpression instanceof ListExpression) {
            ListExpression includesList = entryExpression.valueExpression as ListExpression
            return includesList.expressions.findAll { it instanceof ConstantExpression }*.text
        }
        return Collections.emptySet()
    }
}

class ClassConstraintsAndMapping {
    Map<String, Set<String>> constraints = [:].withDefault { [] as Set<String> }
    Map<String, Set<String>> mapping = [:].withDefault { [] as Set<String> }
    Set<String> stringProperties = []
}
