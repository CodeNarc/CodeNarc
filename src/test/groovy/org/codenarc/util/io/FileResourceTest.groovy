/*
 * Copyright 2009 the original author or authors.
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

import org.codenarc.test.AbstractTest

/**
 * Tests for FileResource
 *
 * @author Chris Mair
 * @version $Revision: 181 $ - $Date: 2009-07-11 18:32:34 -0400 (Sat, 11 Jul 2009) $
 */
class FileResourceTest extends AbstractTest {

    private static final TEXT_FILE = 'src/test/resources/resource/SampleResource.txt'
    private static final TEXT_FILE_CONTENTS = 'abcdef12345'

    void testConstructor_NullOrEmpty() {
        shouldFailWithMessageContaining('path') { new FileResource(null) }
        shouldFailWithMessageContaining('path') { new FileResource('') }
    }

    void testGetPath() {
        def resource = new FileResource(TEXT_FILE)
        assert resource.getPath() == TEXT_FILE
    }

    void testGetInputStream() {
        def resource = new FileResource(TEXT_FILE)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
    }

    void testGetInputStream_FileDoesNotExist() {
        def resource = new FileResource('DoesNotExist.txt')
        shouldFail(IOException) { resource.getInputStream() }
    }

    void testGetInputStream_TwiceOnTheSameResource() {
        def resource = new FileResource(TEXT_FILE)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
        assert resource.getInputStream().text == TEXT_FILE_CONTENTS
    }

}