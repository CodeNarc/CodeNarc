/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule

/**
 * Parser for inline violation metadata within Rule test classes
 *
 * @author Artur Gajowy
 */
class InlineViolationsParser {

    protected static String inlineViolation(String violationMessage) {
        '#' + violationMessage.replaceAll(~/\Q#\E/, /\\#/) + PREVENT_ACCIDENTAL_ESCAPING_OF_NEXT_MARKER
    }

    private static final String PREVENT_ACCIDENTAL_ESCAPING_OF_NEXT_MARKER = ' '

    private static final String HASH_NOT_ESCAPED = /(?<!\\)#/
    private static final String HASH_NOT_ESCAPED_NOT_PART_OF_SHEBANG = /^#(?!\!)|(?<!(^|\\))#/
    private static final int KEEP_EMPTY_STRINGS = -1

    ParseResult result = new ParseResult()

    ParseResult parse(String annotatedSource) {
        annotatedSource.eachLine(1, this.&parseLine)
        result.source = result.source.replaceFirst(~/\n$/, '')
        result
    }

    private void parseLine(String lineWithInlineViolations, int lineNumber) {
        def splitByViolationMarkerPattern = lineNumber ==  1 ? HASH_NOT_ESCAPED_NOT_PART_OF_SHEBANG : HASH_NOT_ESCAPED
        def splitByMarker = lineWithInlineViolations.split(splitByViolationMarkerPattern, KEEP_EMPTY_STRINGS)
        def (sourceLine, violationMessages) = [splitByMarker.head(), splitByMarker.tail()]
        result.source += sourceLine.replaceAll(~/\s+$/, '') + '\n'
        result.violations += violationMessages.collect {
            createViolation(lineNumber, sourceLine.trim(), unescape(it.trim()))
        }
    }

    private String unescape(String violationMessage) {
        return violationMessage.replaceAll(~/\Q\#\E/, '#')
    }

    private Map createViolation(int lineNumber, String sourceLine, String message) {
        [lineNumber: lineNumber, sourceLineText: sourceLine, messageText: message]
    }

    static class ParseResult {
        String source = ''
        List<Map> violations = []
    }
}
