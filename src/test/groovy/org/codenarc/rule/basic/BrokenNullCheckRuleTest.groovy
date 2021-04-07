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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for BrokenNullCheckRule
 *
 * @author Chris Mair
 */
class BrokenNullCheckRuleTest extends AbstractRuleTestCase<BrokenNullCheckRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BrokenNullCheck'
    }

    @Test
    void testNoNullChecks_NoViolations() {
        final SOURCE = '''
            if (name == 'xxx' || name.length > 0) { }
            (string != null && isReady()) ? 'yes' : 'no'
            while (string != null) { }
            def ok = string != null && x == 99
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testProperNullChecks_NoViolations() {
        final SOURCE = '''
            if (name == null || name.length < 1) { }
            def notValid = name == null || name.length == 0
            def notValidStr = (name == null || !name.size()) ? 'not valid' : 'valid'

            if (string != null && string.length == 5) { }
            if (string != null && string.equals("")) { }
            while (nextRecord != null && nextRecord.getId()) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testBrokenNullChecks_PropertyAccess_Violations() {
        final SOURCE = '''
            if (name != null || name.length > 0) { }
            if (name != null || name.length) { }
            while (record == null && record.id < 10) { }
            if (record == null && record.id && somethingElse()) { }
            def isNotValid = record == null && record.id < 10
            return record == null && !record.id
        '''
        assertViolations(SOURCE,
            [line:2, source:'if (name != null || name.length > 0) { }', message:['name', 'None']],
            [line:3, source:'if (name != null || name.length) { }', message:'name'],
            [line:4, source:'while (record == null && record.id < 10) { }', message:'record'],
            [line:5, source:'if (record == null && record.id && somethingElse()) { }', message:'record'],
            [line:6, source:'def isNotValid = record == null && record.id < 10', message:'record'],
            [line:7, source:'return record == null && !record.id', message:'record'])
    }

    @Test
    void testBrokenNullChecks_MethodCall_Violations() {
        final SOURCE = '''
            class MyClass {
                def doStuff() {
                    if (name != null || name.size() > 0) { }
                    if (string == null && string.equals("")) { }
                    def isValid = name != null || name.size() > 0
                    return name != null || !name.size()
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'if (name != null || name.size() > 0) { }', message:['name', 'MyClass']],
            [line:5, source:'if (string == null && string.equals("")) { }', message:'string'],
            [line:6, source:'def isValid = name != null || name.size() > 0', message:'name'],
            [line:7, source:'return name != null || !name.size()', message:'name'])
    }

    @Override
    protected BrokenNullCheckRule createRule() {
        new BrokenNullCheckRule()
    }
}
