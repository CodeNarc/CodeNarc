/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.MethodCall
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Tests for JenkinsUtil
 *
 * @author Daniel Zänker
 */
class JenkinsUtilTest extends AbstractTestCase {

    @Test
    void testIsCpsMethod_shouldReturnFalseForConstructor() {
        MethodNode methodNode = new MethodNode('someMethod', 0, ClassHelper.VOID_TYPE, [] as Parameter[], [] as ClassNode[], null)
        assertFalse(JenkinsUtil.isCpsMethod(methodNode, true))
    }

    @Test
    void testIsCpsMethod_shouldReturnFalseForNonCpsMethod() {
        MethodNode methodNode = new MethodNode('someMethod', 0, ClassHelper.VOID_TYPE, [] as Parameter[], [] as ClassNode[], null)
        methodNode.addAnnotation(new AnnotationNode(new ClassNode('com.cloudbees.groovy.cps.NonCPS', 0, null, [] as ClassNode[], [] as MixinNode[])))
        assertFalse(JenkinsUtil.isCpsMethod(methodNode, false))
    }

    @Test
    void testIsCpsMethod_shouldReturnTrueForCpsMethod() {
        MethodNode methodNode = new MethodNode('someMethod', 0, ClassHelper.VOID_TYPE, [] as Parameter[], [] as ClassNode[], null)
        assertTrue(JenkinsUtil.isCpsMethod(methodNode, false))
    }

    @Test
    void testGetReceiverType_shouldReturnTypeOfExpression() {
        VariableExpression variableExpression = new VariableExpression('someVar', ClassHelper.Integer_TYPE)
        MethodCall methodCall = new MethodCallExpression(variableExpression, 'someMethod', MethodCallExpression.NO_ARGUMENTS)

        assertEquals(ClassHelper.Integer_TYPE, JenkinsUtil.getReceiverType(methodCall))
    }

    @Test
    void testGetReceiverType_shouldReturnTypeOfClass() {
        StaticMethodCallExpression staticCall = new StaticMethodCallExpression(ClassHelper.Long_TYPE, 'someMethod', MethodCallExpression.NO_ARGUMENTS)

        assertEquals(ClassHelper.Long_TYPE, JenkinsUtil.getReceiverType(staticCall))
    }
}
