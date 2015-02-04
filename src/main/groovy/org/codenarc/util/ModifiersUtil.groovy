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

/**
 * Provide static utility methods for parsing AST member modifiers, e.g. public/protected/private, static, final, etc.
 *
 * @author Chris Mair
 */
class ModifiersUtil {

    private static final MODIFIERS = [
        public:MethodNode.ACC_PUBLIC,
        protected:MethodNode.ACC_PROTECTED,
        private:MethodNode.ACC_PRIVATE,
        static:MethodNode.ACC_STATIC,
        final:MethodNode.ACC_FINAL,
        volatile:MethodNode.ACC_VOLATILE,
        transient:MethodNode.ACC_TRANSIENT
    ]

    static boolean matchesAnyModifiers(Integer actualModifiers, List<Integer> expectedModifiersList) {
        if (actualModifiers == null) {
            return true
        }
        return expectedModifiersList.any { expectedModifiers -> matchesModifiers(actualModifiers, expectedModifiers) }
    }

    /**
     * Return true only if the actualModifiers int value contains all of the bits (enabled) from the expectedModifiers
     * @param actualModifiers - the full actual modifiers; an int value of the OR-ed modifiers (values from Opcodes)
     * @param expectedModifiers - the modifiers to check against; an int value of the OR-ed modifiers (values from Opcodes)
     * @return true only if the actualModifiers contains all of the bits (enabled) from the expectedModifiers
     */
    static boolean matchesModifiers(Integer actualModifiers, Integer expectedModifiers) {
        if (actualModifiers && expectedModifiers) {
            return (actualModifiers & expectedModifiers) == expectedModifiers
        }
        return true
    }

    /**
     * Parse comma-separated list of modifier groups
     * @param modifiersString - comma-separated list of modifier groups;
     *      each group is a list of whitespace-delimited modifier names;
     *      e.g. "public, protected static, protected final"
     * @return a List<Integer> of the modifiers, one int value for each group (separated by commas)
     */
    static List<Integer> parseModifiersList(String modifiersString) {
        def groups = modifiersString?.tokenize(',')
        return groups.collect { group -> parseModifiers(group) }
    }

    /**
     * Parse a group of whitespace-delimited modifier names
     * @param modifiersString - a group of whitespace-delimited modifier names
     * @return the int value of the OR-ed modifiers (values from Opcodes)
     */
    static int parseModifiers(String modifiersString) {
        def tokens = modifiersString?.tokenize()
        def mod = 0
        tokens.each { token ->
            mod = mod | parseSingleModifier(token)
        }
        return mod
    }

    /**
     * Parse a single modifier name
     * @param name - a modifier name; e.g. "public", "private", "static", "final"
     * @return the int value for the modifier from Opcodes
     */
    private static int parseSingleModifier(String name) {
        def mod = MODIFIERS[name]
        assert mod != null, "The modifier $name is not supported"
        return mod
    }

    // Prevent instantiation; all members are static
    private ModifiersUtil() { }

}
