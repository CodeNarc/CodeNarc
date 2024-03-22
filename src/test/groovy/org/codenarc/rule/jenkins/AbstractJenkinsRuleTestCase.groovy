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

    private String initialNonCpsAnnotationName

    @BeforeEach
    void setUp() {
        this.rule.applyToFileNames = ''

        // replace the jenkins annotation com.cloudbees.groovy.cps.NonCPS with a placeholder for testing
        initialNonCpsAnnotationName = JenkinsUtil.nonCpsAnnotationName
        JenkinsUtil.nonCpsAnnotationName = 'org.codenarc.rule.jenkins.NonCPS'
    }

    @AfterEach
    void tearDown() {
        JenkinsUtil.nonCpsAnnotationName = initialNonCpsAnnotationName
    }
}
