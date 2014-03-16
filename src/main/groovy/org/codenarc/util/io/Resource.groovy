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
 * Defines the interface for objects that represent a resource (e.g. a file) and
 * provide access to its InputStream.
 * <p/>
 * The design of this (resource) framework is heavily influenced by the Resource classes 
 * within The Spring Framework.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
  */
interface Resource {

    /**
     * Return the InputStream for this resource.
     * @throws IOException - if an error occurs opening the InputStream
     */
    InputStream getInputStream() throws IOException

    /**
     * @return true only if this resource exists and is accessible
     */
    boolean exists()
}
