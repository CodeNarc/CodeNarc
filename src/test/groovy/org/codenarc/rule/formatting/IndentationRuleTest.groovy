/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for IndentationRule
 *
 * @author Chris Mair
 */
class IndentationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'Indentation'
        assert rule.singleIndentLevel == '    '
    }

    @Test
    void test_Class_ProperIndentation_NoViolations() {
        final SOURCE = '''
            |class MyClass { }
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Class_WrongIndentation_Violation() {
        final SOURCE = '''
            | class MyClass { }
            |   class MyClass2 { }
            |\tclass MyClass3 { }
        '''.stripMargin()
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'class MyClass { }', messageText:'The class MyClass'],
            [lineNumber:3, sourceLineText:'class MyClass2 { }', messageText:'The class MyClass2'],
            [lineNumber:4, sourceLineText:'class MyClass3 { }', messageText:'The class MyClass3'],
        )
    }

//    @Test
//    void test_Class_singleIndentLevel_NoViolation() {
//        final SOURCE = '''
//            |\tclass MyClass { }
//        '''.stripMargin()
//        rule.singleIndentLevel = '\t'
//        assertNoViolations(SOURCE)
//    }
//
//    @Test
//    void test_Class_singleIndentLevel_Violation() {
//        final SOURCE = '''
//            |\t  class MyClass { }
//        '''.stripMargin()
//        rule.singleIndentLevel = '\t'
//        assertViolations(SOURCE,
//            [lineNumber:2, sourceLineText:'class MyClass { }', messageText:'The class MyClass'],
//        )
//    }

    @Test
    void test_setSpacesPerIndentLevel() {
        rule.setSpacesPerIndentLevel(3)
        assert rule.singleIndentLevel == '   '
    }

    @Override
    protected Rule createRule() {
        new IndentationRule()
    }
}
