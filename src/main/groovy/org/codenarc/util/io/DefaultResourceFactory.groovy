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

/**
 * Default implementation of ResourceFactory.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
  */
class DefaultResourceFactory implements ResourceFactory {

    private static final String CLASSPATH_PREFIX = 'classpath:'

    /**
     * Return a Resource instance suitable for the specified path.
     * @param path - the path to the resource. Must not be null or empty. This may be
     *      optionally prefixed by "classpath:" or any of the valid java.net.URL prefixes
     *      (e.g., "file:", "http:")
     * @throws IOException - if an error occurs opening the InputStream
     */
    Resource getResource(String path) throws IOException {
        assert path
        if (path.startsWith(CLASSPATH_PREFIX)) {
            return new ClassPathResource(path - CLASSPATH_PREFIX)
        }

        isUrl(path) ? new UrlResource(path) : new ClassPathResource(path)
    }

    private isUrl(String path) {
        path =~ /.*\:.*/
    }
}
