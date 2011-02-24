/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessarySemicolonRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class UnnecessarySemicolonRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnnecessarySemicolon'
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testSuccessScenario() {
        final SOURCE = '''
/*
 *
 * (the "License");
* (the "License");
   * (the "License");
 * you may not use this file except in compliance with the License.
 *
 */

            //comment is not a violation;

        	package foo
            import java.lang.String
            println(value)
            println(value); println (otherValue)

            println(value); // comment so no violation

            @SuppressWarnings('UnnecessarySemicolon')
            def method() {
                ;
            }

        '''
        assertNoViolations(SOURCE)
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testSuppressWarningsOnImport() {
        final SOURCE = '''

            @SuppressWarnings('UnnecessarySemicolon')
            import java.lang.String

            println(value);

            class MyClass {
                def method() {
                    println(value);
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testSuppressWarningsOnClass() {
        final SOURCE = '''

            import java.lang.String

            @SuppressWarnings('UnnecessarySemicolon')
            class MyClass {
                def method() {
                    println(value);
                }
            }

            class MyClass2 {
                def method() {
                    println(value);
                }
            }
        '''
        assertSingleViolation(SOURCE, 14, 'println(value);', 'Semi-colons as line endings can be removed safely')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testPackage() {
        final SOURCE = '''
            package my.company.server;
        '''
        assertSingleViolation(SOURCE, 2, 'package my.company.server;', 'Semi-colons as line endings can be removed safely')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testLoop() {
        final SOURCE = '''
            for (def x : list);
        '''
        assertSingleViolation(SOURCE, 2, 'for (def x : list);', 'Semi-colons as line endings can be removed safely')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testMethodCall() {
        final SOURCE = '''
            println(value) ;
        '''
        assertSingleViolation(SOURCE, 2, 'println(value) ;', 'Semi-colons as line endings can be removed safely')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testImport() {
        final SOURCE = '''
            import java.lang.String;    
        '''
        assertSingleViolation(SOURCE, 2, 'import java.lang.String;', 'Semi-colons as line endings can be removed safely')
    }

    protected Rule createRule() {
        new UnnecessarySemicolonRule()
    }
}