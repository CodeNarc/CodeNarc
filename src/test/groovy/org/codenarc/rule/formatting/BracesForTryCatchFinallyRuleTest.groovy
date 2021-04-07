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
import org.junit.Test

/**
 * Tests for BracesForTryCatchFinallyRule
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class BracesForTryCatchFinallyRuleTest extends AbstractRuleTestCase<BracesForTryCatchFinallyRule> {

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
            [line:21, source:'try',                 message: "Opening brace should be on the same line as 'try'"],
            [line:26, source:'catch (Exception e)', message: "'catch' should be on the same line as the closing brace"],
            [line:26, source:'catch (Exception e)', message: "Opening brace should be on the same line as 'catch'"],
            [line:29, source:'finally',             message: "'finally' should be on the same line as the closing brace"],
            [line:29, source:'finally',             message: "Opening brace should be on the same line as 'finally'"]
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
          [line:16, source:'try{',                  message: "Opening brace should not be on the same line as 'try'"],
          [line:19, source:'}catch (Exception e){', message: "'catch' should not be on the same line as the closing brace"],
          [line:19, source:'}catch (Exception e){', message: "Opening brace should not be on the same line as 'catch'"],
          [line:20, source:'}finally{',             message: "'finally' should not be on the same line as the closing brace"],
          [line:20, source:'}finally{',             message: "Opening brace should not be on the same line as 'finally'"]
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
            [line:9, source:'catch(Exception e) // what about {}', message:"'catch' should not be on the same line as the closing brace"],
            [line:12, source:'finally // what about {}', message:"'finally' should not be on the same line as the closing brace"],
            [line:12, source:'finally // what about {}', message:"Opening brace should not be on the same line as 'finally'"]
        )
    }

    @Override
    protected BracesForTryCatchFinallyRule createRule() {
        BracesForTryCatchFinallyRule rule = new BracesForTryCatchFinallyRule()
        rule.validateCatch = true
        rule.validateFinally = true

        rule
    }
}
