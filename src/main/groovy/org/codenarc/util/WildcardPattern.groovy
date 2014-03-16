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
 * Represents a string pattern that may optionally include wildcards ('*', '**' or '?'), and
 * provides an API to determine whether that pattern matches a specified input string.
 * <p/>
 * The wildcard character '*' within the pattern matches a sequence of zero or more characters within a
 * single file or directory name in the input string. It does not match a sequence of two or more
 * dir/file names. For instance, 'a*b' matches 'a12345b' and 'ab', but does NOT match 'a/b' or 'a123/b'.
 * <p/>
 * The '**' wildcard matches any sequence of zero or more characters in the input string, including
 * directory names and separators . It matches any part of the directory tree. For instance, 'a**b'
 * matches 'a12345b', 'ab', 'a/b' and 'a1/a2/a3b'.
 * <p/>
 * The wildcard character '?' within the pattern matches exactly one character in the input string,
 * excluding the normalized file separator character ('/').
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
  */
class WildcardPattern {

    private final List regexes = []
    private final List strings = []
    private final defaultMatches

    /**
     * Construct a new WildcardPattern instance on a single pattern or a comma-separated list of patterns.
     * @param patternString - the pattern string, optionally including wildcard characters ('*' or '?');
     *      may optionally contain more than one pattern, separated by commas; may be null or empty to always match
     * @param defaultMatches - a boolean indicating whether <code>matches()</code> should
     *      return true if the pattern string is either empty or null. This parameter is
     *      optional and defaults to <code>true</code>.
     */
    WildcardPattern(String patternString, boolean defaultMatches=true) {
        this.defaultMatches = defaultMatches
        def patterns = patternString ? patternString.tokenize(',') : []
        patterns.each { pattern -> 
            if (containsWildcards(pattern)) {
                regexes << convertStringWithWildcardsToRegex(pattern.trim())
            }
            else {
                strings << pattern.trim()
            }
        }
    }

    /**
     * Return true if the specified String matches the pattern or if the original
     * patternString (specified in the constructor) was null or empty and the
     * value for defaultMatches (also specified in the constructor) was true.
     * @param string - the String to check
     * @return true if the String matches the pattern
     */
    boolean matches(String string) {
        if (regexes.empty && strings.empty) {
            return defaultMatches
        }
        regexes.find { regex -> string ==~ regex } ||
            strings.contains(string)
    }

    /**
     * Return true if the specified String contains one or more wildcard characters ('?' or '*')
     * @param string - the String to check
     * @return true if the String contains wildcards
     */
    private static boolean containsWildcards(String string) {
        string =~ /\*|\?/
    }

    /**
     * Convert the specified String, optionally containing wildcards (? or *), to a regular expression String
     *
     * @param stringWithWildcards - the String to convert, optionally containing wildcards (? or *)
     * @return an equivalent regex String
     *
     * @throws AssertionError - if the stringWithWildcards is null
     */
    @SuppressWarnings('DuplicateLiteral')
    private static String convertStringWithWildcardsToRegex(String stringWithWildcards) {
        assert stringWithWildcards != null

        def result = new StringBuffer()
        def prevCharWasStar = false
        stringWithWildcards.each { ch ->
            switch (ch) {
                case '*':
                    // Single '*' matches single dir/file; Double '*' matches sequence of zero or more dirs/files
                    result << (prevCharWasStar ? /.*/ : /[^\/]*/) 
                    prevCharWasStar = !prevCharWasStar
                    break
                case '?':
                    // Any character except the normalized file separator ('/')
                    result << /[^\/]/
                    break
                case ['$', '|', '[', ']', '(', ')', '.', ':', '{', '}', '\\', '^', '+']:
                    result << '\\' + ch
                    break
                default: result << ch
            }
        }
        result
    }

}
