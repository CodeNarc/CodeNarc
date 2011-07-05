/*
 * Copyright 2011 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ClassJavadocRule
 *
 * @author Hamlet D'Arcy
  */
class ClassJavadocRuleTest extends AbstractRuleTestCase {

    static skipTestThatUnrelatedCodeHasNoViolations

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassJavadoc'
    }

    void testNonMainClasses() {
        final SOURCE = '''
            /**
            *
            *
            */
            class MyClass {}

            class OtherClass {}
        '''
        sourceCodeName = 'MyClass'
        assertNoViolations(SOURCE)

    }
    void testSuccess() {
        def testFile = this.getClass().getClassLoader().getResource('rule/ClassJavadocPass.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    void testFailure() {

        def testFile = this.getClass().getClassLoader().getResource('rule/ClassJavadocFail.txt')
        final SOURCE = new File(testFile.toURI()).text
        rule.applyToNonMainClasses = true
        
        assertViolations(SOURCE,
                [lineNumber: 18, sourceLineText: 'class User', messageText: 'Class com.bkool.webapp.user.User missing JavaDoc'],
                [lineNumber: 173, sourceLineText: 'public interface Second', messageText: 'Class com.bkool.webapp.user.Second missing JavaDoc'])
    }

    protected Rule createRule() {
        new ClassJavadocRule()
    }
}