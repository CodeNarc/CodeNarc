/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.rule

import org.codehaus.groovy.control.Phases
import org.junit.Test
import org.junit.contrib.java.lang.system.RestoreSystemProperties

import static org.codenarc.rule.AbstractEnhanceableAstVisitorRule.ENHANCED_MODE_SYSTEM_PROPERTY
import static org.codenarc.source.SourceCode.DEFAULT_COMPILER_PHASE

/**
 * @author Marcin Erdmann
 */
class AbstractEnhanceableAstVisitorRuleTest extends AbstractRuleTestCase<AbstractEnhanceableAstVisitorRuleTestRule> {

    @SuppressWarnings('JUnitPublicField')
    @org.junit.Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties()

    @Override
    protected AbstractEnhanceableAstVisitorRuleTestRule createRule() {
        new AbstractEnhanceableAstVisitorRuleTestRule()
    }

    @Test
    void testCompilerPhaseWhenEnhancedModeIsDisabled() {
        rule.enhancedMode = false

        assert rule.compilerPhase == DEFAULT_COMPILER_PHASE
    }

    @Test
    void testCompilerPhaseWhenEnhancedModeIsEnabled() {
        rule.enhancedMode = true

        assert rule.compilerPhase == Phases.SEMANTIC_ANALYSIS
    }

    @Test
    void testEnhancedModeIsDisabledByDefault() {
        assert !rule.enhancedMode
    }

    @Test
    void testEnhancedModeCanBeEnabledUsingASystemProperty() {
        System.setProperty(ENHANCED_MODE_SYSTEM_PROPERTY, 'true')

        assert createRule().enhancedMode
    }

    static class AbstractEnhanceableAstVisitorRuleTestRule extends AbstractEnhanceableAstVisitorRule {
        String name = 'AbstractEnhanceableAstVisitorRuleTest'
        int priority = 1
        Class astVisitorClass = AbstractEnhanceableAstVisitorRuleTestAstVisitor
    }

    static class AbstractEnhanceableAstVisitorRuleTestAstVisitor extends AbstractMethodVisitor {
    }
}
