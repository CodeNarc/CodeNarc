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
package org.codenarc.ruleset

import org.codenarc.test.AbstractTest

/**
 * Tests for GroovyDslRuleSet
 *
 * @author Chris Mair
 * @version $Revision: 60 $ - $Date: 2009-02-22 14:46:41 -0500 (Sun, 22 Feb 2009) $
 */
class GroovyDslRuleSetTest extends AbstractTest {

    void testNullPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslRuleSet(null) }
    }

    void testEmptyPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslRuleSet('') }
    }

    void testFileDoesNotExist() {
        def errorMessage = shouldFail { new GroovyDslRuleSet('DoesNotExist.xml') }
        assertContainsAll(errorMessage, ['DoesNotExist.xml', 'does not exist'])
    }

    void testLoadGroovyRuleSet() {
        final PATH = 'rulesets/GroovyRuleSet1.txt'  // groovy files are not on classpath; have to use *.txt
        def groovyDslRuleSet = new GroovyDslRuleSet(PATH)
        def rules = groovyDslRuleSet.rules
        log("rules=$rules")
        assert rules*.name == ['CatchThrowable', 'ThrowExceptionFromFinallyBlock']
        assert rules[0].priority == 1
        assert !rules[0].enabled
        assert rules[1].priority == 3
    }

    void testLoadGroovyRuleSet_RelativeFileUrl() {
        final PATH = 'file:src/test/resources/rulesets/GroovyRuleSet1.groovy'
        def groovyDslRuleSet = new GroovyDslRuleSet(PATH)
        def rules = groovyDslRuleSet.rules
        log("rules=$rules")
        assert rules*.name == ['CatchThrowable', 'ThrowExceptionFromFinallyBlock']
        assert rules[0].priority == 1
        assert !rules[0].enabled
        assert rules[1].priority == 3
    }

    void testLoadNestedGroovyRuleSet() {
        final PATH = 'rulesets/GroovyRuleSet2.txt'
        def groovyDslRuleSet = new GroovyDslRuleSet(PATH)
        def rules = groovyDslRuleSet.rules
        log("rules=$rules")
        assert rules*.name == ['CatchThrowable', 'ThrowExceptionFromFinallyBlock']
        assert rules[0].priority == 3
        assert !rules[0].enabled
        assert rules[1].priority == 3
    }

}