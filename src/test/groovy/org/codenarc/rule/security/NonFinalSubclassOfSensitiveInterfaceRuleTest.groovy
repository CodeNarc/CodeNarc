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

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for NonFinalSubclassOfSensitiveInterfaceRule
 *
 * @author Hamlet D'Arcy
  */
class NonFinalSubclassOfSensitiveInterfaceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NonFinalSubclassOfSensitiveInterface'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            final class MyPermission extends java.security.Permission {
                MyPermission(String name) { super(name) }
                boolean implies(Permission permission) { true }
                boolean equals(Object obj) { true }
                int hashCode() { 0 }
                String getActions() { "action" }
            }

            final class MyBasicPermission extends java.security.Permission {
                MyBasicPermission(String name) { super(name) }
                boolean implies(Permission permission) { true }
                boolean equals(Object obj) { true }
                int hashCode() { 0 }
                String getActions() { "action" }
            }

            final class MyPrivilegedAction implements PrivilegedAction {
                Object run() { 0 }
            }

            final class MyPrivilegedActionException extends PrivilegedActionException {
                MyPrivilegedActionException(Exception exception) { super(exception) }
            }        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPermissionFullyQualified() {
        final SOURCE = '''
            class MyPermission extends java.security.Permission {
                MyPermission(String name) { super(name) }
                boolean implies(Permission permission) { true }
                boolean equals(Object obj) { true }
                int hashCode() { 0 }
                String getActions() { "action" }
            }        '''
        assertSingleViolation(SOURCE, 2, 'class MyPermission extends java.security.Permission', 'The class MyPermission extends java.security.Permission but is not final')
    }

    @Test
    void testBasicPermissionFullyQualified() {
        final SOURCE = '''
            class MyBasicPermission extends java.security.BasicPermission {
                MyBasicPermission(String name) { super(name) }
            }   '''
        assertSingleViolation(SOURCE, 2, 'class MyBasicPermission extends java.security.BasicPermission {', 'The class MyBasicPermission extends java.security.BasicPermission but is not final')
    }

    @Test
    void testPrivilegedActionFullyQualified() {
        final SOURCE = '''
            class MyPrivilegedAction implements java.security.PrivilegedAction {
                Object run() { 0 }
            }        '''
        assertSingleViolation(SOURCE, 2, 'class MyPrivilegedAction implements java.security.PrivilegedAction', 'The class MyPrivilegedAction implements java.security.PrivilegedAction but is not final')
    }

    @Test
    void testPrivilegedActionExceptionFullyQualified() {
        final SOURCE = '''
            class MyPrivilegedActionException extends java.security.PrivilegedActionException {
                MyPrivilegedActionException(Exception exception) { super(exception) }
            }        '''
        assertSingleViolation(SOURCE, 2, 'class MyPrivilegedActionException extends java.security.PrivilegedActionException', ' class MyPrivilegedActionException extends java.security.PrivilegedActionException but is not final')
    }

    @Test
    void testPermission() {
        final SOURCE = '''
            import java.security.Permission
            class MyPermission extends Permission {
                MyPermission(String name) { super(name) }
                boolean implies(Permission permission) { true }
                boolean equals(Object obj) { true }
                int hashCode() { 0 }
                String getActions() { "action" }
            }        '''
        assertSingleViolation(SOURCE, 3, 'class MyPermission extends Permission', 'The class MyPermission extends java.security.Permission but is not final')
    }

    @Test
    void testBasicPermission() {
        final SOURCE = '''
            import java.security.BasicPermission
            class MyBasicPermission extends BasicPermission {
                MyBasicPermission(String name) { super(name) }
            }   '''
        assertSingleViolation(SOURCE, 3, 'class MyBasicPermission extends BasicPermission', 'The class MyBasicPermission extends java.security.BasicPermission but is not final')
    }

    @Test
    void testPrivilegedAction() {
        final SOURCE = '''
            import java.security.PrivilegedAction
            class MyPrivilegedAction implements PrivilegedAction {
                Object run() { 0 }
            }        '''
        assertSingleViolation(SOURCE, 3, 'class MyPrivilegedAction implements PrivilegedAction', 'The class MyPrivilegedAction implements java.security.PrivilegedAction but is not final')
    }

    @Test
    void testPrivilegedActionException() {
        final SOURCE = '''
            import java.security.PrivilegedActionException
            class MyPrivilegedActionException extends PrivilegedActionException {
                MyPrivilegedActionException(Exception exception) { super(exception) }
            }        '''
        assertSingleViolation(SOURCE, 3, 'class MyPrivilegedActionException extends PrivilegedActionException', 'The class MyPrivilegedActionException extends java.security.PrivilegedActionException but is not final')
    }

    protected Rule createRule() {
        new NonFinalSubclassOfSensitiveInterfaceRule()
    }
}

