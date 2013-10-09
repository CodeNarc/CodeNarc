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

    private static final SOURCE_WITH_SINGLE_VIOLATION = '''
        [next: {}] as Iterator
        '''

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
        final SOURCE = '''
        	[run: {}] as Runnable
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTestSourcesNotCheckedByDefault() {
        sourceCodePath = '/where/ever/WhateverTest.groovy'
        assertNoViolations(SOURCE_WITH_SINGLE_VIOLATION)
    }

    @Test
    void testSingleViolation() {
        assertSingleViolation(SOURCE_WITH_SINGLE_VIOLATION,
            2, '[next: {}] as Iterator', violationMessage('java.util.Iterator', 'hasNext, remove'))
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            [:] as Runnable
            [noSuchMethod: {}] as Iterator
            ['next': {}] as Iterator
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'[:] as Runnable', 
                messageText: violationMessage('java.lang.Runnable', 'run')],
            [lineNumber:3, sourceLineText:'[noSuchMethod: {}] as Iterator', 
                messageText: violationMessage('java.util.Iterator', 'hasNext, next, remove')],
            [lineNumber:4, sourceLineText:"['next': {}] as Iterator", 
                messageText: violationMessage('java.util.Iterator', 'hasNext, remove')])
    }

    @Test
    void testMethodsMissingFromInheritedInterfaceViolate() {
        final SOURCE = '''                      
            interface FunnyIterator extends Iterator {
                void makeFun()
                void makeLotsOfFun()    
            }
            
            [next: {}, makeFun: {}] as FunnyIterator
        '''
        assertSingleViolation(SOURCE, 
            7, '[next: {}, makeFun: {}] as FunnyIterator', violationMessage('FunnyIterator', 'hasNext, makeLotsOfFun, remove'))
    }

    @Test
    void testViolation_WithinClass() {
        final SOURCE = '''
            class ValueClass {
                def listener =  [mouseClicked: { }] as java.awt.event.MouseListener
            }
            '''
        assertSingleViolation(SOURCE,
            3, 'def listener =  [mouseClicked: { }] as java.awt.event.MouseListener', 'java.awt.event.MouseListener')
    }

    private String violationMessage(String implementedInterface, String missingMethods) {
        "Incomplete interface implementation. The following methods of $implementedInterface are not implemented" +
        " by this map-to-interface coercion: [$missingMethods]. Please note that calling any of these methods" +
        ' on this implementation will cause an UnsupportedOperationException, which is likely not intended.'
    }
    
    protected Rule createRule() {
        new UnsafeImplementationAsMapRule()
    }
}
