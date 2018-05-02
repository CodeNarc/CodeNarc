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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UnnecessaryGetterRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryGetterRuleTest extends AbstractRuleTestCase<UnnecessaryGetterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryGetter'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            x.get()
            x.property
            x.first
            x.firstName
            x.a
            x.getnotagetter()
            x.getClass()
            x.getProperty('key') '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTwoSimpleGetters() {
        final SOURCE = '''
            x.getProperty()
            x.getPi()
        '''
        assertTwoViolations SOURCE,
                2, 'x.getProperty()', 'getProperty() can probably be rewritten as property',
                3, 'x.getPi()', 'getPi() can probably be rewritten as pi'
    }

    @Test
    void testCamelCaseGetters() {
        final SOURCE = '''
            x.getFirstName()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getFirstName()', 'getFirstName() can probably be rewritten as firstName')
    }

    @Test
    void testSingleLetterNamedGetters() {
        final SOURCE = '''
            x.getA()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getA()', 'getA() can probably be rewritten as a')
    }

    @Test
    void testUpperCaseGetter1() {
        final SOURCE = '''
            x.getURLs()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getURLs()', 'getURLs() can probably be rewritten as URLs')
    }

    @Test
    void testUpperCaseGetter2() {
        final SOURCE = '''
            x.getURL()
        '''
        assertSingleViolation(SOURCE, 2, 'x.getURL()', 'getURL() can probably be rewritten as URL')
    }

    @Test
    void testNonGetter() {
        final SOURCE = '''
            def allPaths = resultsMap.keySet()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testFieldWithSameNameAlreadyExists() {
        final SOURCE = '''
            class Spec extends Specification {

                private final field = 'field\'

                def test() {
                    expect:
                    field == 'field\'
                    getField() == 'getField\'
                }

                private static String getField() {
                    'getField\'
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testGetterWithinClosure() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    x.getA()
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:4, sourceLineText:'x.getA()', messageText:'getA() can probably be rewritten as a'])
    }

    @Test
    void testGetterWithinMethodCall() {
        final SOURCE = '''
            class MyClass {
                def thing = doStuff(12345) {
                    getSomeData()
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'getSomeData()', messageText:'getSomeData()'])
    }

    @Test
    void testGetterWithinMethod_SameNameAsSpockMethods_ButDifferentMethodSignatures() {
        final SOURCE = '''
            Mock {
                getSomeData()
            }
            Stub(1,2,3) {
                getData2()
            }
            "${'Stub'}"(MyClass) {
                getData3()
            }
            def closure = { getData4() }
            Mock({ getData5() }, 1234)      // 2nd param is not a Closure
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'getSomeData()', messageText:'getSomeData()'],
            [lineNumber:6, sourceLineText:'getData2()', messageText:'getData2()'],
            [lineNumber:9, sourceLineText:'getData3()', messageText:'getData3()'],
            [lineNumber:11, sourceLineText:'getData4()', messageText:'getData4()'],
            [lineNumber:12, sourceLineText:'getData5()', messageText:'getData5()'])
    }

    @Test
    void testSpock_Mock() {
        final SOURCE = '''
            def someMock = Mock(MyType) {
                getSomeData() >> 'some-data'
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSpock_Stub() {
        final SOURCE = '''
            def "some test method"() {
                product = Stub(Product) {
                    getId() >> 10
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnnecessaryGetterRule createRule() {
        new UnnecessaryGetterRule()
    }
}
