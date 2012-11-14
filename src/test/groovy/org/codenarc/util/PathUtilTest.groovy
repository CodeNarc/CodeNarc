/*
 * Copyright 2010 the original author or authors.
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
 * Tests for PathUtil
 *
 * @author Chris Mair
  */
class PathUtilTest extends AbstractTestCase {

    @Test
    void testGetName() {
        assert PathUtil.getName(null) == null
        assert PathUtil.getName('') == null
        assert PathUtil.getName('abc') == 'abc'
        assert PathUtil.getName('/abc') == 'abc'
        assert PathUtil.getName('/dir/abc') == 'abc'
        assert PathUtil.getName('\\abc') == 'abc'
    }

    @Test
    void testGetParentPath() {
        assert PathUtil.getParentPath(null) == null
        assert PathUtil.getParentPath('') == null
        assert PathUtil.getParentPath('abc') == null
        assert PathUtil.getParentPath('a/') == null
        assert PathUtil.getParentPath('abc\\def\\') == 'abc'
        assert PathUtil.getParentPath('a/b') == 'a'
        assert PathUtil.getParentPath('abc/def/ghi') == 'abc/def'
        assert PathUtil.getParentPath('a\\b\\c\\d\\e\\f') == 'a/b/c/d/e'
    }

    @Test
    void testRemovePathPrefix() {
        assert PathUtil.removePathPrefix(null, 'abc/def') == 'abc/def'
        assert PathUtil.removePathPrefix('xxx', 'abc/def') == 'abc/def'
        assert PathUtil.removePathPrefix('abc', 'abcdef') == 'def'
        assert PathUtil.removePathPrefix('abc', 'abc/def') == 'def'
        assert PathUtil.removePathPrefix('abc/', 'abc/def') == 'def'
    }

    @Test
    void testNormalizePath() {
        assert PathUtil.normalizePath(null) == null
        assert PathUtil.normalizePath('') == ''
        assert PathUtil.normalizePath('abc') == 'abc'
        assert PathUtil.normalizePath('abc/def') == 'abc/def'
        assert PathUtil.normalizePath('abc\\def\\ghi') == 'abc/def/ghi'
    }
}
