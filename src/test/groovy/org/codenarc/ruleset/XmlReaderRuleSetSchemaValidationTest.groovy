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

class XmlReaderRuleSetSchemaValidationTest extends AbstractTest {
    static final NAMESPACE = '''
        xmlns="http://codenarc.org/ruleset/1.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://codenarc.org/ruleset/1.0 http://codenarc.org/ruleset-schema.xsd"
        xsi:noNamespaceSchemaLocation="http://codenarc.org/ruleset-schema.xsd" '''

    void testIllegalTopLevelElement() {
        def XML = '<ruleset2></ruleset2>'
        assertSchemaValidationError(XML, 'ruleset2')
    }

    void testNoNamespaceDeclaration() {
        def XML = """
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>"""
        assertSchemaValidationError(XML, 'ruleset')
    }

    void testIllegalRuleSetChildElement() {
        def XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'/>
                <other stuff='12345'/>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalRuleChildElement() {
        def XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'>
                    <property name='name' value='YYYY'/>
                    <other name='priority' value='1'/>
                </rule>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalRuleAttribute() {
        def XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule' other='12345'/>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalPropertyAttribute() {
        def XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'>
                    <property other='name' value='YYYY'/>
                </rule>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalRuleSetRefChildElement() {
        def XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet1.xml'>
                    <other name='12345'/>
                </ruleset-ref>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalRuleSetRefAttribute() {
        def XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet1.xml' other='12345'/>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalIncludeAttribute() {
        def XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet1.xml'>
                    <include other='12345'/>
                </ruleset-ref>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalExcludeAttribute() {
        def XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet1.xml'>
                    <exclude name='123' other='12345'/>
                </ruleset-ref>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalRuleConfigAttribute() {
        def XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet1.xml'>
                    <rule-config name='123' other='12345'/>
                </ruleset-ref>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalRuleConfigChildElement() {
        def XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet1.xml'>
                    <rule-config name='123'>
                        <other name='12345'/>
                    </rule-config>
                </ruleset-ref>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    void testIllegalRuleConfigPropertyAttribute() {
        def XML = """
            <ruleset $NAMESPACE>
                <ruleset-ref path='rulesets/RuleSet1.xml'>
                    <rule-config name='123'>
                        <property name='123' other='name' value='YYYY'/>
                    </rule-config>
                </ruleset-ref>
            </ruleset>"""
        assertSchemaValidationError(XML, 'other')
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private XmlReaderRuleSet assertSchemaValidationError(String xml, String expectedText) {
        def reader = new StringReader(xml)
        shouldFailWithMessageContaining(expectedText) { new XmlReaderRuleSet(reader) }
    }
}