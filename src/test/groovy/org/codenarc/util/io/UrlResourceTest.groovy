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
 * Tests for UrlResource
 *
 * @author Chris Mair
  */
class UrlResourceTest extends AbstractTestCase {

    private static final TEXT_FILE = 'src/test/resources/resource/SampleResource.txt'
    private static final RELATIVE_FILE_URL = 'file:' + TEXT_FILE
    private static final URL_DOEST_NOT_EXIST = 'file:bad/DoesNotExist.txt'
    private static final TEXT_FILE_CONTENTS = 'abcdef12345'

    @Test
    void testConstructor_NullOrEmpty() {
        shouldFailWithMessageContaining('path') { new UrlResource(null) }
        shouldFailWithMessageContaining('path') { new UrlResource('') }
    }

    @Test
    void testGetPath() {
        def resource = new UrlResource(TEXT_FILE)
        assert resource.getPath() == TEXT_FILE
    }

    @Test
    void testGetInputStream_File_AbsolutePath() {
        def file = new File(TEXT_FILE)
        def urlName = 'file:' + file.absolutePath
        log("urlName=$urlName")
        def resource = new UrlResource(urlName)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
    }

    @Test
    void testGetInputStream_File_RelativePath() {
        def resource = new UrlResource(RELATIVE_FILE_URL)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
    }

    // Can't assume always-on internet access
//    void testGetInputStream_Http() {
//        def resource = new UrlResource('http://google.com')
//        def inputStream = resource.getInputStream()
//        assert inputStream.text.contains('Google')
//    }

    @Test
    void testGetInputStream_MalformedUrlName() {
        def resource = new UrlResource('DoesNotExist.txt')
        shouldFail(MalformedURLException) { resource.getInputStream() }
    }

    @Test
    void testGetInputStream_ResourceDoesNotExist() {
        def resource = new UrlResource('file:///DoesNotExist.txt')
        shouldFail(IOException) { resource.getInputStream() }
    }

    @Test
    void testGetInputStream_TwiceOnTheSameResource() {
        def resource = new UrlResource(RELATIVE_FILE_URL)
        def inputStream = resource.getInputStream()
        assert inputStream.text == TEXT_FILE_CONTENTS
        assert resource.getInputStream().text == TEXT_FILE_CONTENTS
    }

    @Test
    void testExists_DoesExist() {
        def resource = new UrlResource(RELATIVE_FILE_URL)
        assert resource.exists()
    }

    @Test
    void testExists_DoesNotExist() {
        def resource = new UrlResource(URL_DOEST_NOT_EXIST)
        assert !resource.exists()
    }
}
