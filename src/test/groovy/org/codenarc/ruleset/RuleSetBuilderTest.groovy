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

import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.rule.generic.IllegalRegexRule
import org.codenarc.rule.naming.ClassNameRule
import org.codenarc.ruleregistry.RuleRegistry
import org.codenarc.ruleregistry.RuleRegistryHolder
import org.codenarc.test.AbstractTestCase

/**
 * Tests for RuleSetBuilder
 *
 * @author Chris Mair
  */
class RuleSetBuilderTest extends AbstractTestCase {

    private static final RULESET_XML_FILE1 = 'rulesets/RuleSet1.xml'
    private static final RULESET_XML_FILE2 = 'rulesets/RuleSet4.xml'
    private static final RULESET_GROOVY_FILE1 = 'rulesets/GroovyRuleSet1.txt'
    private static final RULE_SCRIPT_FILE = 'rule/DoNothingRule.txt'
    private ruleSetBuilder

    void testRuleset_NullFilename() {
        shouldFailWithMessageContaining('path') { 
            ruleSetBuilder.ruleset {
                ruleset(null)
            }
        }
    }

    void testRuleset_XmlFile_RuleSetFileDoesNotExist() {
        shouldFailWithMessageContaining('DoesNotExist.xml') {
            ruleSetBuilder.ruleset {
                ruleset('DoesNotExist.xml')
            }
        }
    }

    void testRuleset_XmlFile_GroovyRuleSetFileDoesNotExist() {
        shouldFailWithMessageContaining('DoesNotExist.groovy') {
            ruleSetBuilder.ruleset {
                ruleset('DoesNotExist.groovy')
            }
        }
    }

    void testRuleset_XmlFile_NoClosure() {
        ruleSetBuilder.ruleset {
            ruleset(RULESET_XML_FILE1)
        }
        assertRuleNames('TestPath')
    }

    void testRuleset_XmlFile_Exclude() {
        ruleSetBuilder.ruleset {
            ruleset(RULESET_XML_FILE1) {
                exclude 'TestPath'
            }
        }
        assertRuleNames()
    }

    void testRuleset_XmlFile_Include() {
        ruleSetBuilder.ruleset {
            ruleset(RULESET_XML_FILE2) {
                include 'CatchThrowable'
                include 'EmptyTryBlock'
            }
        }
        assertRuleNames('CatchThrowable', 'EmptyTryBlock')
    }

    void testRuleset_XmlFile_ConfigureRuleUsingMap() {
        ruleSetBuilder.ruleset {
            ruleset(RULESET_XML_FILE2) {
                'CatchThrowable' priority:1, enabled:false
                exclude 'Empty*'
            }
        }
        assertRuleNames('CatchThrowable')
        assertRuleProperties('CatchThrowable', [priority:1, enabled:false])
    }

    void testRuleset_XmlFile_ConfigureRuleUsingMap_RuleNotFound() {
        shouldFailWithMessageContaining('NotFound') { 
            ruleSetBuilder.ruleset {
                ruleset(RULESET_XML_FILE2) {
                    'NotFound' priority:1, enabled:false
                }
            }
        }
    }

    void testRuleset_XmlFile_ConfigureRuleUsingClosure() {
        ruleSetBuilder.ruleset {
            ruleset(RULESET_XML_FILE2) {
                'CatchThrowable' {
                    priority = 1
                    enabled = false
                }
                include 'CatchThrowable'
            }
        }
        assertRuleNames('CatchThrowable')
        assertRuleProperties('CatchThrowable', [priority:1, enabled:false])
    }

    void testRuleset_XmlFile_ConfigureRuleUsingClosure_RuleNotFound() {
        shouldFailWithMessageContaining('NotFound') {
            ruleSetBuilder.ruleset {
                ruleset(RULESET_XML_FILE2) {
                    'NotFound' {
                        priority = 1
                        enabled = false
                    }
                }
            }
        }
    }

    void testRuleset_GroovyFile_NoClosure() {
        ruleSetBuilder.ruleset {
            ruleset(RULESET_GROOVY_FILE1)
        }
        assertRuleNames('CatchThrowable', 'ThrowExceptionFromFinallyBlock')
    }

    void testRuleset_GroovyFile_ConfigureRuleUsingClosure() {
        ruleSetBuilder.ruleset {
            ruleset(RULESET_GROOVY_FILE1) {
                'CatchThrowable' {
                    priority = 1
                    enabled = false
                }
                include 'CatchThrowable'
            }
        }
        assertRuleNames('CatchThrowable')
        assertRuleProperties('CatchThrowable', [priority:1, enabled:false])
    }

    void testRule_Class_NoClosure() {
        ruleSetBuilder.ruleset {
            rule CatchThrowableRule
        }
        assertRuleNames('CatchThrowable')
    }

