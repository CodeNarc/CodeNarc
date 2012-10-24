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

import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for DefaultResourceFactory
 *
 * @author Chris Mair
  */
class DefaultResourceFactoryTest extends AbstractTestCase {

    private static final PATH = 'src/test/resources/resource/SampleResource.txt'
    private resourceFactory

    @Test
    void testConstructor_NullOrEmpty() {
        shouldFailWithMessageContaining('path') { resourceFactory.getResource(null) }
        shouldFailWithMessageContaining('path') { resourceFactory.getResource('') }
    }

    @Test
    void testGetResource_NoPrefix() {
        assertResourceTypeAndLocation(PATH, ClassPathResource)
    }

    @Test
    void testGetResource_HttpPrefix() {
        assertResourceTypeAndLocation('http://codenarc.org', UrlResource)
    }

    @Test
    void testGetResource_FtpPrefix() {
        assertResourceTypeAndLocation('ftp://codenarc.org', UrlResource)
    }

    @Test
    void testGetResource_ClassPathPrefix() {
        assertResourceTypeAndLocation('classpath:' + PATH, ClassPathResource, PATH)
    }

    private void assertResourceTypeAndLocation(String path, Class resourceClass, String expectedResourcePath=path) {
        def resource = resourceFactory.getResource(path)
        assert resource.class == resourceClass
        assert resource.getPath() == expectedResourcePath
    }

    @Before
    void setUpDefaultResourceFactoryTest() {
        resourceFactory = new DefaultResourceFactory()
    }
}
