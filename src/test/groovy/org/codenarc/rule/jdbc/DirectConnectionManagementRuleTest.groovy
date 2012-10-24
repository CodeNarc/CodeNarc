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
package org.codenarc.rule.jdbc

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for DirectConnectionManagementRule
 *
 * @author Hamlet D'Arcy
  */
class DirectConnectionManagementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DirectConnectionManagement'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            OtherManager.getConnection()
            java.something.DriverManager.getConnection()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            DriverManager.getConnection()
            java.sql.DriverManager.getConnection()
        '''
        assertTwoViolations(SOURCE,
                2, 'DriverManager.getConnection()', 'Using DriverManager.getConnection() violates the J2EE standards. Use the connection from the context instead',
                3, 'java.sql.DriverManager.getConnection()', 'Using DriverManager.getConnection() violates the J2EE standards. Use the connection from the context instead')
    }

    protected Rule createRule() {
        new DirectConnectionManagementRule()
    }
}
