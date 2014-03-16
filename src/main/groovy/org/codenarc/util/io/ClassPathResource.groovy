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

/**
 * A Resource implementation for resources available on the classpath.
 * <p/>
 * This class also provides a static <code>InputStream getInputStream(String path)</code> convenience method.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
  */
class ClassPathResource implements Resource {
    final String path

    /**
     * Convenience method to open an InputStream on the specified resource path relative the classpath
     * @path - the path to the resource (file). The path is relative to the classpath,
     *      by default, but may be optionally prefixed by any of the valid java.net.URL prefixes, such
     *      as "file:" (to load from a relative or absolute path on the filesystem), or "http:". The
     *      path must not be empty or null.
     * @throws IOException - if an error occurs opening the InputStream
     */
    static InputStream getInputStream(String path) throws IOException {
        new ClassPathResource(path).getInputStream()
    }

    /**
     * Construct a new ClassPathResource
     * @path - the path to the resource (file). The path is relative to the classpath,
     *      by default, but may be optionally prefixed by any of the valid java.net.URL prefixes, such
     *      as "file:" (to load from a relative or absolute path on the filesystem), or "http:". The
     *      path must not be empty or null.
     */
    ClassPathResource(String path) {
        assert path
        this.path = path
    }

    /**
     * Open an InputStream on the classpath resource path
     * @throws IOException - if an error occurs opening the InputStream
     */
    InputStream getInputStream() throws IOException {
        def inputStream = getClass().classLoader.getResourceAsStream(path)
        if (!inputStream) {
            throw new FileNotFoundException("File [$path] does not exist or is not accessible")
        }
        inputStream
    }

    /**
     * @return true only if this resource exists and is accessible
     */
    boolean exists() {
        def inputStream
        try {
            inputStream = getClass().classLoader.getResourceAsStream(path)
            return inputStream != null
        }
        catch (IOException e) {
            return false
        }
        finally {
            if (inputStream) {
                inputStream.close()
            }
        }
    }
}
