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

import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for WildcardPattern
 *
 * @author Chris Mair
  */
class WildcardPatternTest extends AbstractTestCase {

    @Test
    void testMatches_DefaultMatches_True() {
        assert new WildcardPattern(null, true).matches('')
        assert new WildcardPattern(null, true).matches('abc')
        assert new WildcardPattern('', true).matches('')
        assert new WildcardPattern('', true).matches('abc')
        assert new WildcardPattern('a', true).matches('a')

        assert !new WildcardPattern('a', true).matches('b')
    }

    @Test
    void testMatches_DefaultMatches_False() {
        assert new WildcardPattern('a', false).matches('a')

        assert !new WildcardPattern('a', false).matches('b')
        assert !new WildcardPattern(null, false).matches('')
        assert !new WildcardPattern(null, false).matches('abc')
        assert !new WildcardPattern('', false).matches('')
        assert !new WildcardPattern('', false).matches('abc')
    }

    @Test
    void testMatches() {
        assert new WildcardPattern('').matches('')
        assert new WildcardPattern('').matches('abc')
        assert new WildcardPattern('a').matches('a')
        assert new WildcardPattern('a@b.c').matches('a@b.c')
        assert new WildcardPattern('!@#$%^&.()-_+=~`{}[]:;+<>').matches('!@#$%^&.()-_+=~`{}[]:;+<>')
        assert new WildcardPattern('abc+def').matches('abc+def')
        assert new WildcardPattern('a*def').matches('abcdef')
        assert new WildcardPattern('a?c+de?').matches('abc+def')
        assert new WildcardPattern('*?cd*').matches('abcdef')
        assert new WildcardPattern('a/*/c*.groovy').matches('a/b/c.groovy')
        assert new WildcardPattern('a/*/c*.groovy').matches('a/b/cdef.groovy')
        assert new WildcardPattern('**/c*.groovy').matches('a/b/cdef.groovy')
        assert new WildcardPattern('a**/c*.groovy').matches('a/b/cdef.groovy')

        assert !new WildcardPattern('a').matches('b')
        assert !new WildcardPattern('a??cdef').matches('abcdef')
        assert !new WildcardPattern('a?cdef').matches('a/cdef')
        assert !new WildcardPattern('a*fg').matches('abcdef')
        assert !new WildcardPattern('a*c*.groovy').matches('a/b/c.groovy')
        assert !new WildcardPattern('a/*/c*.groovy').matches('a/b1/b2/cdef.groovy')
        assert !new WildcardPattern('**/c*.groovy').matches('a/b/c/def.groovy')
    }

    @Test
    void testMatches_TrimsPatternStrings() {
        assert new WildcardPattern(' abc ').matches('abc')
        assert new WildcardPattern('\ta ?c ').matches('a bc')
    }

    @Test
    void testMatches_CommaSeparatedListOfPatterns() {
        assert new WildcardPattern('a,b').matches('a')
        assert new WildcardPattern('x,a@b.c').matches('a@b.c')
        assert new WildcardPattern('a*de+f,xx').matches('abcde+f')
        assert new WildcardPattern('xx,a?cde?').matches('abcdef')
        assert new WildcardPattern('xx,yy,*?cd*').matches('abcdef')
        assert new WildcardPattern('a/*/c*.groovy,xx,yy').matches('a/b/c.groovy')
        assert new WildcardPattern('xx,**/c*.groovy').matches('a/b/cdef.groovy')
        assert new WildcardPattern('a**/c*.groovy,xx').matches('a/b/cdef.groovy')

        assert !new WildcardPattern('a,c').matches('b')
        assert !new WildcardPattern('x,a??cdef').matches('abcdef')
        assert !new WildcardPattern('a?cdef,xx,yy').matches('a/cdef')
        assert !new WildcardPattern('a*fg,xx').matches('abc+def')
        assert !new WildcardPattern('xx,yy,a*c*.groovy').matches('a/b/c.groovy')
        assert !new WildcardPattern('xx,a/*/c*.groovy').matches('a/b1/b2/cdef.groovy')
        assert !new WildcardPattern('**/c*.groovy,xx').matches('a/b/c/def.groovy')
    }

    @Test
    void testMatches_CommaSeparatedListOfPatterns_TrimsPatternStrings() {
        assert new WildcardPattern(' a , b ').matches('a')
        assert new WildcardPattern(' a , b\t,  c ').matches('b')
        assert new WildcardPattern(' a , b\t,  c?d ').matches('cdd')
        assert new WildcardPattern(' a , b*g,  c?d ').matches('bcdefg')
    }
}
