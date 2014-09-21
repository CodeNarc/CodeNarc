/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for PackageNameRule
 *
 * @author Chris Mair
  */
class PackageNameRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'PackageName'
    }

    @Test
    void testRegexIsNull() {
        rule.regex = null
        shouldFailWithMessageContaining('regex') { applyRuleTo('println 1') }
    }

    @Test
    void testApplyTo_DoesNotMatchDefaultRegex() {
        final SOURCE = '''
            package MyPackage.base
            class _MyClass { }
        '''
        assertSingleViolation(SOURCE, null, null, 'MyPackage.base')
    }

    @Test
    void testApplyTo_MatchesDefaultRegex() {
        final SOURCE = '''
            package mypackage.base.domain
            class _MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NumbersWithinFirstPartOfThePackageName() {
        final SOURCE = '''
            package t3
            class MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MatchesDefaultRegex_Numbers() {
        final SOURCE = '''
            package mypackage.base.i18n
            class _MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoPackage() {
        final SOURCE = '''
            class MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DoesNotMatchCustomRegex() {
        final SOURCE = '''
            package mypackage.base.domain
            class _MyClass { }
        '''
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, null, null, 'mypackage.base.domain')
    }

    @Test
    void testApplyTo_MatchesCustomRegex() {
        final SOURCE = '''
            package zpackage.base.domain
            class _MyClass { }
        '''
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoClassDefinition() {
        final SOURCE = '''
            if (isReady) {
                println 'ready'
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PackageNameRequired_MatchesDefaultRegex() {
        final SOURCE = '''
            package mypackage.base.domain
            class _MyClass { }
        '''
        rule.packageNameRequired = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PackageNameRequired_NoPackage() {
        final SOURCE = '''
            class MyClass { }
        '''
        rule.packageNameRequired = true
        assertSingleViolation(SOURCE, 2, 'MyClass', 'Required package declaration is missing')
    }

    protected Rule createRule() {
        new PackageNameRule()
    }

}
