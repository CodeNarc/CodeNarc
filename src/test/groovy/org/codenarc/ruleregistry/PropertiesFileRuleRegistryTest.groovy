/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.ruleregistry

import org.codenarc.rule.naming.ClassNameRule
import org.codenarc.rule.naming.MethodNameRule
import org.codenarc.rule.unused.UnusedPrivateFieldRule
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for PropertiesFileRuleRegistry
 *
 * @author Chris Mair
  */
class PropertiesFileRuleRegistryTest extends AbstractTestCase {

    private registry = new PropertiesFileRuleRegistry()

    @Test
    void testGetRuleClass_LoadsRulesFromPropertiesFile() {
        assert registry.getRuleClass('ClassName') == ClassNameRule
        assert registry.getRuleClass('MethodName') == MethodNameRule
        assert registry.getRuleClass('UnusedPrivateField') == UnusedPrivateFieldRule
    }

    @Test
    void testGetRuleClass_ReturnsNullForNoMatchingRule() {
        assert registry.getRuleClass('NoSuchRule') == null
    }

}
