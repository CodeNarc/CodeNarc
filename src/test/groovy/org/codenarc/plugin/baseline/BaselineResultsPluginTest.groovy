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
package org.codenarc.plugin.baseline

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.plugin.CodeNarcPlugin
import org.codenarc.plugin.FileViolations
import org.codenarc.results.FileResults
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTestCase
import org.codenarc.util.io.ClassPathResource
import org.codenarc.util.io.StringResource
import org.junit.Before
import org.junit.Test

/**
 * Tests for BaselineResultsPlugin
 */
class BaselineResultsPluginTest extends AbstractTestCase {

    private static final PATH1 = 'src/main/MyAction.groovy'
    private static final PATH2 = 'src/main/dao/MyDao.groovy'
    private static final PATH3 = 'src/main/dao/MyOtherDao.groovy'
    private static final PATH4 = 'src/main/dao/SpecialDao.groovy'

    private static final RULE1 = 'Rule #1'
    private static final RULE2 = 'Rule #2'
    private static final RULE3 = 'Rule #3'

    private static final MESSAGE1 = 'Message_1'
    private static final MESSAGE2 = 'Message_2'
    private static final MESSAGE_SPECIAL_CHARS = '"Guaraní" "75%–84%" "Tschüß" "…" "str\n\r"'   // #303

