/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ExplicitGarbageCollectionRule
 *
 * @author 'Hamlet D'Arcy'
 */
class ExplicitGarbageCollectionRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitGarbageCollection'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            System.gc(666)
            System2.gc()
            Runtime2.getRuntime().gc()
            Runtime.getRuntime2().gc()
            Runtime.getRuntime(666).gc()
            Runtime.getRuntime().gc(666)
            System.runFinalization(666)
            System2.runFinalization()
            Runtime.runtime.gc2()
            Runtime.runtime.gc(666)
            Runtime2.runtime.gc()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSystemGC() {
        final SOURCE = '''
            System.gc()
        '''
        assertSingleViolation(SOURCE, 2, 'System.gc()', 'Garbage collection should not be explicitly forced')
    }

    @Test
    void testSystemFinalization() {
        final SOURCE = '''
            System.runFinalization()
        '''
        assertSingleViolation(SOURCE, 2, 'System.runFinalization()', 'Garbage collection should not be explicitly forced')
    }

    @Test
    void testRuntimeGcMethodCall() {
        final SOURCE = '''
            Runtime.getRuntime().gc()
        '''
        assertSingleViolation(SOURCE,
                2, 'Runtime.getRuntime().gc()', 'Garbage collection should not be explicitly forced')
    }

    @Test
    void testRuntimeGcPropertyInvocation() {
        final SOURCE = '''
            Runtime.runtime.gc()
        '''
        assertSingleViolation(SOURCE,
                2, 'Runtime.runtime.gc()', 'Garbage collection should not be explicitly forced')
    }

    protected Rule createRule() {
        new ExplicitGarbageCollectionRule()
    }
}