    void testRule_Class_NoClosure_NullRuleClass() {
        shouldFailWithMessageContaining('ruleClass') {
            ruleSetBuilder.ruleset {
                rule(null)
            }
        }
    }

    void testRule_Class_NoClosure_ClassDoesNotImplementRuleInterface() {
        shouldFailWithMessageContaining('ruleClass') {
            ruleSetBuilder.ruleset {
                rule(this.class)
            }
        }
    }

    void testRule_Class_Closure() {
        ruleSetBuilder.ruleset {
            rule(CatchThrowableRule) {
                priority = 1
                enabled = false
            }
        }
        assertRuleNames('CatchThrowable')
        assertRuleProperties('CatchThrowable', [priority:1, enabled:false])
    }

    void testRule_Class_Map() {
        ruleSetBuilder.ruleset {
            rule(CatchThrowableRule, [priority:1, enabled:false])
        }
        assertRuleNames('CatchThrowable')
        assertRuleProperties('CatchThrowable', [priority:1, enabled:false])
    }

    void testRule_Class_Closure_SetNonExistentProperty() {
        shouldFailWithMessageContaining('doesNotExist') {
            ruleSetBuilder.ruleset {
                rule(IllegalRegexRule) {
                    doesNotExist = 1
                }
            }
        }
    }

    void testRule_Class_Closure_NullRuleClass() {
        shouldFailWithMessageContaining('ruleClass') {
            ruleSetBuilder.ruleset {
                rule((Class)null) {
                    priority = 1
                }
            }
        }
    }

    void testRule_Class_Closure_ClassDoesNotImplementRuleInterface() {
        shouldFailWithMessageContaining('ruleClass') {
            ruleSetBuilder.ruleset {
                rule(this.class) {
                    priority = 1
                }
            }
        }
    }

    void testRule_Script_NoClosure() {
        ruleSetBuilder.ruleset {
            rule RULE_SCRIPT_FILE
        }
        assertRuleNames('DoNothing')
    }

    void testRule_Script_NoClosure_ClassDoesNotImplementRuleInterface() {
        shouldFailWithMessageContaining('ruleClass') {
            ruleSetBuilder.ruleset {
                rule('rule/NotARule.txt')
            }
        }
    }

    void testRule_Script_Closure() {
        ruleSetBuilder.ruleset {
            def scriptPath = RULE_SCRIPT_FILE
            rule(scriptPath) {
                priority = 1
                enabled = false
            }
        }
        assertRuleNames('DoNothing')
        assertRuleProperties('DoNothing', [priority:1, enabled:false])
    }

    void testRuleNameOnly_EmptyParentheses() {
        RuleRegistryHolder.ruleRegistry = [getRuleClass:{ ClassNameRule }] as RuleRegistry
        ruleSetBuilder.ruleset {
            ClassName()
        }
        assertRuleNames('ClassName')
    }

    void testRuleNameOnly_ParenthesesWithMap() {
        RuleRegistryHolder.ruleRegistry = [getRuleClass:{ ClassNameRule }] as RuleRegistry
        ruleSetBuilder.ruleset {
            ClassName(priority:1)
        }
        assertRuleProperties('ClassName', [priority:1, enabled:true])
    }

    void testRuleNameOnly_NoParenthesesOrClosure() {
        RuleRegistryHolder.ruleRegistry = [getRuleClass:{ ClassNameRule }] as RuleRegistry
        ruleSetBuilder.ruleset {
            ClassName
        }
        assertRuleNames('ClassName')
    }

    void testRuleNameOnly_NoSuchRuleName() {
        RuleRegistryHolder.ruleRegistry = null
        shouldFailWithMessageContaining('ClassName') {
            ruleSetBuilder.ruleset {
                ClassName()
            }
        }
    }

    void testRuleNameOnly_Closure() {
        RuleRegistryHolder.ruleRegistry = [getRuleClass:{ ClassNameRule }] as RuleRegistry
        ruleSetBuilder.ruleset {
            ClassName {
                priority = 1
                enabled = false
            }
            def myVariable = 27
            println "some arbitrary Groovy code: $myVariable"
        }
        assertRuleNames('ClassName')
        assertRuleProperties('ClassName', [priority:1, enabled:false])
    }

    @SuppressWarnings('JUnitTestMethodWithoutAssert')
    void testDescription() {
        // lack of exception indicated success
        ruleSetBuilder.ruleset {
            description 'abc'
        }
    }

    void setUp() {
        super.setUp()
        ruleSetBuilder = new RuleSetBuilder()
    }

    private RuleSet getRuleSet() {
        ruleSetBuilder.getRuleSet()
    }

    private void assertRuleNames(String[] names) {
        assert getRuleSet().rules*.name == names
    }

    private void assertRuleProperties(String ruleName, Map properties) {
        def rule = findRule(ruleName)
        properties.each { key, value -> assert rule[key] == value }
    }

    private findRule(String name) {
        getRuleSet().rules.find { rule -> rule.name == name }
    }

}