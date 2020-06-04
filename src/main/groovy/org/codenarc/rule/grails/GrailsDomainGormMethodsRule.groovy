/*
 * Copyright 2020 the original author or authors.
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
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Database operation should be performed by Data Services instead of calling GORM static and instance methods.
 *
 * @author Vladimir Orany
 */
class GrailsDomainGormMethodsRule extends AbstractAstVisitorRule {

    public static final List<String> DEFAULT_GORM_STATIC_METHOD_NAMES = [
            'attach',
            'count',
            'create',
            'createCriteria',
            'createQueryMapForExample',
            'delete',
            'deleteAll',
            'discard',
            'eachTenant',
            'execute',
            'executeQuery',
            'executeUpdate',
            'exists',
            'finalize',
            'find',
            'findAll',
            'findAllWhere',
            'findOrCreateWhere',
            'findOrSaveWhere',
            'findWhere',
            'first',
            'get',
            'getAll',
            'ident',
            'insert',
            'instanceOf',
            'isAttached',
            'last',
            'list',
            'load',
            'lock',
            'merge',
            'mutex',
            'proxy',
            'read',
            'refresh',
            'save',
            'saveAll',
            'unsupported',
            'where',
            'whereAny',
            'whereLazy',
            'withCriteria',
            'withDatastoreSession',
            'withNewSession',
            'withNewTransaction',
            'withSession',
            'withStatelessSession',
            'withTenant',
            'withTransaction',
    ].asImmutable()

    String name = 'GrailsDomainGormMethods'
    int priority = 3
    Class astVisitorClass = GrailsDomainGormMethodsAstVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
    List<String> gormStaticMethodsNamesList = DEFAULT_GORM_STATIC_METHOD_NAMES

    String getGormStaticMethodsNames() {
        return gormStaticMethodsNamesList.join(',')
    }

    void setGormStaticMethodsNames(String gormStaticMethodsNames) {
        this.gormStaticMethodsNamesList = gormStaticMethodsNames.split(/\s*,\s*/).toList()
    }

}

class GrailsDomainGormMethodsAstVisitor extends AbstractAstVisitor {

    @Override
    @SuppressWarnings('Instanceof')
    void visitMethodCallExpression(MethodCallExpression call) {
        if (call.method instanceof ConstantExpression) {
            String methodName = call.method.value
            ClassNode type = call.objectExpression.type
            if (call.objectExpression instanceof VariableExpression && call.objectExpression.variable == 'this') {
                type = currentClassNode
            }
            ClassNode gormEntityNode = type.interfaces.find {
                it.name == 'org.grails.datastore.gorm.GormEntity'
            }?.redirect()
            if (gormEntityNode) {
                List<MethodNode> methods = gormEntityNode.getMethods(methodName)
                if (methods) {
                    addViolation(call, "Prefer GORM Data Services to GORM instance calls like '$methodName'")
                    return
                }
                if (rule.gormStaticMethodsNamesList.contains(methodName)) {
                    addViolation(call, "Prefer GORM Data Services to GORM static calls like '$methodName'")
                }
            }
        }
    }
}
