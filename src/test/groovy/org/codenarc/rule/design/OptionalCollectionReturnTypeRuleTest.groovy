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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for OptionalCollectionReturnTypeRule
 *
 * @author Chris Mair
 */
class OptionalCollectionReturnTypeRuleTest extends AbstractRuleTestCase<OptionalCollectionReturnTypeRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'OptionalCollectionReturnType'
    }

    @Test
    void test_RegularMethods_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def getCount() { }
                public String getName(int count, String prefix) { }
                protected void doStuff() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OtherOptionalReturnTypes_NoViolations() {
        final SOURCE = '''
            class MyClass {
                Optional<Integer> getCount() { }
                public Optional<String> getName(int count, String prefix) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OtherUsesOfOptional_NoViolations() {
        final SOURCE = '''
            class MyClass {
                protected Optional<Integer> count
                void setAlias(Optional<String> alias) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_OptionalCollectionReturnType_RawCollectionTypes_Violations() {
        final SOURCE = '''
            class MyClass {
                Optional<Collection> getCollection() { }
                private Optional<List> getList() { }
                Optional<Set> getSet() { }
                Optional<Map> getMap() { }
            }
        '''
        assertViolations(SOURCE,
                [line:3, source:'Optional<Collection> getCollection() { }', message:messageFor('getCollection')],
                [line:4, source:'private Optional<List> getList() { }', message:messageFor('getList')],
                [line:5, source:'Optional<Set> getSet() { }', message:messageFor('getSet')],
                [line:6, source:'Optional<Map> getMap() { }', message:messageFor('getMap')])
    }

    @Test
    void test_OptionalCollectionReturnType_Violations() {
        final SOURCE = '''
            class MyClass {
                Optional<Collection<Object>> getCollection() { }
                private Optional<List<Integer>> getList() { }
                Optional<ArrayList<String>> getArrayList() { }
                Optional<LinkedList<Double>> getLinkedList() { }
                protected Optional<Set<BigDecimal>> getSet() { }
                Optional<HashSet<Boolean>> getHashSet() { }
                Optional<LinkedHashSet<Boolean>> getLinkedHashSet() { }
                Optional<TreeSet<Boolean>> getTreeSet() { }
                Optional<SortedSet<Boolean>> getSortedSet() { }
                Optional<EnumSet<MyEnum>> getEnumSet() { }
                Optional<Map<Integer, String>> getMap() { }
                Optional<HashMap<String, String>> getHashMap() { }
                Optional<LinkedHashMap<String, String>> getLinkedHashMap() { }
                Optional<EnumMap<String, String>> getEnumMap() { }
                Optional<SortedMap<String, String>> getSortedMap() { }
                Optional<TreeMap<String, String>> getTreeMap() { }
            }
        '''
        assertViolations(SOURCE,
                [line:3, source:'Optional<Collection<Object>> getCollection() { }', message:messageFor('getCollection')],
                [line:4, source:'private Optional<List<Integer>> getList() { }', message:messageFor('getList')],
                [line:5, source:'Optional<ArrayList<String>> getArrayList() { }', message:messageFor('getArrayList')],
                [line:6, source:'Optional<LinkedList<Double>> getLinkedList() { }', message:messageFor('getLinkedList')],
                [line:7, source:'protected Optional<Set<BigDecimal>> getSet() { }', message:messageFor('getSet')],
                [line:8, source:'Optional<HashSet<Boolean>> getHashSet() { }', message:messageFor('getHashSet')],
                [line:9, source:'Optional<LinkedHashSet<Boolean>> getLinkedHashSet() { }', message:messageFor('getLinkedHashSet')],
                [line:10, source:'Optional<TreeSet<Boolean>> getTreeSet() { }', message:messageFor('getTreeSet')],
                [line:11, source:'Optional<SortedSet<Boolean>> getSortedSet() { }', message:messageFor('getSortedSet')],
                [line:12, source:'Optional<EnumSet<MyEnum>> getEnumSet() { }', message:messageFor('getEnumSet')],
                [line:13, source:'Optional<Map<Integer, String>> getMap() { }', message:messageFor('getMap')],
                [line:14, source:'Optional<HashMap<String, String>> getHashMap() { }', message:messageFor('getHashMap')],
                [line:15, source:'Optional<LinkedHashMap<String, String>> getLinkedHashMap() { }', message:messageFor('getLinkedHashMap')],
                [line:16, source:'Optional<EnumMap<String, String>> getEnumMap() { }', message:messageFor('getEnumMap')],
                [line:17, source:'Optional<SortedMap<String, String>> getSortedMap() { }', message:messageFor('getSortedMap')],
                [line:18, source:'Optional<TreeMap<String, String>> getTreeMap() { }', message:messageFor('getTreeMap')],
        )
    }

    private String messageFor(String methodName) {
        return "The method $methodName in class MyClass returns an Optional collection"
    }

    @Override
    protected OptionalCollectionReturnTypeRule createRule() {
        new OptionalCollectionReturnTypeRule()
    }
}
