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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ThreadGroupRule
 *
 * @author Hamlet D'Arcy
 */
class ThreadGroupRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThreadGroup'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	getThreadGroup('...')    // a parameter means it must not be a getter.  
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstructors() {
        final SOURCE = '''
            new ThreadGroup("...")
        '''
        assertSingleViolation(SOURCE, 2, 'new ThreadGroup("...")', 'Avoid using java.lang.ThreadGroup; it is unsafe')
    }

    @Test
    void testConstructorsFullyQualified() {
        final SOURCE = '''
            new java.lang.ThreadGroup("...")
        '''
        assertSingleViolation(SOURCE, 2, 'new java.lang.ThreadGroup("...")', 'Avoid using java.lang.ThreadGroup; it is unsafe')
    }

    @Test
    void testConstructors2() {
        final SOURCE = '''
            new ThreadGroup(tg, "my thread group")
        '''
        assertSingleViolation(SOURCE, 2, 'new ThreadGroup(tg, "my thread group")', 'Avoid using java.lang.ThreadGroup; it is unsafe')
    }

    @Test
    void testFromCurrentThread() {
        final SOURCE = '''
            Thread.currentThread().getThreadGroup()
        '''
        assertSingleViolation(SOURCE, 2, 'Thread.currentThread().getThreadGroup()', 'Avoid using java.lang.ThreadGroup; it is unsafe')
    }

    @Test
    void testFromSecurityManager() {
        final SOURCE = '''
            System.getSecurityManager().getThreadGroup()
        '''
        assertSingleViolation(SOURCE, 2, 'System.getSecurityManager().getThreadGroup()', 'Avoid using java.lang.ThreadGroup; it is unsafe')
    }

    protected Rule createRule() {
        new ThreadGroupRule()
    }
}
