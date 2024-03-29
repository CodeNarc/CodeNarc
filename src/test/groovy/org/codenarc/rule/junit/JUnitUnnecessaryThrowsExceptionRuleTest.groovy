/*
 * Copyright 2012 the original author or authors.
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
import org.junit.jupiter.api.Test

/**
 * Tests for JUnitUnnecessaryThrowsExceptionRule
 *
 * @author Chris Mair
 */
class JUnitUnnecessaryThrowsExceptionRuleTest extends AbstractRuleTestCase<JUnitUnnecessaryThrowsExceptionRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JUnitUnnecessaryThrowsException'
    }

    @Test
    void testAnnotatedMethods_NoThrows_NoViolations() {
        final SOURCE = '''
        class MyTest {
            @Test
            void shouldDoSomething() { }

            @BeforeClass void initialize() { }
            @Before void setUp() { }
            @After void tearDown() { }
            @AfterClass void cleanUp() { }
        }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTestMethod_NoThrows_NoViolations() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void test1() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPrivateOrProtectedMethod_WithThrows_NoViolations() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void test1() { }
                private void testStuff() throws Throwable { }
                protected void test3() throws IOException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonVoidMethod_WithThrows_NoViolations() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                int test1() throws NullPointerException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMethodWithArguments_WithThrows_NoViolations() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void test1(int count) throws RuntimeException { }
                void setUp(int count) throws RuntimeException { }
                void tearDown(int count) throws RuntimeException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticMethod_WithThrows_NoViolations() {
        final SOURCE = '''
            class MyTestCase extends GroovyTestCase {
                static void test1() throws RuntimeException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonTestMethod_WithThrows_NoViolations() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void other() throws RuntimeException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonAnnotatedNonTestMethod_WithThrows_NoViolations() {
        final SOURCE = '''
            class MyTests {
                void other1() throws RuntimeException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAnnotatedMethods_ThrowsClauses_Violations() {
        final SOURCE = '''
        class MyTest {
            @Test
            void shouldDoStuff() throws Exception { }

            @BeforeClass void initialize() throws RuntimeException { }
            @Before void setUp() throws Exception { }
            @After void tearDown() throws Exception { }
            @AfterClass void cleanUp() throws Exception { }
            @Ignore void ignored() throws Exception { }
        }
        '''
        assertViolations(SOURCE,
            [line:4, source:'void shouldDoStuff() throws Exception { }', message:'The shouldDoStuff method in class MyTest'],
            [line:6, source:'@BeforeClass void initialize() throws RuntimeException { }', message:'The initialize method in class MyTest'],
            [line:7, source:'@Before void setUp() throws Exception { }', message:'The setUp method in class MyTest'],
            [line:8, source:'@After void tearDown() throws Exception { }', message:'The tearDown method in class MyTest'],
            [line:9, source:'@AfterClass void cleanUp() throws Exception { }', message:'The cleanUp method in class MyTest'],
            [line:10, source:'@Ignore void ignored() throws Exception { }', message:'The ignored method in class MyTest'])
    }

    @Test
    void testJUnit3TestMethod_Throws_Violations() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void test1() throws Exception { }
                public void test2() throws IOException { }
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'void test1() throws Exception { }', message:'The test1 method in class MyTest'],
            [line:4, source:'public void test2() throws IOException { }', message:'The test2 method in class MyTest'])
    }

    @Test
    void testJUnit3SetUpOrTearDownMethod_Throws_Violations() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void setUp() throws Exception { }
                public void tearDown() throws IOException { }
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'void setUp() throws Exception { }', message:'The setUp method in class MyTest'],
            [line:4, source:'public void tearDown() throws IOException { }', message:'The tearDown method in class MyTest'])
    }

    @Test
    void testApplyTo_NonTestClass() {
        final SOURCE = '''
            class SomeClass {
                void test1() throws Exception { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected JUnitUnnecessaryThrowsExceptionRule createRule() {
        new JUnitUnnecessaryThrowsExceptionRule()
    }
}
