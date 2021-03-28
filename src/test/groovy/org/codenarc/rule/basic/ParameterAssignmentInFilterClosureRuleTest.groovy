/*
 * Copyright 2021 the original author or authors.
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
package org.codenarc.rule.basic

import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ParameterAssignmentInFilterClosureRule
 *
 * @author Morten Kristiansen
 */
class ParameterAssignmentInFilterClosureRuleTest extends AbstractRuleTestCase<ParameterAssignmentInFilterClosureRule> {

    private static final String VIOLATION_MESSAGE = 'An assignment operator was used on a parameter in a filtering closure. This is usually a typo, and the comparison operator (==) was intended.'

    @Test
    void test_RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ParameterAssignmentInFilterClosure'
    }

    @Test
    void test_AllFilters_NoViolations() {
        final SOURCE = '''
            List someList = [1,2,3]
            someList.find { it == b.property }
            someList.findAll { it == b.property }
            someList.findIndexOf { it == b.property }
            someList.every { it == b.property }
            someList.any { it == b.property }
            someList.filter { it == b.property }
            someList.grep { it == b.property }
            someList.dropWhile { it == b.property }
            someList.takeWhile { it == b.property }
            someList.takeWhile { it.property == b }
            someList.takeWhile { it.property.deepProperty == b }
            someList.hest { a = b.property }
            someList.hest { a = b.property }
            someList.find {
                Integer integer = 42
                return it.hest
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_allFilters_Violations() {
        final SOURCE = '''
            List someList = [1, 2, 3]
            List someOtherList = [object1, object2, object3]
            someList.find { it = 4 }
            someList.findAll { it = 42 }
            someList.findIndexOf { it = 42 }
            someList.every { it = 42 }
            someList.any { it = 42 }
            someList.filter { it = 42 }
            someList.grep { it = 42 }
            someList.dropWhile { it = 42 }
            someList.takeWhile { it = 42 }
            someOtherList.takeWhile { it.someProperty = 42 }
            someOtherList.takeWhile { it.someProperty.aDeeperProperty = 42 }
            someList.find { Integer integer ->
                integer = 42
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'someList.find { it = 4 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:5, sourceLineText:'someList.findAll { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:6, sourceLineText:'someList.findIndexOf { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:7, sourceLineText:'someList.every { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:8, sourceLineText:'someList.any { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:9, sourceLineText:'someList.filter { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:10, sourceLineText:'someList.grep { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:11, sourceLineText:'someList.dropWhile { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:12, sourceLineText:'someList.takeWhile { it = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:13, sourceLineText:'someOtherList.takeWhile { it.someProperty = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:14, sourceLineText:'someOtherList.takeWhile { it.someProperty.aDeeperProperty = 42 }', messageText: VIOLATION_MESSAGE],
            [lineNumber:16, sourceLineText:'integer = 42', messageText: VIOLATION_MESSAGE])
    }

    @Override
    protected ParameterAssignmentInFilterClosureRule createRule() {
        new ParameterAssignmentInFilterClosureRule()
    }
}