    private static final VIOLATION_R1_M1 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE1)
    private static final VIOLATION_R1_M2 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE2)
    private static final VIOLATION_R2_M1 = new Violation(rule:new StubRule(name:RULE2), message:' ' + MESSAGE1 + ' ')   // leading/trailing whitespace on message
    private static final VIOLATION_R2_M2 = new Violation(rule:new StubRule(name:RULE2), message:MESSAGE2)
    private static final VIOLATION_R3_M1 = new Violation(rule:new StubRule(name:RULE3), message:MESSAGE1)
    private static final VIOLATION_R3_M2 = new Violation(rule:new StubRule(name:RULE3), message:MESSAGE2)
    private static final VIOLATION_R1_M1_2 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE1)
    private static final VIOLATION_R1_M1_3 = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE1)
    private static final VIOLATION_R1_EMPTY_MESSAGE = new Violation(rule:new StubRule(name:RULE1), message:'')
    private static final VIOLATION_R1_NULL_MESSAGE = new Violation(rule:new StubRule(name:RULE1), message:null)
    private static final VIOLATION_R2_EMPTY_MESSAGE = new Violation(rule:new StubRule(name:RULE2), message:'')
    private static final VIOLATION_R2_NULL_MESSAGE = new Violation(rule:new StubRule(name:RULE2), message:null)
    private static final VIOLATION_R1_SPECIAL_CHARS = new Violation(rule:new StubRule(name:RULE1), message:MESSAGE_SPECIAL_CHARS)

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

    private BaselineResultsPlugin plugin = new BaselineResultsPlugin(new StringResource(BASELINE_XML))

    //--------------------------------------------------------------------------
    // Tests
    //--------------------------------------------------------------------------

    @Test
    void test_ImplementsCodeNarcPlugin() {
        assert plugin instanceof CodeNarcPlugin
    }

    @Test
    void test_initialize_Populates_baselineViolationsMap() {
        initializePlugin(BASELINE_XML)
        assert plugin.baselineViolationsMap
    }

    @Test
    void test_initialize_BaselineFileDoesNotExist() {
        plugin = new BaselineResultsPlugin(new ClassPathResource('DoesNotExist.xml'))
        shouldFailWithMessageContaining('DoesNotExist.xml') { plugin.initialize() }
    }

    @Test
    void test_initialize_BaselineFileInvalidContents() {
        plugin = new BaselineResultsPlugin(new StringResource('x%$#@^&*'))
        shouldFailWithMessageContaining('Content is not allowed') { plugin.initialize() }
    }

    @Test
    void test_processViolationsForFile_MatchesAll() {
        def fileViolations = fileViolations(PATH1, [VIOLATION_R1_M1, VIOLATION_R2_M1, VIOLATION_R2_M2, VIOLATION_R3_M1])
        plugin.processViolationsForFile(fileViolations)
        assert fileViolations.violations == []
        assert plugin.numViolationsRemoved == 4

        // Just trigger reports to see final log message
        plugin.processReports(null)
    }

    @Test
    void test_processViolationsForFile_MatchesSome() {
        def fileViolations = fileViolations(PATH1, [VIOLATION_R1_M1, VIOLATION_R1_M2, VIOLATION_R2_M2, VIOLATION_R3_M2])
        plugin.processViolationsForFile(fileViolations)
        assert fileViolations.violations == [VIOLATION_R1_M2, VIOLATION_R3_M2]
        assert plugin.numViolationsRemoved == 2
    }

    @Test
    void test_processViolationsForFile_MatchesNone() {
        def fileViolations = fileViolations(PATH1, [VIOLATION_R1_M2, VIOLATION_R3_M2])
        plugin.processViolationsForFile(fileViolations)
        assert fileViolations.violations == [VIOLATION_R1_M2, VIOLATION_R3_M2]
        assert plugin.numViolationsRemoved == 0
    }

    @Test
    void test_processViolationsForFile_MultipleViolationsOfSameRule() {
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
        initializePlugin(BASELINE_XML)
        def fileViolations = fileViolations(PATH1, [VIOLATION_R1_M1, VIOLATION_R1_M1_2, VIOLATION_R1_M1_3])
        this.plugin.processViolationsForFile(fileViolations)
        assert fileViolations.violations == []
        assert plugin.numViolationsRemoved == 3
    }

    @Test
    void test_processViolationsForFile_MessageWithSpecialChars() {
        final String BASELINE_XML = """<?xml version='1.0'?>
        <CodeNarc>
            <File path='$PATH1'>
                <Violation ruleName='$RULE1'>
                    <Message><![CDATA[$MESSAGE_SPECIAL_CHARS]]></Message>
                </Violation>
            </File>
        </CodeNarc>
        """
        initializePlugin(BASELINE_XML)
        def fileViolations = fileViolations(PATH1, [VIOLATION_R1_SPECIAL_CHARS])
        plugin.processViolationsForFile(fileViolations)
        assert fileViolations.violations == []
        assert plugin.numViolationsRemoved == 1
    }

    @Test
    void test_processViolationsForFile_NullOrEmptyMessages() {
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
        initializePlugin(BASELINE_XML)
        def fileViolations = fileViolations(PATH1, [VIOLATION_R1_EMPTY_MESSAGE, VIOLATION_R1_NULL_MESSAGE, VIOLATION_R2_EMPTY_MESSAGE, VIOLATION_R2_NULL_MESSAGE])
        this.plugin.processViolationsForFile(fileViolations)
        assert fileViolations.violations == []
        assert plugin.numViolationsRemoved == 4
    }

    @Test
    void test_processViolationsForFile_MultipleFiles() {
        def fileViolations1 = fileViolations(PATH1, [VIOLATION_R1_M2, VIOLATION_R2_M1, VIOLATION_R3_M1])
        def fileViolations2 = fileViolations(PATH2, [VIOLATION_R2_M1])
        def fileViolations3 = fileViolations(PATH3, [VIOLATION_R3_M1, VIOLATION_R3_M2])
        def fileViolations4 = fileViolations(PATH4, [VIOLATION_R1_M2])

        plugin.processViolationsForFile(fileViolations1)
        plugin.processViolationsForFile(fileViolations2)
        plugin.processViolationsForFile(fileViolations3)
        plugin.processViolationsForFile(fileViolations4)

        assert fileViolations1.violations == [VIOLATION_R1_M2]
        assert fileViolations2.violations == []
        assert fileViolations3.violations == [VIOLATION_R3_M2]
        assert fileViolations4.violations == [VIOLATION_R1_M2]
        assert plugin.numViolationsRemoved == 4
    }

    @Test
    void test_processViolationsForFile_SameViolationInANewFile_OnlyRemovesViolationFromFilesInBaseline() {
        final NEW_PATH = 'src/main/NEW'
        def fileViolations1 = fileViolations(PATH1, [VIOLATION_R1_M1, VIOLATION_R2_M2])
        def fileViolations2 = fileViolations(NEW_PATH, [VIOLATION_R1_M1, VIOLATION_R2_M2])

        plugin.processViolationsForFile(fileViolations1)
        plugin.processViolationsForFile(fileViolations2)

        assert fileViolations2.violations == [VIOLATION_R1_M1, VIOLATION_R2_M2]
        assert plugin.numViolationsRemoved == 2
    }

    @Test
    void test_processViolationsForFile_EmptyResults() {
        def fileViolations = fileViolations(PATH1, [])
        plugin.processViolationsForFile(fileViolations)
        assert fileViolations.violations == []
        assert plugin.numViolationsRemoved == 0
    }

    @Test
    void test_processViolationsForFile_NullFileViolations() {
        shouldFailWithMessageContaining('fileViolations') { plugin.processViolationsForFile(null) }
    }

    @Before
    void setUp() {
        plugin.initialize()
    }

    private BaselineResultsPlugin initializePlugin(String baselineXml) {
        plugin = new BaselineResultsPlugin(new StringResource(baselineXml))
        plugin.initialize()
    }

    private FileViolations fileViolations(String path, List<Violation> violations) {
        def fileResults = new FileResults(path, violations)
        return new FileViolations(fileResults)
    }

}
