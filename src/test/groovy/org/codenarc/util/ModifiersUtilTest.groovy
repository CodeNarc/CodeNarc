/*
 * Copyright 2012 the original author or authors.
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

import org.codehaus.groovy.ast.MethodNode

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for ModifiersUtil
 *
 * @author Chris Mair
 */
class ModifiersUtilTest extends AbstractTestCase {

    private static final ACC_PUBLIC = MethodNode.ACC_PUBLIC
    private static final ACC_PROTECTED = MethodNode.ACC_PROTECTED
    private static final ACC_PRIVATE = MethodNode.ACC_PRIVATE
    private static final ACC_STATIC = MethodNode.ACC_STATIC
    private static final ACC_FINAL = MethodNode.ACC_FINAL
    private static final ACC_VOLATILE = MethodNode.ACC_VOLATILE
    private static final ACC_TRANSIENT = MethodNode.ACC_TRANSIENT

    // Tests for matchesAnyModifiers

    @Test
    void testMatchesAnyModifiers() {
        assert ModifiersUtil.matchesAnyModifiers(null, [])
        assert ModifiersUtil.matchesAnyModifiers(null, [ACC_PUBLIC])

        // exact
        assert ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC, [ACC_PUBLIC])
        assert ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC, [ACC_PUBLIC, ACC_PRIVATE])
        assert ModifiersUtil.matchesAnyModifiers(ACC_PRIVATE, [ACC_PUBLIC, ACC_PRIVATE])
        assert ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC | ACC_FINAL, [ACC_PUBLIC | ACC_STATIC , ACC_PUBLIC | ACC_FINAL, ACC_STATIC])

        // partial
        assert ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC | ACC_FINAL, [ACC_PUBLIC, ACC_STATIC])
        assert ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC | ACC_FINAL, [ACC_PROTECTED, ACC_FINAL])

        assert !ModifiersUtil.matchesAnyModifiers(0, [])
        assert ModifiersUtil.matchesAnyModifiers(0, [ACC_PUBLIC])
        assert !ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC, [ACC_PRIVATE])
        assert !ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC | ACC_FINAL, [ACC_PUBLIC | ACC_STATIC, ACC_FINAL | ACC_PROTECTED])
        assert !ModifiersUtil.matchesAnyModifiers(ACC_PUBLIC | ACC_FINAL, [ACC_PUBLIC | ACC_FINAL | ACC_STATIC])
    }

    // Tests for matchesModifiers

    @Test
    void testMatchesModifiers() {
        assert ModifiersUtil.matchesModifiers(null, null)
        assert ModifiersUtil.matchesModifiers(0, null)
        assert ModifiersUtil.matchesModifiers(null, 0)
        assert ModifiersUtil.matchesModifiers(0, 0)

        assert ModifiersUtil.matchesModifiers(ACC_PUBLIC, 0)
        assert ModifiersUtil.matchesModifiers(ACC_PUBLIC, null)
        assert ModifiersUtil.matchesModifiers(ACC_PUBLIC, ACC_PUBLIC)
        assert ModifiersUtil.matchesModifiers(ACC_PUBLIC | ACC_FINAL, ACC_PUBLIC)
        assert ModifiersUtil.matchesModifiers(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, ACC_PRIVATE)
        assert ModifiersUtil.matchesModifiers(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, ACC_PRIVATE | ACC_STATIC)
        assert ModifiersUtil.matchesModifiers(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, ACC_PRIVATE | ACC_STATIC | ACC_FINAL)

        assert !ModifiersUtil.matchesModifiers(ACC_PUBLIC, ACC_PRIVATE)
        assert !ModifiersUtil.matchesModifiers(ACC_PUBLIC | ACC_FINAL, ACC_PROTECTED)
        assert !ModifiersUtil.matchesModifiers(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, ACC_PROTECTED)
        assert !ModifiersUtil.matchesModifiers(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, ACC_PRIVATE | ACC_VOLATILE)
    }

    // Tests for parseModifiersList(String)

    @Test
    void testParseModifiersList() {
        assert ModifiersUtil.parseModifiersList(null) == []
        assert ModifiersUtil.parseModifiersList('') == []
        assert ModifiersUtil.parseModifiersList('public') == [ACC_PUBLIC]
        assert ModifiersUtil.parseModifiersList('protected, private final') == [ACC_PROTECTED, ACC_PRIVATE | ACC_FINAL]
        assert ModifiersUtil.parseModifiersList('private static, protected, final') == [ACC_PRIVATE | ACC_STATIC, ACC_PROTECTED, ACC_FINAL]

        assert ModifiersUtil.parseModifiersList('protected static') == [ACC_PROTECTED | ACC_STATIC]
        assert ModifiersUtil.parseModifiersList('private static final, volatile public') == [ACC_PRIVATE | ACC_STATIC | ACC_FINAL, ACC_PUBLIC | ACC_VOLATILE]
    }

    @Test
    void testParseModifiersList_IgnoresExtraWhitespace() {
        assert ModifiersUtil.parseModifiersList('\tprotected  ') == [ACC_PROTECTED]
        assert ModifiersUtil.parseModifiersList(' private,  static  \t  final') == [ACC_PRIVATE, ACC_STATIC | ACC_FINAL]
    }

    @Test
    void testParseModifiersList_IllegalModifier() {
        shouldFailWithMessageContaining('xxx') { ModifiersUtil.parseModifiersList('xxx') }
        shouldFailWithMessageContaining('xxx') { ModifiersUtil.parseModifiersList('protected xxx') }
        shouldFailWithMessageContaining('xxx') { ModifiersUtil.parseModifiersList('protected, private xxx') }
    }

    // Tests for parseModifiers(String)

    @Test
    void testParseModifiers() {
        assert ModifiersUtil.parseModifiers(null) == 0
        assert ModifiersUtil.parseModifiers('') == 0
        assert ModifiersUtil.parseModifiers('public') == ACC_PUBLIC
        assert ModifiersUtil.parseModifiers('protected') == ACC_PROTECTED
        assert ModifiersUtil.parseModifiers('private') == ACC_PRIVATE
        assert ModifiersUtil.parseModifiers('static') == ACC_STATIC
        assert ModifiersUtil.parseModifiers('final') == ACC_FINAL
        assert ModifiersUtil.parseModifiers('volatile') == ACC_VOLATILE
        assert ModifiersUtil.parseModifiers('transient') == ACC_TRANSIENT

        assert ModifiersUtil.parseModifiers('protected static') == (ACC_PROTECTED | ACC_STATIC)
        assert ModifiersUtil.parseModifiers('private static final') == (ACC_PRIVATE | ACC_STATIC | ACC_FINAL)
        assert ModifiersUtil.parseModifiers('volatile public') == (ACC_PUBLIC | ACC_VOLATILE)
    }

        @Test
    void testParseModifiers_IgnoresExtraWhitespace() {
        assert ModifiersUtil.parseModifiers('\tprotected  ') == ACC_PROTECTED
        assert ModifiersUtil.parseModifiers(' private  static  \t  final') == (ACC_PRIVATE | ACC_STATIC | ACC_FINAL)
    }

    @Test
    void testParseModifiers_IllegalModifier() {
        shouldFailWithMessageContaining('xxx') { ModifiersUtil.parseModifiers('xxx') }
        shouldFailWithMessageContaining('xxx') { ModifiersUtil.parseModifiers('protected xxx') }
    }

}
