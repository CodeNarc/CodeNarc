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

import org.codenarc.analyzer.SuppressionAnalyzer

/**
 * The SourceCode implementation for a single file.
 * Note that the path is normalized: file separator chars are normalized to standard '/'.
 *
 * @author Chris Mair
  */
class SourceFile extends AbstractSourceCode {

    private final File file
    private final path
    private String text

    /**
     * Construct a new instance for the file at the specified path
     * @param path - the path of the file; must not be null or empty
     */
    SourceFile(File file) {
        assert file
        this.file = file
        this.path = normalizePath(file.path)
        setSuppressionAnalyzer(new SuppressionAnalyzer(this))
    }

    /**
     * @return the filename for this source file, excluding path
     */
    String getName() {
        file.name
    }

    /**
     * @return the normalized path for this source file, including filename
     */
    String getPath() {
        path
    }

    /**
     * @return the full text of the source code
     */
    String getText() {
        if (text == null) {
            text = file.text
        }
        text
    }

    String toString() {
        "SourceFile[$file.absolutePath]"
    }

}
