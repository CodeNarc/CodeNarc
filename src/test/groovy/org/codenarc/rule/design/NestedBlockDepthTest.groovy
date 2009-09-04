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
package org.codenarc.rule.design

import org.codenarc.rule.Rule
import org.codenarc.rule.AbstractRuleTest

/**
 * Tests for NestedBlockDepthRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class NestedBlockDepthTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NestedBlockDepth'
        assert rule.maxNestedBlockDepth == 3
    }

    void testNoNestedBlocks_CausesNoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count = 23
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    void testNestingDepthLessThanDefaultMaximum_CausesNoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    if (count > maxCount) {                 // 1
                        while(notReady()) {                 // 2
                            listeners.each { notify(it) }   // 3
                        }
                    }
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    void testNestingDepthGreaterThanDefaultMaximum_CausesAViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    if (count > maxCount) {         // 1
                        myList.each {               // 2
                            sleep(1000L)
                            listeners.each {
                                notify(it)          // 3
                                if (logging) {      // 4
                                    println "Still waiting..."
                                }
                            }
                        }
                    }
                }
            }
            '''
        assertSingleViolation(SOURCE, 9, 'if (logging) {')
    }

    void testNestingDepthExceededForFinally_CausesAViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    try {                           // 1
                        while (notReady()) {        // 2
                            sleep(1000L)
                        }
                    }
                    catch(Exception e) {
                    }
                    finally {                       // 1
                        if (ready) {                // 2 
                            println 'ready'
                        }
                    }
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertTwoViolations(SOURCE, 5, 'while (notReady()) {', 12, "if (ready) {")
    }

    void testNestingDepthExceededForTryOrCatch_CausesAViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    while(notReady()) {                 // 1
                        try {                           // 2
                            sleep(1000L)
                        }
                        catch(Exception e) {            // 2
                            log(e)
                        }
                    }
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertTwoViolations(SOURCE, 5, 'try {', 8, "catch(Exception e) {")
    }

    void testNestingDepthExceededForWhile_CausesAViolation() {
        final SOURCE = '''
            def myMethod() {
                while (notReady()) {        // 1
                    sleep(1000L)
                    while(hasChars()) {     // 2
                    }
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertSingleViolation(SOURCE, 5, 'while(hasChars()) {')
    }

    void testNestingDepthExceededForIfOrElse_CausesAViolation() {
        final SOURCE = '''
            if (ready) {                // 1
                if (logging) {          // 2
                    println "waiting..."
                } else {                // 2
                    start()
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertTwoViolations(SOURCE, 3, 'if (logging) {', 5, '} else {')
    }

    void testNestingDepthExceededForSwitch_CausesAViolation() {
        final SOURCE = '''
            if (ready) {                    // 1
                switch(code) {
                    case 1: println "one"   // 2
                    case 2: println "two"   // 2
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertTwoViolations(SOURCE, 4, 'case 1: println "one"', 5, 'case 2: println "two"')
    }

    void testNestingDepthExceededForAForLoop_CausesAViolation() {
        final SOURCE = '''
            while (notReady()) {                // 1
                sleep(1000L)
                for(int i=0; i < 5; i++) {      // 2
                    println "waiting..."
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertSingleViolation(SOURCE, 4, 'for(int i=0; i < 5; i++) {')
    }

    void testNestingDepthExceededForSynchronized_CausesAViolation() {
        final SOURCE = '''
            def myMethod() {
                while (notReady()) {          // 1
                    sleep(1000L)
                    synchronized(this) {      // 2
                        doSomething()
                    }
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertSingleViolation(SOURCE, 5, 'synchronized(this) {')
    }

    void testNestingDepthExceeded_IfStatementWithoutABlock_CausesNoViolation() {
        final SOURCE = '''
            if (ready) {                    // 1
                if (logging) println "ok"   // 2 - but does not count - no block
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertNoViolations(SOURCE)
    }

    void testNestingDepthExceededForClosureAssignment_CausesAViolation() {
        final SOURCE = '''
            def myMethod() {
                while (notReady()) {          // 1
                    sleep(1000L)
                    def closure = {           // 2
                        doSomething()
                    }
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertSingleViolation(SOURCE, 5, 'def closure = {')
    }

    void testNestingDepthExceededForClosureIteration_CausesAViolation() {
        final SOURCE = '''
            def myMethod() {
                while (notReady()) {          // 1
                    sleep(1000L)
                    myList.each { element ->  // 2
                        doSomething(element)
                    }
                }
            }
            '''
        rule.maxNestedBlockDepth = 1
        assertSingleViolation(SOURCE, 5, 'myList.each { element ->')
    }

    protected Rule createRule() {
        return new NestedBlockDepthRule()
    }
}