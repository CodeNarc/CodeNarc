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
 * Represents a string pattern that may optionally include wildcard characters ('*' or '?'), and
 * provides an API to determine whether that pattern matches a specified input string.
 * <p/>
 * The wildcard character '*' within the pattern matches a sequence of zero or more characters in the input
 * string. The wildcard character '?' within the pattern matches exactly one character in the input string.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class WildcardPattern {
    private String pattern
    private String regex

    /**
     * Construct a new WildcardPattern instance.
     * @param pattern - the pattern string, optionally including wildcard characters ('*' or '?'); must not be null
     */
    WildcardPattern(String pattern) {
        assert pattern != null
        this.pattern = pattern
        this.regex = containsWildcards(pattern) ? convertStringWithWildcardsToRegex(pattern) : null

    }

    /**
     * Return true if the specified String matches the pattern
     * @param string - the String to check
     * @return true if the String matches the pattern
     */
    boolean matches(String string) {
        return regex ? string ==~ regex : string == pattern
    }

    /**
     * Return true if the specified String contains one or more wildcard characters ('?' or '*')
     * @param string - the String to check
     * @return true if the String contains wildcards
     */
    private static boolean containsWildcards(String string) {
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
    private static String convertStringWithWildcardsToRegex(String stringWithWildcards) {
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
        return result
    }

}