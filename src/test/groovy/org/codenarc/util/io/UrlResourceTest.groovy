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
 * Tests for UrlResource
 *
 * @author Chris Mair
 * @version $Revision: 181 $ - $Date: 2009-07-11 18:32:34 -0400 (Sat, 11 Jul 2009) $
 */
class UrlResourceTest extends AbstractTest {

    private static final TEXT_FILE = 'src/test/resources/resource/SampleResource.txt'
    private static final TEXT_FILE_CONTENTS = 'abcdef12345'
    private urlName

    void testConstructor_NullOrEmpty() {
        shouldFailWithMessageContaining('path') { new UrlResource(null) }
        shouldFailWithMessageContaining('path') { new UrlResource('') }
    }

    void testGetPath() {
        def resource = new UrlResource(TEXT_FILE)
        assert resource.getPath() == TEXT_FILE
    }

    void testGetInputStream_File() {
        def resource = new UrlResource(urlName)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
    }

    // Can't assume always-on internet access
    void DISABLE_testGetInputStream_Http() {
        def resource = new UrlResource('http://google.com')
        def inputStream = resource.getInputStream()
        assert inputStream.text.contains('Google')
    }

    void testGetInputStream_MalformedUrlName() {
        def resource = new UrlResource('DoesNotExist.txt')
        shouldFail(MalformedURLException) { resource.getInputStream() }
    }

    void testGetInputStream_ResourceDoesNotExist() {
        def resource = new UrlResource('file:///DoesNotExist.txt')
        shouldFail(IOException) { resource.getInputStream() }
    }

    void testGetInputStream_TwiceOnTheSameResource() {
        def resource = new UrlResource(urlName)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
        assert resource.getInputStream().text == TEXT_FILE_CONTENTS
    }

    void setUp() {
        def file = new File(TEXT_FILE)
        def absPath = file.absolutePath
        urlName = "file:///" + absPath
        log("urlName=$urlName")
        super.setUp()
    }
}