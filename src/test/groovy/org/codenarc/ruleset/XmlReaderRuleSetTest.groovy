/*
 * Copyright 2008 the original author or authors.
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

import org.codenarc.rule.StubRule
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.rule.basic.EmptyIfStatementRule
import org.codenarc.rule.TestPathRule
import org.codenarc.test.AbstractTestCase
import org.codenarc.rule.Rule

/**
 * Tests for XmlReaderRuleSet
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class XmlReaderRuleSetTest extends AbstractTestCase {
    static final NAMESPACE = '''
        xmlns="http://codenarc.org/ruleset/1.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://codenarc.org/ruleset/1.0 http://codenarc.org/ruleset-schema.xsd"
        xsi:noNamespaceSchemaLocation="http://codenarc.org/ruleset-schema.xsd" '''

    private List rules

    void testNullReader() {
        shouldFailWithMessageContaining('reader') { new XmlReaderRuleSet(null) }
    }

    void testEmptyReader() {
        def reader = new StringReader('')
        shouldFail { new XmlReaderRuleSet(reader) }
    }

    void testNoRules() {
        final XML = "<ruleset $NAMESPACE></ruleset>"
        parseXmlRuleSet(XML)
        assert rules == []
    }

    void testOneRuleScript() {
        // Load ".txt" file so that it gets copied as resource in Idea
        final XML = """
            <ruleset $NAMESPACE>
                <rule-script path='rule/DoNothingRule.txt'/>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertEqualSets(rules*.class.name, ['DoNothingRule'])
    }

    void testOneRuleScriptWithProperties() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule-script path='rule/DoNothingRule.txt'>
                    <property name='name' value='YYYY'/>
                    <property name='priority' value='1'/>
                </rule-script>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertEqualSets(rules*.class.name, ['DoNothingRule'])
        assert rules*.name == ['YYYY']
        assert rules*.priority == [1]
    }

    void testRuleScriptFileNotFound() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule-script path='rule/DoesNotExist.groovy'/>
            </ruleset>"""
        shouldFailWithMessageContaining('DoesNotExist.groovy') { parseXmlRuleSet(XML) }
    }

    void testRuleScriptCompileError() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule-script path='rule/DoesNotCompileRule.txt'/>
            </ruleset>"""
        shouldFail { parseXmlRuleSet(XML) }
    }

    void testRuleScriptNotARule() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule-script path='rule/NotARule.txt'/>
            </ruleset>"""
        shouldFailWithMessageContaining('NotARule') { parseXmlRuleSet(XML) }
    }

    void testOneRule() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule])
    }

    void testTwoRules() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'/>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'/>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, CatchThrowableRule])
    }

    void testTwoRulesWithProperties() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'>
                    <property name='name' value='XXXX'/>
                </rule>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'>
                    <property name='name' value='YYYY'/>
                    <property name='priority' value='1'/>
                </rule>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, CatchThrowableRule])
        assert rules*.name == ['XXXX', 'YYYY']
        assert rules*.priority == [0, 1]
    }

    void testGroovyRuleSet() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/GroovyRuleSet1.txt'/>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleNames(['CatchThrowable', 'ThrowExceptionFromFinallyBlock'])
    }

    void testNestedRuleSet() {
        final XML = """
            <ruleset $NAMESPACE>
                <description>Sample rule set</description>
                <ruleset-ref path='rulesets/RuleSet1.xml'/>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'>
                    <property name='priority' value='1'/>
                </rule>
                <rule-script path='rule/DoNothingRule.txt'/>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleNames(['CatchThrowable', 'TestPath', 'DoNothing'])
        assert rules[0].priority == 1
    }

    void testDeeplyNestedRuleSet() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet3.xml'/>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'/>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'>
                    <property name='priority' value='1'/>
                </rule>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, DuplicateImportRule, CatchThrowableRule, TestPathRule, EmptyIfStatementRule])
        assert findRule('DuplicateImport').priority == 1
        assert findRule('EmptyIfStatement').priority == 1
        assert findRule('CatchThrowable').priority == 1
    }

    void testNestedRuleSet_Excludes() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <exclude name='TestPath'/>
                    <exclude name='EmptyIf*'/>
                </ruleset-ref>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'/>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleClasses([DuplicateImportRule, CatchThrowableRule])
    }

    void testNestedRuleSet_IncludesAndExcludes() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet3.xml'>
                    <exclude name='Stub'/>
                </ruleset-ref>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <include name='TestPath'/>
                    <include name='EmptyIfStatement'/>
                </ruleset-ref>
                <ruleset-ref path='rulesets/GroovyRuleSet1.txt'>
                    <include name='Cat*Throwable'/>
                </ruleset-ref>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'>
                    <property name='priority' value='1'/>
                </rule>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleClasses([DuplicateImportRule, TestPathRule, EmptyIfStatementRule, CatchThrowableRule])
    }

    void testNestedRuleSet_IncludesExcludesAndConfig() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet3.xml'>
                    <include name='Stub'/>
                </ruleset-ref>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <rule-config name='CatchThrowable'>
                        <property name='priority' value='3'/>
                    </rule-config>
                    <exclude name='TestPath'/>
                </ruleset-ref>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'>
                    <property name='priority' value='1'/>
                </rule>
            </ruleset>"""
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, DuplicateImportRule, CatchThrowableRule, EmptyIfStatementRule])
        assert findRule('CatchThrowable').priority == 3
    }

    void testRuleClassNotFound() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.DoesNotExist'/>
            </ruleset>"""
        shouldFail(ClassNotFoundException) { parseXmlRuleSet(XML) }
    }

    void testRuleClassNotARule() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule class='java.lang.Object'/>
            </ruleset>"""
        shouldFailWithMessageContaining('java.lang.Object') { parseXmlRuleSet(XML) }
    }

    void testNestedRuleSet_RuleSetFileNotFound() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/DoesNotExist.xml'/>
            </ruleset>"""
        shouldFailWithMessageContaining('DoesNotExist.xml') { parseXmlRuleSet(XML) }
    }

    void testNestedRuleSet_ConfigRuleDoesNotExist() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <rule-config name='DoesNotExist'>
                        <property name='priority' value='3'/>
                    </rule-config>
                </ruleset-ref>
            </ruleset>"""
        shouldFailWithMessageContaining('DoesNotExist') { parseXmlRuleSet(XML) }
    }

    void testNestedRuleSet_ConfigRulePropertyDoesNotExist() {
        final XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <rule-config name='CatchThrowable'>
                        <property name='DoesNotExist' value='123456789'/>
                    </rule-config>
                </ruleset-ref>
            </ruleset>"""
        shouldFailWithMessageContaining('DoesNotExist') { parseXmlRuleSet(XML) }
    }

    void testRulesListIsImmutable() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>"""
        parseXmlRuleSet(XML)

        shouldFail(UnsupportedOperationException) { rules.clear() }
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------
    
    private void parseXmlRuleSet(String xml) {
        def reader = new StringReader(xml)
        def ruleSet = new XmlReaderRuleSet(reader)
        rules = ruleSet.rules
        log("rules=$rules")
    }

    private void assertRuleClasses(List classes) {
        assertEqualSets(rules*.class, classes)
    }

    private void assertRuleNames(List ruleNames) {
        assertEqualSets(rules*.name, ruleNames)
    }

    private Rule findRule(String name) {
        return rules.find { it.name == name }
    }

}