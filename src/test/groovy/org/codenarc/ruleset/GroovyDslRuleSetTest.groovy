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

import org.codenarc.test.AbstractTestCase
import org.junit.Test

import static org.codenarc.test.TestUtil.*

/**
 * Tests for GroovyDslRuleSet
 *
 * @author Chris Mair
  */
class GroovyDslRuleSetTest extends AbstractTestCase {

    @Test
    void testNullPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslRuleSet(null) }
    }

    @Test
    void testEmptyPath() {
        shouldFailWithMessageContaining('path') { new GroovyDslRuleSet('') }
    }

    @Test
    void testFileDoesNotExist() {
        def errorMessage = shouldFail { new GroovyDslRuleSet('DoesNotExist.xml') }
        assertContainsAll(errorMessage, ['DoesNotExist.xml', 'does not exist'])
    }

    @Test
    void testLoadGroovyRuleSet_SetNonExistentRuleProperty() {
        shouldFailWithMessageContaining('noSuchProperty') { new GroovyDslRuleSet('rulesets/GroovyRuleSet_Bad.txt') }
    }

    @Test
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

    @Test
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

    @Test
    void testLoadGroovyRuleSet_ConfigFileDoesNotCompile() {

        def file = File.createTempFile('codenarctest', '.groovy')
        file.deleteOnExit()
        file.text = '''
// OH HAI, I IZ DEFINITLY NOT COMPILING
+++++++++
'''
        def msg = shouldFail(IllegalStateException) {
            new GroovyDslRuleSet('file:' + file.absolutePath)
        }
        assert msg.contains('error occurred compiling')
        assert msg.contains(file.absolutePath)
        assert msg.contains('unexpected token: ++ @ line 3, column 3')
        file.delete()
    }

    @Test
    void testLoadNestedGroovyRuleSet() {
        final PATH = 'rulesets/GroovyRuleSet2.txt'
        def groovyDslRuleSet = new GroovyDslRuleSet(PATH)
        def rules = groovyDslRuleSet.rules
        log("rules=$rules")
        assert rules*.name == ['CatchThrowable', 'ThrowExceptionFromFinallyBlock', 'StatelessClass']
        assert rules[0].priority == 3
        assert !rules[0].enabled
        assert rules[1].priority == 3
    }

}
