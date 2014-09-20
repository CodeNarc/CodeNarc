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
package org.codenarc.rule.formatting

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for SpaceAroundMapEntryColonRule
 *
 * @author Chris Mair
 */
class SpaceAroundMapEntryColonRuleTest extends AbstractRuleTestCase {

    private static final PRECEDED = 'preceded'
    private static final FOLLOWED = 'followed'
    private static final CLASS = 'None'
    private static final REGEX = /\S/

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAroundMapEntryColon'
        assert rule.characterBeforeColonRegex == REGEX
        assert rule.characterAfterColonRegex == REGEX
    }

    @Test
    void testMapEntryFormatting_DefaultConfiguration_NoViolations() {
        final SOURCE = '''
            Map m1 = [myKey:12345]
            def m2 = [a:123, (key):'xxx',
                c:[1,2] ]
            println [k1:[a:1], k2:[b:2, c:3]]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMapEntryFormatting_BeforeColon_CustomConfiguration_NoViolations() {
        final SOURCE = '''
            Map m1 = [myKey :12345]
            def m2 = [a :123, (key)\t:'xxx',
                c :[1,2] ]
        '''
        rule.characterBeforeColonRegex = /\s/
        assertNoViolations(SOURCE)
    }

    @Test
    void testMapEntryFormatting_AfterColon_CustomConfiguration_NoViolations() {
        final SOURCE = '''
            println [k1: [a: 1], k2: [b:\t2, c: System.currentTmeMillis()]]
        '''
        rule.characterAfterColonRegex = /\s/
        assertNoViolations(SOURCE)
    }

    @Test
    void testMapEntryFormatting_DefaultConfiguration_Violations() {
        final SOURCE = """
            Map m1 = [myKey : 12345]        ${violation('myKey', PRECEDED, CLASS, REGEX)}  \
                                            ${violation('myKey', FOLLOWED, CLASS, REGEX)}
            def m2 = [a:\t123, (key) :'xxx', ${violation('a', FOLLOWED, CLASS, REGEX)}  \
                                            ${violation('key', PRECEDED, CLASS, REGEX)}
                99:'ok', c:                 ${violation('c', FOLLOWED, CLASS, REGEX)}
                     [1,2] ]
            println [a :[1:11, 2:22],       ${violation('a', PRECEDED, CLASS, REGEX)}
                b:[(Integer): 33]]          ${violation('Integer', FOLLOWED, CLASS, REGEX)}
        """
        assertInlineViolations(SOURCE)
    }

    @Test
    void testMapEntryFormatting_CustomConfiguration_Violations() {
        final REGEX = /\s/
        final SOURCE = """
            Map m1 = [myKey:12345]          ${violation('myKey', PRECEDED, CLASS, REGEX)}  \
                                            ${violation('myKey', FOLLOWED, CLASS, REGEX)}
            def m2 = [a\t:123, (key): 'xxx', ${violation('a', FOLLOWED, CLASS, REGEX)}  \
                                            ${violation('key', PRECEDED, CLASS, REGEX)}
                99 : 'ok', c :[1,2] ]       ${violation('c', FOLLOWED, CLASS, REGEX)}
            println [a: [1 : 11, 2 : 22],   ${violation('a', PRECEDED, CLASS, REGEX)}
                b : [(Integer) :33]]         ${violation('Integer', FOLLOWED, CLASS, REGEX)}
        """
        rule.characterBeforeColonRegex = REGEX
        rule.characterAfterColonRegex = REGEX
        assertInlineViolations(SOURCE)
    }

    @Test
    void testIgnoresSpreadMapOperator_NoViolations() {
        final SOURCE = '''
            def params = [:]
            to(page, *:params)
        '''
        rule.characterAfterColonRegex = /\s/
        assertNoViolations(SOURCE)
    }

    private String violation(String keyName, String precededOrFollowed, String className, String regex) {
        return inlineViolation("The colon for the literal Map entry for key [$keyName] within class $className" +
            " is not $precededOrFollowed by a match for regular expression [$regex]")
    }

    protected Rule createRule() {
        new SpaceAroundMapEntryColonRule()
    }
}
