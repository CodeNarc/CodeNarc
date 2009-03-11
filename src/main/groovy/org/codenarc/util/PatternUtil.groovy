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
package org.codenarc.util

/**
 * Contains static utility methods related to pattern-matching and regular expressions.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class PatternUtil {

    /**
     * Return true if the specified String contains one or more wildcard characters ('?' or '*')
     * @param string - the String to check
     * @return true if the String contains wildcards
     */
    static boolean containsWildcards(String string) {
        return string =~ /\*|\?/
    }

    /**
     * Convert the specified String, optionally containing wildcards (? or *), to a regular expression String
     *
     * @param stringWithWildcards - the String to convert, optionally containing wildcards (? or *)
     * @return an equivalent regex String
     *
     * @throws AssertionError - if the stringWithWildcards is null
     */
    static String convertStringWithWildcardsToRegex(String stringWithWildcards) {
        assert stringWithWildcards != null

        def result = new StringBuffer()
        stringWithWildcards.each {ch ->
            switch (ch) {
                case '*':
                    result << '.*'
                    break;
                case '?':
                    result << '.'
                    break;
                case ['$', '|', '[', ']', '(', ')', '.', ':', '{', '}', '\\', '^']:
                    result << '\\' + ch
                    break;
                default: result << ch
            }
        }
        println result

        return result
    }

    /**
     * Private constructor to prevent instantiation. All members are static.
     */
    private PatternUtil() {
    }
}