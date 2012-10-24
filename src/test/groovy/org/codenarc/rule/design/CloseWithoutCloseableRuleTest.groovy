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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for CloseWithoutCloseableRule
 *
 * @author Hamlet D'Arcy
 */
class CloseWithoutCloseableRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CloseWithoutCloseable'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	void close() { } // in script, OK

            class MyClass implements Closeable {
                void close() {}
            }

            class MyClass2 {
                private void close() { } // private, OK
                void close(arg1) { } // with arguments, OK
            }

            class MyClass3 {
                def close() { null } // return type, OK
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPublicCloseViolation() {
        final SOURCE = '''
            class MyClass {
                void close() { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void close() { }', 'void close() method defined without implementing Closeable')
    }

    @Test
    void testProtectedCloseViolation() {
        final SOURCE = '''
            class MyClass {
                protected void close() { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'protected void close() { }', 'void close() method defined without implementing Closeable')
    }

    protected Rule createRule() {
        new CloseWithoutCloseableRule()
    }
}
