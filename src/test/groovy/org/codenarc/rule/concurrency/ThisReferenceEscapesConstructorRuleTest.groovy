/*
 * Copyright 2013 the original author or authors.
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
import org.junit.jupiter.api.Test

/**
 * Tests for ThisReferenceEscapesConstructorRule
 *
 * @author Artur Gajowy
 */
class ThisReferenceEscapesConstructorRuleTest extends AbstractRuleTestCase<ThisReferenceEscapesConstructorRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThisReferenceEscapesConstructor'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class Valid {
                Integer value

                Valid() {
                    this.value = 42
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class Invalid {
                Invalid() {
                    What.ever(this)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'What.ever(this)', VIOLATION_MESSAGE)
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class EventListener {
                EventListener(EventPublisher publisher) {
                    publisher.register(this)
                    new WorkThread(publisher, this).start()
                    new AnotherWorkThread(listener: this).start()
                }
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'publisher.register(this)', message:VIOLATION_MESSAGE],
            [line: 5, source: 'new WorkThread(publisher, this)', message:VIOLATION_MESSAGE],
            [line: 6, source: 'new AnotherWorkThread(listener: this)', message:VIOLATION_MESSAGE])
    }

    private static final String VIOLATION_MESSAGE = 'The `this` reference escapes constructor.' +
        ' This equals exposing a half-baked object and can lead to race conditions.'

    @Override
    protected ThisReferenceEscapesConstructorRule createRule() {
        new ThisReferenceEscapesConstructorRule()
    }
}
