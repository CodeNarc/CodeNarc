/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.security

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Before
import org.junit.Test

/**
 * Tests for UnsafeImplementationAsMapRule
 *
 * @author Artur Gajowy
 */
class UnsafeImplementationAsMapRuleTest extends AbstractRuleTestCase<UnsafeImplementationAsMapRule> {

    private static final SOURCE_WITH_SINGLE_VIOLATION = """
        [nextElement: {}] as Enumeration      ${violation('java.util.Enumeration', 'hasMoreElements')}
    """

    @Before
    void setup() {
        sourceCodePath = '/src/main/where/ever/Whatever.groovy'
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnsafeImplementationAsMap'
    }

    @Test
    void testNoViolations() {
        assertNoViolations('''
            [run: {}] as Runnable
        ''')
    }

    @Test
    void testTestSourcesNotCheckedByDefault() {
        sourceCodePath = '/where/ever/WhateverTest.groovy'
        assertNoViolations(removeInlineViolations(SOURCE_WITH_SINGLE_VIOLATION))
    }

    @Test
    void testSingleViolation() {
        assertInlineViolations(SOURCE_WITH_SINGLE_VIOLATION)
    }

    @Test
    void testMultipleViolations() {
        assertInlineViolations("""
            [:] as Runnable                     ${violation('java.lang.Runnable', 'run')}
            [noSuchMethod: {}] as Enumeration   ${violation('java.util.Enumeration', 'hasMoreElements, nextElement')}
        """)
    }

    @Test
    void testMethodsMissingFromInheritedInterfaceViolate() {
        assertInlineViolations("""
            interface FunnyEnumeration extends Enumeration {
                void makeFun()
                void makeLotsOfFun()
            }

            [nextElement: {}, makeFun: {}] as FunnyEnumeration    ${violation(
                'FunnyEnumeration', 'hasMoreElements, makeLotsOfFun'
            )}
        """)
    }

    @Test
    void testViolation_WithinClass() {
        assertInlineViolations('''
            class ValueClass {
                def listener = [mouseClicked: { }] as java.awt.event.MouseListener      #java.awt.event.MouseListener
            }
        ''')
    }

    private static String violation(String implementedInterface, String missingMethods) {
        inlineViolation(
            "Incomplete interface implementation. The following methods of $implementedInterface are not implemented" +
            " by this map-to-interface coercion: [$missingMethods]. Please note that calling any of these methods" +
            ' on this implementation will cause an UnsupportedOperationException, which is likely not intended.'
        )
    }

    @Override
    protected UnsafeImplementationAsMapRule createRule() {
        new UnsafeImplementationAsMapRule()
    }
}
