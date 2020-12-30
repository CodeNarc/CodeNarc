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
package org.codenarc.rule.unused

import org.junit.Test

/**
 * Tests for UnusedPrivateFieldRule when constructor only usages are allowed
 */
class UnusedPrivateFieldRuleTest extends AbstractUnusedPrivateFieldRuleTest {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedPrivateField'
    }

    @Test
    void testBugFix_CannotCastFieldNodeToMetaClass() {
        final SOURCE = '''
            class FlowBuilder extends AbstractFlowBuilder implements GroovyObject, ApplicationContextAware {
                private MetaClass metaClass
                FlowBuilder() {
                    println metaClass
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstructorOnlyUsages() {
        assertNoViolations '''
            class Injected {}

            class Bean {
                private final Injected unused

                Bean(Injected unused) {
                    this.unused = unused
                }
            }
        '''
    }
}
