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

import org.codenarc.test.AbstractTest

/**
 * Tests for PatternUtil
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class PatternUtilTest extends AbstractTest {

    void testConvertStringWithWildcardsToRegex() {
        assert PatternUtil.convertStringWithWildcardsToRegex('abc') == /abc/
        assert PatternUtil.convertStringWithWildcardsToRegex('abc.def') == /abc\.def/
        assert PatternUtil.convertStringWithWildcardsToRegex('(abc):{def}') == /\(abc\)\:\{def\}/
        assert PatternUtil.convertStringWithWildcardsToRegex('|[23]^a$b') == /\|\[23\]\^a/ + '\\$b'

        assert PatternUtil.convertStringWithWildcardsToRegex('*.txt') == /.*\.txt/
        assert PatternUtil.convertStringWithWildcardsToRegex('abc*') == /abc.*/
        assert PatternUtil.convertStringWithWildcardsToRegex('??x?.*') == /..x.\..*/
    }

    void testContainsWildcards() {
        assert !PatternUtil.containsWildcards('')
        assert !PatternUtil.containsWildcards('abc')
        assert !PatternUtil.containsWildcards('abc.def')

        assert PatternUtil.containsWildcards('*.txt')
        assert PatternUtil.containsWildcards('abc.*_OLD')
        assert PatternUtil.containsWildcards('a??.txt')
        assert PatternUtil.containsWildcards('?a*.*HH???')
    }
}