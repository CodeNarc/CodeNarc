/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.plugin.disablerules

import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for LookupTable
 */
class LookupTableTest extends AbstractTestCase {

    private static final String CODENARC_DISABLE = 'codenarc-disable'
    private static final String CODENARC_DISABLE_LINE = 'codenarc-disable-line'
    private static final String CODENARC_ENABLE = 'codenarc-enable'

    @Test
    void test_parseRuleNames() {
        assert LookupTable.parseRuleNames('abc', CODENARC_DISABLE) == [] as Set

        assert LookupTable.parseRuleNames('//codenarc-disable', CODENARC_DISABLE) == [] as Set
        assert LookupTable.parseRuleNames('//codenarc-disable          ', CODENARC_DISABLE) == [] as Set
        assert LookupTable.parseRuleNames('// codenarc-disable A', CODENARC_DISABLE) == ['A'] as Set
        assert LookupTable.parseRuleNames('  // codenarc-disable A, B  ', CODENARC_DISABLE) == ['A', 'B'] as Set
        assert LookupTable.parseRuleNames('println 123 // codenarc-disable A,B,C  ', CODENARC_DISABLE) == ['A', 'B', 'C'] as Set
        assert LookupTable.parseRuleNames(' /*codenarc-disable    A,   B  */', CODENARC_DISABLE) == ['A', 'B'] as Set
        assert LookupTable.parseRuleNames('// codenarc-disable A,B,A , B', CODENARC_DISABLE) == ['A', 'B'] as Set

        assert LookupTable.parseRuleNames('//  codenarc-enable        ', CODENARC_ENABLE) == [] as Set
        assert LookupTable.parseRuleNames('//codenarc-enable A  ,  B', CODENARC_ENABLE) == ['A', 'B'] as Set

        assert LookupTable.parseRuleNames('//codenarc-disable-line    ', CODENARC_DISABLE_LINE) == [] as Set
        assert LookupTable.parseRuleNames('// codenarc-disable-line    A', CODENARC_DISABLE_LINE) == ['A'] as Set
        assert LookupTable.parseRuleNames('  // codenarc-disable-line A,    B  ', CODENARC_DISABLE_LINE) == ['A', 'B'] as Set
    }

}
