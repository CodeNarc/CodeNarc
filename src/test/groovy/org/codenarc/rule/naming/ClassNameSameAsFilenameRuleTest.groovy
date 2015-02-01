/*
 * Copyright 2013 the original author or authors.
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
 * Tests for ClassNameSameAsFilenameRule
 *
 * @author Artur Gajowy
 * @author Chris Mair
 */
class ClassNameSameAsFilenameRuleTest extends AbstractRuleTestCase {

    static skipTestThatUnrelatedCodeHasNoViolations
    
    @Before
    void setup() {
        sourceCodeName = 'SameAsFilename.groovy'
    }
    
    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassNameSameAsFilename'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	class SameAsFilename {
        	    static class Foo {}
        	}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSourceCodeName_NullOrEmpty() {
        final SOURCE = '''
        	class Ignore { }
        '''
        sourceCodeName = null
        assertNoViolations(SOURCE)

        sourceCodeName = ''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MultipleClasses_NoViolations() {
        final SOURCE = '''
        	class SameAsFilename {}
        	enum NotSameAsFilename {}
        '''
        assertNoViolations(SOURCE)
    }
    
    @Test
    void test_ScriptFile_NoViolations() {
        final SOURCE = '''
        	println 'Hello world!'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class NotSameAsFilename {}
        '''
        assertSingleViolation(SOURCE, 2, 'class NotSameAsFilename {}', violationMessage('Class', 'NotSameAsFilename'))
    }

    @Test
    void testEnumsChecked() {
        final SOURCE = '''
            enum NotSameAsFilename {}
        '''
        assertSingleViolation(SOURCE, 2, '', violationMessage('Enum', 'NotSameAsFilename'))
    }

    @Test
    void testInterfacesChecked() {
        final SOURCE = '''
            interface NotSameAsFilename {}
        '''
        assertSingleViolation(SOURCE, 2, 'interface NotSameAsFilename {}', violationMessage('Interface', 'NotSameAsFilename'))
    }

    @Test
    void testNestedClassesNotCountedAsTopLevel() {
        final SOURCE = '''
            class NotSameAsFilename {
                enum Foo {}
                class Baz {}
                static class FooBar {}
            }
        '''
        assertSingleViolation(SOURCE, 2, 'class NotSameAsFilename {', violationMessage('Class', 'NotSameAsFilename'))
    }

    private String violationMessage(String sourceUnitType, String violatingClass) {
        "$sourceUnitType `$violatingClass` is the only class in `$sourceCodeName`. " +
            'In such a case the file and the class should have the same name.'
    }

    protected Rule createRule() {
        new ClassNameSameAsFilenameRule()
    }
}
