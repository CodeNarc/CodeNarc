/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.security

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for FileCreateTempFileRule
 *
 * @author Hamlet D'Arcy
  */
class FileCreateTempFileRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'FileCreateTempFile'
    }

    @Test
    void testApplyTo_Violation_Initializers() {
        final SOURCE = '''
            class MyClass {
                static {
                    File.createTempFile(null, null)
                }
                {
                    File.createTempFile(null, null, null)
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'File.createTempFile(null, null)', 'The method File.createTempFile is insecure. Use a secure API such as that provided by ESAPI',
                7, 'File.createTempFile(null, null, null)', 'The method File.createTempFile is insecure. Use a secure API such as that provided by ESAPI')
    }

    @Test
    void testApplyTo_Violation_Methods() {
        final SOURCE = '''
            class MyClass {
                static def method1() {
                    File.createTempFile(null, null)
                }
                def method2() {
                    File.createTempFile(null, null, null)
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'File.createTempFile(null, null)', 'The method File.createTempFile is insecure. Use a secure API such as that provided by ESAPI',
                7, 'File.createTempFile(null, null, null)', 'The method File.createTempFile is insecure. Use a secure API such as that provided by ESAPI')
    }

    @Test
    void testApplyTo_Violation_Closures() {
        final SOURCE = '''
            File.createTempFile('a', 'b')
            def method = {
                File.createTempFile('a', 'b')
            }
        '''
        assertTwoViolations(SOURCE,
                2, "File.createTempFile('a', 'b')",
                4, "File.createTempFile('a', 'b')")
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    file.createTempFile('', '')
                    File.createTempFile2('', '', '')
                    File.createTempFile('')
                    File.createTempFile('', '', '', '')
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new FileCreateTempFileRule()
    }
}
