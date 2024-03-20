/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Abstract class for Jenkins rule testcases
 *
 * @author Daniel Zänker
 */
abstract class AbstractJenkinsRuleTestCase<T extends Rule> extends AbstractRuleTestCase<T> {

    protected List<String> nonCpsMethods = []

    /**
     * Simulates a method as if its annotated with @NonCPS by overwriting the method call to {@link JenkinsUtil#isCpsMethod(org.codehaus.groovy.ast.MethodNode, boolean)}.
     * This is necessary to avoid adding the real com.cloudbees.groovy.cps.NonCPS method annotation to the test dependencies.
     *
     * @param methodName The name of the method to mark with @NonCPS
     */
    protected void addNonCPSMethod(String methodName) {
        nonCpsMethods.add(methodName)

        JenkinsUtil.metaClass.static.isCpsMethod = { MethodNode methodNode, boolean isConstructor ->
            return !isConstructor && !nonCpsMethods.contains(methodNode.name)
        }
    }

    @BeforeEach
    void setUp() {
        this.rule.applyToFileNames = ''
    }

    @AfterEach
    void tearDown() {
        nonCpsMethods = []
        GroovySystem.metaClassRegistry.removeMetaClass(JenkinsUtil)
    }
}
