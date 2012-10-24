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
 * Tests for JavaIoPackageAccessRule
 *
 * @author 'Hamlet D'Arcy'
  */
class JavaIoPackageAccessRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JavaIoPackageAccess'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            fileSystem.getFileSystem() //just a variable reference, not a FileSystem reference
            fileSystem.fileSystem.delete(aFile)

            new File() // no arg ctor allowed, b/c they don't exist in JDK
            new File(one, two, three) // three arg ctor allowed, b/c they don't exist in JDK

            new FileReader()  // no arg ctor allowed, b/c they don't exist in JDK
            new FileReader(one, two, three) // three arg ctor allowed, b/c they don't exist in JDK

            new FileOutputStream()  // no arg ctor allowed, b/c they don't exist in JDK
            new FileOutputStream(one, two, three)              new FileOutputStream(one, two, three)

            new RandomAccessFile(parm)
            new RandomAccessFile(one, two, three)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testFileOutputStream() {
        final SOURCE = '''
            new FileOutputStream(name)
            new FileOutputStream(name, true)
        '''
        assertTwoViolations(SOURCE,
                2, 'new FileOutputStream(name)', 'The use of java.io.FileOutputStream violates the Enterprise Java Bean specification',
                3, 'new FileOutputStream(name, true)', 'The use of java.io.FileOutputStream violates the Enterprise Java Bean specification')
    }

    @Test
    void testFileReader() {
        final SOURCE = '''
            new FileReader(file)
        '''
        assertSingleViolation(SOURCE,
                2, 'new FileReader(file)', 'The use of java.io.FileReader violates the Enterprise Java Bean specification')
    }

    @Test
    void testRandomAccessFile() {
        final SOURCE = '''
            new RandomAccessFile(name, parent)
        '''
        assertSingleViolation(SOURCE,
                2, 'new RandomAccessFile(name, parent)', 'The use of java.io.RandomAccessFile violates the Enterprise Java Bean specification')
    }

    @Test
    void testFile() {
        final SOURCE = '''
            new File(name)
            new File(name, parent)
        '''
        assertTwoViolations(SOURCE,
                2, 'new File(name)', 'The use of java.io.File violates the Enterprise Java Bean specification',
                3, 'new File(name, parent)', 'The use of java.io.File violates the Enterprise Java Bean specification')
    }

    @Test
    void testFileSystem() {
        final SOURCE = '''
            FileSystem.getFileSystem() // any method on FileSystem
            FileSystem.fileSystem.delete(aFile) // property access of FileSystem
        '''
        assertTwoViolations(SOURCE,
                2, 'FileSystem.getFileSystem()', 'The use of java.io.FileSystem violates the Enterprise Java Bean specification',
                3, 'FileSystem.fileSystem.delete(aFile)', 'The use of java.io.FileSystem violates the Enterprise Java Bean specification')
    }

    protected Rule createRule() {
        new JavaIoPackageAccessRule()
    }
}
