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
import org.junit.Test

/**
 * Tests for BracesForTryCatchFinallyRule
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class BracesForTryCatchFinallyRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        def rule = new BracesForTryCatchFinallyRule()
        assert rule.priority == 2
        assert rule.name == 'BracesForTryCatchFinally'
        assert rule.sameLine == true
        assert rule.validateCatch == false
        assert rule.validateFinally == false
        assert rule.catchOnSameLineAsClosingBrace == null
        assert rule.catchOnSameLineAsOpeningBrace == null
        assert rule.finallyOnSameLineAsClosingBrace == null
        assert rule.finallyOnSameLineAsOpeningBrace == null
    }

    @Test
    void testNewLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
            [lineNumber:21, sourceLineText:'try',                 messageText: "Opening brace should be on the same line as 'try'"],
            [lineNumber:26, sourceLineText:'catch (Exception e)', messageText: "'catch' should be on the same line as the closing brace"],
            [lineNumber:26, sourceLineText:'catch (Exception e)', messageText: "Opening brace should be on the same line as 'catch'"],
            [lineNumber:29, sourceLineText:'finally',             messageText: "'finally' should be on the same line as the closing brace"],
            [lineNumber:29, sourceLineText:'finally',             messageText: "Opening brace should be on the same line as 'finally'"]
        )
    }

    @Test
    void testNewLineOverride() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void testSameLineOverride() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        rule.sameLine = false
        assertViolations(SOURCE,
          [lineNumber:16, sourceLineText:'try{',                  messageText: "Opening brace should not be on the same line as 'try'"],
          [lineNumber:19, sourceLineText:'}catch (Exception e){', messageText: "'catch' should not be on the same line as the closing brace"],
          [lineNumber:19, sourceLineText:'}catch (Exception e){', messageText: "Opening brace should not be on the same line as 'catch'"],
          [lineNumber:20, sourceLineText:'}finally{',             messageText: "'finally' should not be on the same line as the closing brace"],
          [lineNumber:20, sourceLineText:'}finally{',             messageText: "Opening brace should not be on the same line as 'finally'"]
      )
    }

    @Test
    void testSameLineFalse_BracesWithinComment_KnownIssue_Violation() {
        rule.sameLine = false
        final SOURCE = '''
            class MyClass {
                void myMethod()
                {
                    try // what about {}
                    {
                        doStuff()
                    }
                    catch(Exception e) // what about {}
                    {
                    }
                    finally // what about {}
                    {
                    }
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:9, sourceLineText:'catch(Exception e) // what about {}', messageText:"'catch' should not be on the same line as the closing brace"],
            [lineNumber:12, sourceLineText:'finally // what about {}', messageText:"'finally' should not be on the same line as the closing brace"],
            [lineNumber:12, sourceLineText:'finally // what about {}', messageText:"Opening brace should not be on the same line as 'finally'"]
        )
    }

    protected Rule createRule() {
        BracesForTryCatchFinallyRule rule = new BracesForTryCatchFinallyRule()
        rule.validateCatch = true
        rule.validateFinally = true
        
        rule
    }
}
