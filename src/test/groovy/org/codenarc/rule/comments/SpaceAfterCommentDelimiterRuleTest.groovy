/*
 * Copyright 2023 the original author or authors.
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

import org.junit.jupiter.api.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for SpaceAfterCommentDelimiterRule
 *
 * @author Chris Mair
 */
class SpaceAfterCommentDelimiterRuleTest extends AbstractRuleTestCase<SpaceAfterCommentDelimiterRule> {

    private static final String MESSAGE = 'The comment does not begin with space or whitespace'

    @Test
    void RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterCommentDelimiter'
    }

    @Test
    void Comments_NoViolations() {
        final SOURCE = '''
            /**
             * Sample class
             *    @author Some Developer
             *
             * See  http://www.apache.org/licenses/LICENSE-2.0
             */
            class MyClass {
                /**
                 * Return the calculated count of some stuff,
                 * starting with the specified startIndex.
                 *
                 * @param startIndex - the starting index
                 * @return the full count
                 * @throws RuntimeException when hell freezes over
                 */
                int countThings(int startIndex) { // some comment
                    // Do stuff
                }// comment
             
                // Defaults to "**/*.groovy"
                
                //---------------------------------------------
                // These are ignored
                //---------------------------------------------
                
                //==================================
                //**********************************
                
                /*-------------------------------------*/
                /*================*/
                /*$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$*/
                
                /**=======================================*/
                /**---------------------------------
                  * comment
                  */
            }
            // Other comment
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void StringsContainingCommentDelimiters_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final String TEST_FILES = 'src/**/*Test.groovy'
                
                void doStuff() {
                    String includes = '**/*.groovy'
                    String excludes = "temp/*File2*"
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void MultiLineJavadoc_CommentsWithNoSpace_NoViolations() {
        final SOURCE = '''
            /**
             *Sample class
             *@author Some Developer
             */
            class MyClass {
                /**
                 *Return the calculated count of some stuff,
                 * starting with the specified startIndex.
                 *
                 *@param startIndex - the starting index
                 *@return the full count
                 *@throws RuntimeException when hell freezes over
                 */
                int countThings(int startIndex) { } // some comment
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void URLs_NoViolations() {
        final SOURCE = '''
            /*
             * Copyright 2023 the original author or authors.
             *
             * Licensed under the Apache License, Version 2.0 (the "License");
             * you may not use this file except in compliance with the License.
             * You may obtain a copy of the License at
             *
             *      http://www.apache.org/licenses/LICENSE-2.0
             */

             // See https://www.google.com/?authuser=0
             // FTP: ftp://user:password@server/pathname
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void DollarSlashyStrings_NoViolations() {
        final SOURCE = '''
            String excludeFilePatterns = [~$//example.+/$]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void Comments_Violations() {
        final SOURCE = '''
            /**Bad1
             *Ignored .. not a violation
             *    @author Some Developer
             *
             */
            class MyClass {//Bad4
                /**
                 *Return the calculated count of some stuff,
                 */
                int countThings(int startIndex) { //some comment
                    //Do stuff
                    amount = 3 + amount/*violation*/
                }//comment
            }
            //Other comment
            /*Single line comment*/
        '''
        assertViolations(SOURCE,
            [line:2, source:'/**Bad1', message: MESSAGE],
            [line:7, source:'class MyClass {//Bad4', message: MESSAGE],
            [line:11, source:'int countThings(int startIndex) { //some comment', message: MESSAGE],
            [line:12, source:'//Do stuff', message: MESSAGE],
            [line:13, source:'amount = 3 + amount/*violation*/', message: MESSAGE],
            [line:14, source:'}//comment', message: MESSAGE],
            [line:16, source:'//Other comment', message: MESSAGE],
            [line:17, source:'/*Single line comment*/', message: MESSAGE])
    }

    @Override
    protected SpaceAfterCommentDelimiterRule createRule() {
        new SpaceAfterCommentDelimiterRule()
    }
}
