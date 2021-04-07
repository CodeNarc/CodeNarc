/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for NoJavaUtilDateRule
 *
 * @author Eric Helgeson
 * @author Chris Mair
 */
class NoJavaUtilDateRuleTest extends AbstractRuleTestCase<NoJavaUtilDateRule> {

    private static final VIOLATION_MESSAGE = NoJavaUtilDateAstVisitor.VIOLATION_MESSAGE

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NoJavaUtilDate'
    }

    @Test
    void test_NoViolations() {
        final SOURCE = '''
            def timestamp = new LocalDateTime()
            MyDate getMyDate(LocalDate localDate) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_UsingExistingDateObjectsAndTypes_NoViolations() {
        final SOURCE = '''
            def getMyDate(java.util.Date date) { }
            def getOtherDate(String name, Date date) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ConstructingANewDateObject_Violations() {
        final SOURCE = '''
            def timestamp = new Date()
            Date myDate = new java.util.Date()
            Date startTime = new Date(123456789L)
        '''
        assertViolations(SOURCE,
                [line:2, source:'def timestamp = new Date()', message:VIOLATION_MESSAGE],
                [line:3, source:'Date myDate = new java.util.Date()', message:VIOLATION_MESSAGE],
                [line:4, source:'Date startTime = new Date(123456789L)', message:VIOLATION_MESSAGE])
    }

    @Test
    void test_ReferencesOtherImportedDateClass_NoViolation() {
        final SOURCE = '''
            import some.other.Date

            class MyClass {
                Date myDate = new java.util.Date()      // the only violation
                def timestamp = new Date()
                Date startTime = new Date(123456789L)
            }
        '''
        assertViolations(SOURCE,
                [line:5, source:'Date myDate = new java.util.Date()', message:VIOLATION_MESSAGE])
    }

    @Test
    void test_UsingDeprecatedStaticFactoryMethods_Violations() {
        final SOURCE = '''
            def parsedDate = Date.parse("12 Aug 1995 13:30:00")
            def utcMillisSinceEpoch = Date.UTC(2020, 1, 25, 17, 19, 0)
        '''
        assertViolations(SOURCE,
            [line:2, source:'def parsedDate = Date.parse("12 Aug 1995 13:30:00")', message:VIOLATION_MESSAGE],
            [line:3, source:'def utcMillisSinceEpoch = Date.UTC(2020, 1, 25, 17, 19, 0)', message:VIOLATION_MESSAGE]
        )
    }

    @Test
    void test_UsingDeprecatedStaticFactoryMethodsButOnCustomDateClass_NoViolations() {
        final SOURCE = '''
            import some.other.Date

            def parsedDate = Date.parse("12 Aug 1995 13:30:00")
            def utcMillisSinceEpoch = Date.UTC(2020, 1, 25, 17, 19, 0)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_UsingConversionMethodFromInstant_NoViolation() {
        final SOURCE = '''
            import java.time.Instant

            Date.from(Instant.now())
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected NoJavaUtilDateRule createRule() {
        new NoJavaUtilDateRule()
    }

}
