/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc

import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTestCase
import org.codenarc.util.BaselineResultsProcessor
import org.codenarc.util.io.ClassPathResource
import org.codenarc.util.io.StringResource
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for BaselineResultsProcessor
 */
class BaselineResultsProcessorTest extends AbstractTestCase {

    private static final PATH1 = 'src/main/MyAction.groovy'
    private static final PATH2 = 'src/main/dao/MyDao.groovy'
    private static final PATH3 = 'src/main/dao/MyOtherDao.groovy'
    private static final PATH4 = 'src/main/dao/SpecialDao.groovy'

    private static final RULE1 = 'Rule #1'
    private static final RULE2 = 'Rule #2'
    private static final RULE3 = 'Rule #3'

    private static final MESSAGE1 = 'Message_1'
    private static final MESSAGE2 = 'Message_2'

    private static final VIOLATION_R1_M1 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE1)
    private static final VIOLATION_R1_M2 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE2)
    private static final VIOLATION_R2_M1 = new Violation(rule:new StubRule(name:RULE2), message:MESSAGE1)
    private static final VIOLATION_R2_M2 = new Violation(rule:new StubRule(name:RULE2), message:MESSAGE2)
    private static final VIOLATION_R3_M1 = new Violation(rule:new StubRule(name:RULE3), message:MESSAGE1)
    private static final VIOLATION_R3_M2 = new Violation(rule:new StubRule(name:RULE3), message:MESSAGE2)
    private static final VIOLATION_R1_M1_2 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE1)
    private static final VIOLATION_R1_M1_3 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE1)
    private static final VIOLATION_R1_EMPTY_MESSAGE = new Violation(rule:new StubRule(name:RULE1), message:'')
    private static final VIOLATION_R1_NULL_MESSAGE = new Violation(rule:new StubRule(name:RULE1), message:null)
    private static final VIOLATION_R2_EMPTY_MESSAGE = new Violation(rule:new StubRule(name:RULE2), message:'')
    private static final VIOLATION_R2_NULL_MESSAGE = new Violation(rule:new StubRule(name:RULE2), message:null)

    private static final String BASELINE_XML = """<?xml version='1.0'?>
        <CodeNarc>
            <File path='$PATH1'>
                <Violation ruleName='$RULE1'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
                <Violation ruleName='$RULE2'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
                <Violation ruleName='$RULE2'>
                    <Message><![CDATA[$MESSAGE2]]></Message>
                </Violation>
                <Violation ruleName='$RULE3'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
            </File>
            <File path='$PATH2'>
                <Violation ruleName='$RULE2'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
            </File>
            <File path='$PATH3'>
                <Violation ruleName='$RULE3'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
            </File>
        </CodeNarc>
        """

    private BaselineResultsProcessor processor = new BaselineResultsProcessor(new StringResource(BASELINE_XML))

    //--------------------------------------------------------------------------
    // Tests
    //--------------------------------------------------------------------------

    @Test
    void test_processResults_MatchesAll() {
        def results = new FileResults(PATH1, [VIOLATION_R1_M1, VIOLATION_R2_M1, VIOLATION_R2_M2, VIOLATION_R3_M1])
        processor.processResults(results)
        assert results.violations == []
        assert processor.numViolationsRemoved == 4
    }

    @Test
    void test_processResults_MatchesSome() {
        def results = new FileResults(PATH1, [VIOLATION_R1_M1, VIOLATION_R1_M2, VIOLATION_R2_M2, VIOLATION_R3_M2])
        processor.processResults(results)
        assert results.violations == [VIOLATION_R1_M2, VIOLATION_R3_M2]
        assert processor.numViolationsRemoved == 2
    }

    @Test
    void test_processResults_MatchesNone() {
        def results = new FileResults(PATH1, [VIOLATION_R1_M2, VIOLATION_R3_M2])
        processor.processResults(results)
        assert results.violations == [VIOLATION_R1_M2, VIOLATION_R3_M2]
        assert processor.numViolationsRemoved == 0
    }

    @Test
    void test_processResults_MultipleViolationsOfSameRule() {
        final String BASELINE_XML = """<?xml version='1.0'?>
        <CodeNarc>
            <File path='$PATH1'>
                <Violation ruleName='$RULE1'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
                <Violation ruleName='$RULE1'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
                <Violation ruleName='$RULE1'>
                    <Message><![CDATA[$MESSAGE1]]></Message>
                </Violation>
            </File>
        </CodeNarc>
        """

        BaselineResultsProcessor processor = new BaselineResultsProcessor(new StringResource(BASELINE_XML))
        def results = new FileResults(PATH1, [VIOLATION_R1_M1, VIOLATION_R1_M1_2, VIOLATION_R1_M1_3])
        processor.processResults(results)
        assert results.violations == []
        assert processor.numViolationsRemoved == 3
    }

    @Test
    void test_processResults_NullOrEmptyMessages() {
        final String BASELINE_XML = """<?xml version='1.0'?>
        <CodeNarc>
            <File path='$PATH1'>
                <Violation ruleName='$RULE1'/>
                <Violation ruleName='$RULE1'>
                    <Message><![CDATA[]]></Message>
                </Violation>
                <Violation ruleName='$RULE2'/>
                <Violation ruleName='$RULE2'>
                    <Message><![CDATA[]]></Message>
                </Violation>
            </File>
        </CodeNarc>
        """

        BaselineResultsProcessor processor = new BaselineResultsProcessor(new StringResource(BASELINE_XML))
        def results = new FileResults(PATH1, [VIOLATION_R1_EMPTY_MESSAGE, VIOLATION_R1_NULL_MESSAGE, VIOLATION_R2_EMPTY_MESSAGE, VIOLATION_R2_NULL_MESSAGE])
        processor.processResults(results)
        assert results.violations == []
        assert processor.numViolationsRemoved == 4
    }

    @Test
    void test_processResults_MultipleFiles() {
        def results = new DirectoryResults('src/main')
        def fileResults1 = new FileResults(PATH1, [VIOLATION_R1_M2, VIOLATION_R2_M1, VIOLATION_R3_M1])
        results.addChild(fileResults1)
        def subDirResults = new DirectoryResults('src/dir/dao')
        subDirResults.addChild(new FileResults(PATH2, [VIOLATION_R2_M1]))
        subDirResults.addChild(new FileResults(PATH3, [VIOLATION_R3_M1, VIOLATION_R3_M2]))
        subDirResults.addChild(new FileResults(PATH4, [VIOLATION_R1_M2]))
        results.addChild(subDirResults)

        processor.processResults(results)
        assert results.findResultsForPath(PATH1).violations == [VIOLATION_R1_M2]
        assert results.findResultsForPath(PATH2).violations == []
        assert results.findResultsForPath(PATH3).violations == [VIOLATION_R3_M2]
        assert results.findResultsForPath(PATH4).violations == [VIOLATION_R1_M2]
        assert processor.numViolationsRemoved == 4
    }

    @Test
    void test_processResults_EmptyResults() {
        def results = new FileResults(PATH1, [])
        processor.processResults(results)
        assert results.violations == []
        assert processor.numViolationsRemoved == 0
    }

    @Test
    void test_processResults_NullResults() {
        shouldFailWithMessageContaining('results') { processor.processResults(null) }
    }

    @Test
    void test_processResults_BaselineFileDoesNotExist() {
        def results = new FileResults(PATH1, [])
        processor = new BaselineResultsProcessor(new ClassPathResource('DoesNotExist.xml'))
        shouldFailWithMessageContaining('DoesNotExist.xml') { processor.processResults(results) }
    }

    @Test
    void test_processResults_BaselineFileInvalidContents() {
        def results = new FileResults(PATH1, [])
        processor = new BaselineResultsProcessor(new StringResource('x%$#@^&*'))
        shouldFailWithMessageContaining('Content is not allowed') { processor.processResults(results) }
    }

}
