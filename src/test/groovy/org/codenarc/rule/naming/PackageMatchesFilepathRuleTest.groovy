/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.naming

import org.codenarc.rule.Rule
import org.junit.Before
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for PackageMatchesFilepathRule
 *
 * @author Simon Tost
 */
class PackageMatchesFilepathRuleTest extends AbstractRuleTestCase {

    // static skipTestThatUnrelatedCodeHasNoViolations

    @Before
    void setup() {
        sourceCodePath = filePath('_some_absolute_path_to_project_org_organization_project_component_module_MyClass.groovy')
        sourceCodeName = 'MyClass.groovy'
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 1
        assert rule.name == 'PackageMatchesFilepath'
    }

    @Test
    void testSourceCodePath_NullOrEmpty() {
        final SOURCE = '''\
            package ignore
        '''
        sourceCodePath = null
        assertNoViolations(SOURCE)

        sourceCodePath = ''
        assertNoViolations(SOURCE)
    }

    @Test
    void testGroupId_NullOrEmpty() {
        final SOURCE = '''\
            package ignore
        '''
        rule.groupId = null
        assertNoViolations(SOURCE)

        rule.groupId = ''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''\
            package org.organization.project.component.module
        '''
        rule.groupId = 'org.organization'
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoSubmodule() {
        final SOURCE = '''\
            package org.organization
        '''
        sourceCodePath = filePath('_some_absolute_path_to_project_org_organization_MyClass.groovy')
        rule.groupId = 'org.organization'
        assertNoViolations(SOURCE)
    }

    @Test
    void testRelativePath() {
        final SOURCE = '''\
            package org.organization.project.component.module
        '''
        sourceCodePath = filePath('org_organization_project_component_module_MyClass.groovy')
        rule.groupId = 'org.organization'
        assertNoViolations(SOURCE)
    }

    @Test
    void testScriptsOk() {
        final SOURCE = '''\
            println 'Hello world!'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDifferentPackage() {
        final SOURCE = '''\
            package other.pack.age.name
        '''
        rule.groupId = 'org.organization'
        assertSingleViolation(
            SOURCE,
            1,
            'package other.pack.age.name',
            "The package source file\'s path ($sourceCodePath) should match the package itself",
        )
    }

    @Test
    void testTypoInGroupId() {
        final SOURCE = '''\
            package org.orgXnization.project.component.module
        '''
        rule.groupId = 'org.organization'
        assertSingleViolation(
            SOURCE,
            1,
            'package org.orgXnization.project.component.module',
            "The package source file\'s path ($sourceCodePath) should match the package itself",
        )
    }

    @Test
    void testTypoInSubmodule() {
        final SOURCE = '''\
            package org.organization.project.compXnent.module
        '''
        rule.groupId = 'org.organization'
        assertSingleViolation(
            SOURCE,
            1,
            'package org.organization.project.compXnent.module',
            "The package source file\'s path ($sourceCodePath) should match the package itself",
        )
    }

    @Test
    void testDuplicateOccurenceOfGroupId() {
        sourceCodePath = filePath('src_main_groovy_org_organization_project_org_component_module_MyClass.groovy')
        final SOURCE = '''\
            package org.organization.project.org.compXnent.module
        '''
        rule.groupId = 'org'
        assertSingleViolation(
            SOURCE,
            1,
            'package org.organization.project.org.compXnent.module',
            "The package source file\'s path ($sourceCodePath) should match the package itself",
        )
    }

    protected Rule createRule() {
        new PackageMatchesFilepathRule()
    }

    private String filePath(pathPattern) {
        pathPattern.tr('_', File.separator)
    }
}
