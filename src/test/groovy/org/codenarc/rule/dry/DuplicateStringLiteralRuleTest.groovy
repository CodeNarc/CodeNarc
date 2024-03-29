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
package org.codenarc.rule.dry

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for DuplicateStringLiteralRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 * @author Nicolas Vuillamy
  */
class DuplicateStringLiteralRuleTest extends AbstractRuleTestCase<DuplicateStringLiteralRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DuplicateStringLiteral'
        assert rule.ignoreStrings == ''
        assert rule.ignoreStringsDelimiter == ','
    }

    @Test
    void test_NoDuplicateStrings() {
        final SOURCE = '''
            println 'w'
            println 'x'

            def y = 'y'
            def z = 'z'
            class MyClass {
                def static X = 'xyz'
                def static Y = 'xyz'
                def field = System.getProperty('file.seperator')
                def x = 'foo'
                def y = 'bar'
                String a = 'a'
                String b = 'b'
                def method() {
                    method('c', 'd')
                    ('e' == 'f') ? 'g' : 'h'
                    'i' ?: 'j'
                    return 'return'
                }
            }

            println 123
            println 123
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAcrossManyMethodCalls() {
        final SOURCE = '''
            println 'w'
            println 'w'
            println 'w'
        '''
        assertTwoViolations(SOURCE, 3, "println 'w'", 4, "println 'w'")
    }

    @Test
    void testEnums() {
        final SOURCE = '''
            enum MyEnum {
              FOO(
                'FOO'
                )

              String field

              MyEnum(String field) {
                 this.field = field
              }
            }
            '''
        assertNoViolations SOURCE
    }
    @Test
    void testMethodCall() {
        final SOURCE = '''
            println 'w', 'w', 'w'
        '''
        assertTwoViolations(SOURCE, 2, "println 'w', 'w', 'w'", 2, "println 'w', 'w', 'w'")
    }

    @Test
    void testInAList() {
        final SOURCE = '''
            def x = ['foo', 'bar', 'foo']
        '''
        assertSingleViolation(SOURCE, 2, "def x = ['foo', 'bar', 'foo']")
    }

    @Test
    void testInAMap() {
        final SOURCE = '''
            def y = [x: 'bar', y: 'bar']
        '''
        assertSingleViolation(SOURCE, 2, "def y = [x: 'bar', y: 'bar']")
    }

    @Test
    void testInDeclarations() {
        final SOURCE = '''
            def x = 'foo'
            def y = 'foo'
            x = 'bar'
            y = 'bar'
        '''
        assertTwoViolations(SOURCE, 3, "def y = 'foo'", 5, "y = 'bar'")
    }

    @Test
    void testInFields() {
        final SOURCE = '''
            class MyClass {
                def x = 'foo'
                def y = 'foo'
            }
        '''
        assertSingleViolation(SOURCE, 4, "def y = 'foo'")
    }

    @Test
    void testInTernary() {
        final SOURCE = '''
            ('e' == 'e') ? 'g' : 'h'
            ('e' == 'f') ? 'g' : 'g'
        '''
        assertTwoViolations(SOURCE, 2, "('e' == 'e') ? 'g' : 'h'", 3, "('e' == 'f') ? 'g' : 'g'")
    }

    @Test
    void testInElvis() {
        final SOURCE = '''
            'foo' ?: 'foo'
        '''
        assertSingleViolation(SOURCE, 2, "'foo' ?: 'foo'")
    }

    @Test
    void testInIf() {
        final SOURCE = '''
            if (x == 'foo') return x
            else if (y == 'foo') return y
            else if (z == 'foo') return z
        '''
        assertTwoViolations(SOURCE, 3, "else if (y == 'foo') return y", 4, "else if (z == 'foo') return z")
    }

    @Test
    void testInReturn() {
        final SOURCE = '''
            if (true) return 'foo'
            else return 'foo'
        '''
        assertSingleViolation(SOURCE, 3, "else return 'foo'")
    }

    @Test
    void testInInvocation() {
        final SOURCE = '''
            'foo'.equals('bar')
            'foo'.equals('baz')
        '''
        assertSingleViolation(SOURCE, 3, "'foo'.equals('baz')")
    }

    @Test
    void testInNamedArgumentList() {
        final SOURCE = '''
            x(b: 'bar')
            y(a: 'bar')
        '''
        assertSingleViolation(SOURCE, 3, "y(a: 'bar')")
    }

    @Test
    void testIgnoreStrings_IgnoresSingleValue() {
        final SOURCE = '''
            def x = ['xyz', 'abc', 'xyz']
            def y = ['foo', 'bar', 'foo']
        '''
        rule.ignoreStrings = 'xyz'
        assertSingleViolation(SOURCE, 3, "def y = ['foo', 'bar', 'foo']")
    }

    @Test
    void testIgnoreStrings_IgnoresMultipleValues() {
        final SOURCE = '''
            def x = ['xyz', 'abc', 'xyz']
            def y = ['foo', 'bar', 'foo']
        '''
        rule.ignoreStrings = 'xyz,foo'
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoreStrings_IgnoresMultipleValuesWithEmptyString() {
        final SOURCE = '''
            def x = ['xyz', 'abc', 'xyz']
            def y = ['foo', 'bar', 'foo']
            def z = ['', 'efg', '']
        '''

        rule.ignoreStrings = ',xyz,foo'
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoreStrings_ignoreStringsDelimiter() {
        final SOURCE = '''
            String idString = '123'
            def x = ['xyz', 'abc', 'xyz']
            def y = ['1,2', 'bar', '1,2']
            def z = ['', 'efg', '', '123']
        '''

        rule.ignoreStringsDelimiter = '|'
        rule.ignoreStrings = '|xyz|1,2|123'     // Note that one of the ignore strings contains a comma
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoreValues_IgnoresValuesSurroundedByWhitespace() {
        final SOURCE = '''
            def x = [' xyz ', 'abc', ' xyz ']
            def y = ['foo', 'bar', 'foo']
        '''
        rule.ignoreStrings = ' xyz ,foo'
        assertNoViolations(SOURCE)
    }

    @Test
    void test_DuplicateStrings_LessThanMinimumLength_NoViolations() {
        final SOURCE = '''
            def y = ['foo', 'foo']
        '''
        rule.duplicateStringMinimumLength = 4
        assertNoViolations(SOURCE)
    }

    @Test
    void test_DuplicateStrings_EqualToMinimumLength_Violations() {
        final SOURCE = '''
            def y = ['foo', 'foo']
        '''
        rule.duplicateStringMinimumLength = 3
        assertSingleViolation(SOURCE, 2, "def y = ['foo', 'foo']")
    }

    @Test
    void test_DuplicateStrings_GreaterThanMinimumLength_Violations() {
        final SOURCE = '''
            def y = ['foo', 'foo']
        '''
        rule.duplicateStringMinimumLength = 2
        assertSingleViolation(SOURCE, 2, "def y = ['foo', 'foo']")
    }

    @Test
    void test_AlwaysIgnoresEmptyString() {
        final SOURCE = '''
            def x = ['', 'abc', '', 'def', '']
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ConfigureSeveralSingleCharStrings_NoViolations() {
        final SOURCE = '''
            def x = ['-', '_', '/', '$', '.', ':', ' ', ',']
            def y = ['-', '_', '/', '$', '.', ':', ' ', ',']
        '''
        rule.ignoreStringsDelimiter = '|'
        rule.ignoreStrings = '-|_|/|$|.|:| |,'
        assertNoViolations(SOURCE)
    }

    @Test
    void testInAnnotation_NoViolation() {
        final SOURCE = '''
            @MyAnnotation1StringValue("string1")
            @MyAnnotation2StringValue("string1")
            @MyAnnotation2ListValue(value = ["string1", "string2"])
            class MyClass1 {
            }

            @MyAnnotation1ListValue(value = ["string1"])
            @MyAnnotation2StringValue("string1")
            class MyClass2 {
            }

            @MyAnnotation1ListValue(value = ["string1", "string2"])
            @MyAnnotation2ListValue(value = ["string2", "string1"])
            class MyClass3 {
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Override
    protected DuplicateStringLiteralRule createRule() {
        new DuplicateStringLiteralRule()
    }
}
