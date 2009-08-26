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
 * A Resource implementation based on java.net.URL.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UrlResource implements Resource {
    final String path

    /**
     * Construct a new FileResource
     * @path - the filesystem path to the file. May be absolute or relative.
     */
    UrlResource(String path) {
        assert path
        this.path = path
    }

    /**
     * Open a FileInputStream on the file
     * @throws IOException - if an error occurs opening the InputStream
     */
    InputStream getInputStream() throws IOException {
        def url = new URL(path)
        return url.openStream()
    }
}