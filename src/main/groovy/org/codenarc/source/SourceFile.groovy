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
 * The SourceCode implementation for a single file 
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class SourceFile extends AbstractSourceCode {

    private File file
    private String text

    /**
     * Construct a new instance for the file at the specified path
     * @param path - the path of the file; must not be null or empty
     */
    SourceFile(File file) {
        assert file
        this.file = file
    }

    /**
     * @return the filename for this source file, excluding path
     */
    String getName() {
        return file.name
    }

    /**
     * @return the path for this source file, including filename
     */
    String getPath() {
        return file.path
    }

    /**
     * @return the full text of the source code
     */
    String getText() {
        if (text == null) {
            text = file.text
        }
        return text
    }

    String toString() {
        return "SourceFile[$file.absolutePath]"
    }

}