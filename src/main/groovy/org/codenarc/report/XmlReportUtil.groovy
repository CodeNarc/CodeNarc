/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.report

/**
 * Static utility methods for XML Report Writers
 */
class XmlReportUtil {

    static Closure cdata(String text) {
        return {
            unescaped << '<![CDATA['
            mkp.yield(text)
            unescaped << ']]>'
        }
    }

    static String removeIllegalCharacters(String string) {
        // See http://www.w3.org/TR/xml/#charsets
        // See http://stackoverflow.com/questions/730133/invalid-characters-in-xml
        // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
        final REGEX = /[^\x09\x0A\x0D\x20-\uD7FF\uE000-\uFFFD\u10000-\u10FFFF]/
        return string.replaceAll(REGEX, '')
    }

}
