/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.source

/**
 * SourceCode implementation that uses source from a pre-defined String
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SourceString extends AbstractSourceCode {

    String path
    String name
    private String source

    /**
     * Construct a new instance for the file at the specified path
     * @param source - the source; must not be null or empty
     * @param path - the path for the source code; may be null; defaults to null
     * @param name - the name for the source code; may be null; defaults to null
     */
    SourceString(String source, String path=null, String name=null) {
        assert source
        this.source = source
        this.path = path
        this.name = name
    }

    /**
     * @return the full text of the source code
     */
    public String getText() {
        return source
    }

    String toString() {
        return "SourceString[$source]"
    }

}