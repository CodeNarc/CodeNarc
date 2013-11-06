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

import org.codenarc.rule.Rule
import org.junit.Before
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for UnsafeImplementationAsMapRule
 *
 * @author Artur Gajowy
 */
class UnsafeImplementationAsMapRuleTest extends AbstractRuleTestCase {

    private static final SOURCE_WITH_SINGLE_VIOLATION = """                      
        [next: {}] as Iterator      ${violation('java.util.Iterator', 'hasNext, remove')} 
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
            [noSuchMethod: {}] as Iterator      ${violation('java.util.Iterator', 'hasNext, next, remove')}
            ['next': {}] as Iterator            ${violation('java.util.Iterator', 'hasNext, remove')}
        """)
    }

    @Test
    void testMethodsMissingFromInheritedInterfaceViolate() {
        assertInlineViolations("""                  
            interface FunnyIterator extends Iterator {
                void makeFun()
                void makeLotsOfFun()    
            }
            
            [next: {}, makeFun: {}] as FunnyIterator    ${violation('FunnyIterator', 'hasNext, makeLotsOfFun, remove')}
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
    
    protected Rule createRule() {
        new UnsafeImplementationAsMapRule()
    }
}
