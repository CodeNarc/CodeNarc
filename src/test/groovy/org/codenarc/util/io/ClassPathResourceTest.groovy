/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.util.io

import org.codenarc.test.AbstractTestCase
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFail
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for ClassPathResource
 *
 * @author Chris Mair
  */
class ClassPathResourceTest extends AbstractTestCase {

    private static final TEXT_FILE = 'resource/SampleResource.txt'
    private static final FILE_DOES_NOT_EXIST = 'bad/DoesNotExist.txt'
    private static final TEXT_FILE_CONTENTS = 'abcdef12345'

    @Test
    void testConstructor_NullOrEmpty() {
        shouldFailWithMessageContaining('path') { new ClassPathResource(null) }
        shouldFailWithMessageContaining('path') { new ClassPathResource('') }
    }

    @Test
    void testGetPath() {
        def resource = new ClassPathResource(TEXT_FILE)
        assert resource.getPath() == TEXT_FILE
    }

    @Test
    void testGetInputStream() {
        def resource = new ClassPathResource(TEXT_FILE)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
    }

    @Test
    void testGetInputStream_FileDoesNotExist() {
        def resource = new ClassPathResource('DoesNotExist.txt')
        shouldFail(IOException) { resource.getInputStream() }
    }

    @Test
    void testGetInputStream_TwiceOnTheSameResource() {
        def resource = new ClassPathResource(TEXT_FILE)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
        assert resource.getInputStream().text == TEXT_FILE_CONTENTS
    }

    @Test
    void testGetInputStream_Static() {
        assert ClassPathResource.getInputStream(TEXT_FILE).text == TEXT_FILE_CONTENTS
    }

    @Test
    void testGetInputStream_Static_FileDoesNotExist() {
        shouldFail(IOException) { ClassPathResource.getInputStream('DoesNotExist.txt') }
    }

    @Test
    void testGetInputStream_Static_NullOrEmpty() {
        shouldFailWithMessageContaining('path') { ClassPathResource.getInputStream(null) }
        shouldFailWithMessageContaining('path') { ClassPathResource.getInputStream('') }
    }

    @Test
    void testExists_DoesExist() {
        def resource = new ClassPathResource(TEXT_FILE)
        assert resource.exists()
    }

    @Test
    void testExists_DoesNotExist() {
        def resource = new ClassPathResource(FILE_DOES_NOT_EXIST)
        assert !resource.exists()
    }
}
