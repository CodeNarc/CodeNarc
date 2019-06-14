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

import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for XmlReportUtil
 */
class XmlReportUtilTest extends AbstractTestCase {

    @Test
    void testRemoveIllegalCharacters() {
        assert XmlReportUtil.removeIllegalCharacters('\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008') == ''
        assert XmlReportUtil.removeIllegalCharacters('\uD800') == ''

        // Valid chars
        assert XmlReportUtil.removeIllegalCharacters('') == ''
        assert XmlReportUtil.removeIllegalCharacters('01234567890 ABC abc') == '01234567890 ABC abc'
        assert XmlReportUtil.removeIllegalCharacters('!@#$%^&*()-_=+[]{};\'",./<>?') == '!@#$%^&*()-_=+[]{};\'",./<>?'
        assert XmlReportUtil.removeIllegalCharacters('\u0009') == '\u0009'
        assert XmlReportUtil.removeIllegalCharacters('\t\n\r') == '\t\n\r'
        assert XmlReportUtil.removeIllegalCharacters('\uE000') == '\uE000'
    }

}
