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
 * @author Chris Mair
  */
class UnnecessaryGetterRuleTest extends AbstractRuleTestCase<UnnecessaryGetterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryGetter'
        assert rule.checkIsMethods == true
    }

    @Test
    void testNoViolations() {
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
        assertViolations(SOURCE,
                [line:2, source:'x.getProperty()', message:'getProperty()'],
                [line:3, source:'x.getPi()', message:'getPi()'])
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
        assertViolations(SOURCE,
                [line:2, source:'x.getA()', message:'getA()'])
    }

    @Test
    void testUpperCaseGetter1() {
        final SOURCE = '''
            x.getURLs()
        '''
        assertViolations(SOURCE,
                [line:2, source:'x.getURLs()', message:'getURLs()'])
    }

    @Test
    void testUpperCaseGetter2() {
        final SOURCE = '''
            x.getURL()
            x.isURL()
        '''
        assertViolations(SOURCE,
                [line:2, source:'x.getURL()', message:'getURL()'],
                [line:3, source:'x.isURL()', message:'isURL()'])
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

                private final field = 'field'

                def test() {
                    expect:
                    field == 'field'
                    getField() == 'getField'
                    isField() == 'getField'
                }

                private static String getField() {
                    'getField'
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIsMethodGetter() {
        final SOURCE = '''
            x.isProperty()
            x.isPi()
            x.isX()
        '''
        assertViolations(SOURCE,
                [line:2, source:'x.isProperty()', message:'isProperty()'],
                [line:3, source:'x.isPi()', message:'isPi'],
                [line:4, source:'x.isX()', message:'isX'])
    }

    @Test
    void testIsMethodGetter_checkIsMethods_False_NoViolations() {
        final SOURCE = '''
            x.isProperty()
            x.isPi()
            x.isX()
        '''
        rule.checkIsMethods = false
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
                [line:4, source:'x.getA()', message:'getA() can probably be rewritten as a'])
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
            [line:4, source:'getSomeData()', message:'getSomeData()'])
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
            Spy {
                getMoreData()
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'getSomeData()', message:'getSomeData()'],
            [line:6, source:'getData2()', message:'getData2()'],
            [line:9, source:'getData3()', message:'getData3()'],
            [line:11, source:'getData4()', message:'getData4()'],
            [line:12, source:'getData5()', message:'getData5()'],
            [line:14, source:'getMoreData()', message:'getMoreData()'],
        )
    }

    @Test
    void testSpock_Mock() {
        final SOURCE = '''
            def someMock = Mock(MyType) {
                getSomeData() >> 'some-data'
            }
            def otherMock = Mock(MyType, [a:1]) {
                getSomeData() >> 'some-data\'
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

                product2 = Stub(constructorArgs:[1, 2, 3], Product) {
                    getId() >> 10
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSpock_Spy() {
        final SOURCE = '''
            def "some test method"() {
                def x = Spy(SomeClass) {
                        getMethod() >> {
                    }
                }

                def y = Spy(SomeClass, constructorArgs: [1, 2, 3]) {
                        getMethod() >> {
                    }
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoredMethodName() {
        final SOURCE = '''
            optional.getOrNull()
            x.isPi()
        '''
        rule.ignoreMethodNames = 'getOrNull, isPi'
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnnecessaryGetterRule createRule() {
        new UnnecessaryGetterRule()
    }
}
