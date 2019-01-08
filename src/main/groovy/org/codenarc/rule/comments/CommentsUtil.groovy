/*
 * Copyright 2019 the original author or authors.
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
package org.codenarc.rule.comments

/**
 * Utility methods and constants related to comments rules
 *
 * @author Chris Mair
 */
class CommentsUtil {

    protected static final String OPTIONAL_WHITESPACE =  /\s*/
    protected static final String NEW_LINE =  /\n/
    protected static final String RELUCTANT =  '?'

    protected static final String JAVADOC_START =  /\/\*\*\s*\n/
    protected static final String JAVADOC_ANY_LINES =  /(\s*\*.*\n)*/ + RELUCTANT
    protected static final String JAVADOC_EMPTY_LINE =  /\s*\*\s*\n/
    protected static final String JAVADOC_END =  /\s*\*\//
    protected static final String JAVADOC_LINE_PREFIX =  /\s*\*\s*/

    protected static String group(String inside) {
        return '(' + inside + ')'
    }

    // Prevent instantiation. All members are static.
    private CommentsUtil() { }
}
