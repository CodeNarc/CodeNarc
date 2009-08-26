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
 * Tests for DefaultResourceFactory
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class DefaultResourceFactoryTest extends AbstractTest {

    private static final PATH = 'src/test/resources/resource/SampleResource.txt'
    private resourceFactory

    void testConstructor_NullOrEmpty() {
        shouldFailWithMessageContaining('path') { resourceFactory.getResource(null) }
        shouldFailWithMessageContaining('path') { resourceFactory.getResource('') }
    }

    void testGetResource_NoPrefix() {
        testGetResource(PATH, ClassPathResource)
    }

    void testGetResource_HttpPrefix() {
        testGetResource("http://codenarc.org", UrlResource) 
    }

    void testGetResource_FtpPrefix() {
        testGetResource("ftp://codenarc.org", UrlResource) 
    }

    void testGetResource_ClassPathPrefix() {
        testGetResource("classpath:" + PATH, ClassPathResource, PATH)
    }

    private void testGetResource(String path, Class resourceClass, String expectedResourcePath=path) {
        def resource = resourceFactory.getResource(path)
        assert resource.class == resourceClass
        assert resource.getPath() == expectedResourcePath
    }

    void setUp() {
        super.setUp()
        resourceFactory = new DefaultResourceFactory()
    }
}