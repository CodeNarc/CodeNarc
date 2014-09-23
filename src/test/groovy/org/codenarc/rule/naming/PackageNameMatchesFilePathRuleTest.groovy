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
 * Tests for PackageMismatchesFilepathRule
 *
 * @author Simon Tost
 */
class PackageNameMatchesFilePathRuleTest extends AbstractRuleTestCase {

    @Before
    void setup() {
        sourceCodePath = '/some/absolute/path/to/project/org/organization/project/component/module/MyClass.groovy'
        sourceCodeName = 'MyClass.groovy'
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'PackageNameMatchesFilePath'
    }

    @Test
    void testGroupId_NullOrEmpty() {
        final SOURCE = '''\
            package ignore
        '''
        rule.groupId = null
        assertNoViolations(SOURCE)
        assert !rule.ready

        rule.groupId = ''
        assertNoViolations(SOURCE)
        assert !rule.ready
    }

    @Test
    void testSourceCodePath_NullOrEmpty() {
        final SOURCE = '''\
            package ignore
        '''
        rule.groupId = 'org.organization'

        sourceCodePath = null
        assertNoViolations(SOURCE)
        assert rule.ready

        sourceCodePath = ''
        assertNoViolations(SOURCE)
        assert rule.ready
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
        sourceCodePath = '/some/absolute/path/to/project/org/organization/MyClass.groovy'
        rule.groupId = 'org.organization'
        assertNoViolations(SOURCE)
    }

    @Test
    void testRelativePath() {
        final SOURCE = '''\
            package org.organization.project.component.module
        '''
        sourceCodePath = 'org/organization/project/component/module/MyClass.groovy'
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
            "Could not find groupId 'org.organization' in package (other.pack.age.name) or file's path ($sourceCodePath)",
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
            "Could not find groupId 'org.organization' in package (org.orgXnization.project.component.module) or file's path ($sourceCodePath)",
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
            "The package source file's path (${filePath('org_organization_project_component_module')}) should match the package declaration",
        )
    }

    @Test
    void testMissingSubpackage() {
        final SOURCE = '''\
            package org.organization.project.component
        '''
        rule.groupId = 'org.organization'
        assertSingleViolation(
            SOURCE,
            1,
            'package org.organization.project.component',
            "The package source file's path (${filePath('org_organization_project_component_module')}) should match the package declaration",
        )
    }

    @Test
    void testExtraSubpackage() {
        final SOURCE = '''\
            package org.organization.project.component.module.extra
        '''
        rule.groupId = 'org.organization'
        assertSingleViolation(
            SOURCE,
            1,
            'package org.organization.project.component.module.extra',
            "The package source file's path (${filePath('org_organization_project_component_module')}) should match the package declaration",
        )
    }

    @Test
    void testDuplicateOccurrenceOfGroupId() {
        sourceCodePath = filePath('src_main_groovy_org_organization_project_org_component_module_MyClass.groovy')
        final SOURCE = '''\
            package org.organization.project.org.compXnent.module
        '''
        rule.groupId = 'org'
        assertSingleViolation(
            SOURCE,
            1,
            'package org.organization.project.org.compXnent.module',
            "The package source file's path (${filePath('org_organization_project_org_component_module')}) should match the package declaration",
        )
    }

    protected Rule createRule() {
        new PackageNameMatchesFilePathRule()
    }

    private String filePath(pathPattern) {
        pathPattern.tr('_', File.separator)
    }
}
