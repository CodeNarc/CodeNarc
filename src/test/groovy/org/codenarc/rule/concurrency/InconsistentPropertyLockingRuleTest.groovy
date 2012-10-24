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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for InconsistentPropertyLockingRule
 *
 * @author Hamlet D'Arcy
 */
class InconsistentPropertyLockingRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'InconsistentPropertyLocking'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class Person {
                String name
                Date birthday
                boolean deceased
                boolean parent

                @WithWriteLock setName(String name) {
                    this.name = name
                }
                @groovy.transform.WithReadLock String getName() {
                    name
                }

                void setBirthday(Date birthday) {
                    this.birthday = birthday
                }

                String getBirthday() {
                    birthday
                }

                @groovy.transform.WithWriteLock void setDeceased(boolean deceased) {
                    this.deceased = deceased
                }

                @WithReadLock boolean isDeceased() {
                    deceased
                }

                @WithWriteLock void setParent(boolean parent) {
                    this.parent = parent
                }

                @WithReadLock boolean isParent() {
                    parent
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testWriteLockStringGetMethod() {
        final SOURCE = '''
            class Person {
                String name

                @WithWriteLock setName(String name) {
                    this.name = name
                }
                // violation, get method should be locked
                String getName() {
                    name
                }
            }
        '''
        assertSingleViolation(SOURCE, 9, 'String getName()',
                'The setter method setName is marked @WithWriteLock but the getter method getName is not locked')
    }

    @Test
    void testReadLockDateSetMethod() {
        final SOURCE = '''
            class Person {
                Date birthday

                // violation, set method should be locked
                void setBirthday(Date birthday) {
                    this.birthday = birthday
                }

                @groovy.transform.WithReadLock String getBirthday() {
                    birthday
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'void setBirthday',
                'The getter method getBirthday is marked @WithReadLock but the setter method setBirthday is not locked')
    }

    @Test
    void testLockedBooleanSetMethod() {
        final SOURCE = '''
            class Person {
                boolean deceased

                // violation, set method should be locked
                void setDeceased(boolean deceased) {
                    this.deceased = deceased
                }

                @WithReadLock boolean isDeceased() {
                    deceased
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'void setDeceased(',
                'The getter method isDeceased is marked @WithReadLock but the setter method setDeceased is not locked')
    }

    @Test
    void testWriteLockBooleanGetMethod() {
        final SOURCE = '''
            class Person {
                boolean parent

                @groovy.transform.WithWriteLock void setParent(boolean parent) {
                    this.parent = parent
                }

                // violation, get method should be locked
                boolean isParent() {
                    parent
                }
            }
        '''
        assertSingleViolation(SOURCE, 10, 'boolean isParent()',
                'The setter method setParent is marked @WithWriteLock but the getter method isParent is not locked')
    }

    protected Rule createRule() {
        new InconsistentPropertyLockingRule()
    }
}
