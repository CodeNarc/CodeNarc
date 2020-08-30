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
                [lineNumber:3, sourceLineText:'Optional<Collection> getCollection() { }', messageText:messageFor('getCollection')],
                [lineNumber:4, sourceLineText:'private Optional<List> getList() { }', messageText:messageFor('getList')],
                [lineNumber:5, sourceLineText:'Optional<Set> getSet() { }', messageText:messageFor('getSet')],
                [lineNumber:6, sourceLineText:'Optional<Map> getMap() { }', messageText:messageFor('getMap')])
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
                [lineNumber:3, sourceLineText:'Optional<Collection<Object>> getCollection() { }', messageText:messageFor('getCollection')],
                [lineNumber:4, sourceLineText:'private Optional<List<Integer>> getList() { }', messageText:messageFor('getList')],
                [lineNumber:5, sourceLineText:'Optional<ArrayList<String>> getArrayList() { }', messageText:messageFor('getArrayList')],
                [lineNumber:6, sourceLineText:'Optional<LinkedList<Double>> getLinkedList() { }', messageText:messageFor('getLinkedList')],
                [lineNumber:7, sourceLineText:'protected Optional<Set<BigDecimal>> getSet() { }', messageText:messageFor('getSet')],
                [lineNumber:8, sourceLineText:'Optional<HashSet<Boolean>> getHashSet() { }', messageText:messageFor('getHashSet')],
                [lineNumber:9, sourceLineText:'Optional<LinkedHashSet<Boolean>> getLinkedHashSet() { }', messageText:messageFor('getLinkedHashSet')],
                [lineNumber:10, sourceLineText:'Optional<TreeSet<Boolean>> getTreeSet() { }', messageText:messageFor('getTreeSet')],
                [lineNumber:11, sourceLineText:'Optional<SortedSet<Boolean>> getSortedSet() { }', messageText:messageFor('getSortedSet')],
                [lineNumber:12, sourceLineText:'Optional<EnumSet<MyEnum>> getEnumSet() { }', messageText:messageFor('getEnumSet')],
                [lineNumber:13, sourceLineText:'Optional<Map<Integer, String>> getMap() { }', messageText:messageFor('getMap')],
                [lineNumber:14, sourceLineText:'Optional<HashMap<String, String>> getHashMap() { }', messageText:messageFor('getHashMap')],
                [lineNumber:15, sourceLineText:'Optional<LinkedHashMap<String, String>> getLinkedHashMap() { }', messageText:messageFor('getLinkedHashMap')],
                [lineNumber:16, sourceLineText:'Optional<EnumMap<String, String>> getEnumMap() { }', messageText:messageFor('getEnumMap')],
                [lineNumber:17, sourceLineText:'Optional<SortedMap<String, String>> getSortedMap() { }', messageText:messageFor('getSortedMap')],
                [lineNumber:18, sourceLineText:'Optional<TreeMap<String, String>> getTreeMap() { }', messageText:messageFor('getTreeMap')],
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
