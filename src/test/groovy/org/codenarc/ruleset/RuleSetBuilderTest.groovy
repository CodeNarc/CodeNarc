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
import org.codenarc.rule.exceptions.CatchThrowableRule

/**
 * Tests for RuleSetBuilder
 *
 * @author Chris Mair
 * @version $Revision: 60 $ - $Date: 2009-02-22 14:46:41 -0500 (Sun, 22 Feb 2009) $
 */
class RuleSetBuilderTest extends AbstractTest {

    private static final RULESET_XML_FILE1 = 'rulesets/RuleSet1.xml'
    private static final RULESET_XML_FILE2 = 'rulesets/RuleSet4.xml'
    private ruleSetBuilder

    void testRuleset_NullFilename() {
        shouldFailWithMessageContaining('path') { 
            ruleSetBuilder.ruleset {
                ruleset(null)
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

    void testRule_Class_Closure_NullRuleClass() {
        shouldFailWithMessageContaining('ruleClass') {
            ruleSetBuilder.ruleset {
                rule(null) {
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

    void testSetDescription() {
        ruleSetBuilder.setDescription('abc')
        assert ruleSetBuilder.description == 'abc' 
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