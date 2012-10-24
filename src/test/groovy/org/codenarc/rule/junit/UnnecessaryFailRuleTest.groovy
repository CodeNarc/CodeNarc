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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryFailRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryFailRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnnecessaryFail'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyTest {
                public void testMethod() {
                    try {
                        something()
                    } catch (Exception e) {
                        LOG.error(e) // OK
                        if (x) {
                            fail(e.message)
                        }
                    }

                    try {
                        something()
                    } catch (Exception e) {
                        fail('xyz', e)  //I suppose this is OK b/c it is not the JUnit Assert.fail()
                    }

                    try {
                        something()
                    } catch (Exception e) {
                        assert 'xyz' == e.message
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testFailWithArgument() {
        final SOURCE = '''
            class MyTest {
                public void testMethod() {
                    try {
                        something()
                    } catch (Exception e) {
                        fail()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'fail()', 'Catching an exception and failing will hide the stack trace. It is better to rethrow the exception')
    }

    @Test
    void testMultiCatch() {
        final SOURCE = '''
            class MyTest {
                public void testMethod() {
                    try {
                        something()
                    } catch (IOException e) {

                    } catch (Exception e) {
                        fail()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 9, 'fail()', 'Catching an exception and failing will hide the stack trace. It is better to rethrow the exception')
    }

    @Test
    void testFailMultiline() {
        final SOURCE = '''
            class MyTest {
                public void testMethod() {
                    try {
                        something()
                    } catch (Exception e) {
                        LOG.error(e)
                        fail()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 8, 'fail()', 'Catching an exception and failing will hide the stack trace. It is better to rethrow the exception')
    }

    @Test
    void testFail() {
        final SOURCE = '''
            class MyTest {
                public void testMethod() {
                    try {
                        something()
                    } catch (Exception e) {
                        fail()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'fail()', 'Catching an exception and failing will hide the stack trace. It is better to rethrow the exception')
    }

    @Test
    void testFailWithArgument_FullyQualified() {
        final SOURCE = '''
            class MyTest {
                public void testMethod() {
                    try {
                        something()
                    } catch (Exception e) {
                        Assert.fail()
                    }
                }
            }        '''
        assertSingleViolation(SOURCE, 7, 'Assert.fail()', 'Catching an exception and failing will hide the stack trace. It is better to rethrow the exception')
    }

    @Test
    void testFail_FullyQualified() {
        final SOURCE = '''
            class MyTest {
                public void testMethod() {
                    try {
                        something()
                    } catch (Exception e) {
                        Assert.fail()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 7, 'Assert.fail()', 'Catching an exception and failing will hide the stack trace. It is better to rethrow the exception')
    }

    protected Rule createRule() {
        new UnnecessaryFailRule()
    }
}
