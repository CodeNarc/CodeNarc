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
package org.codenarc.rule.grails

import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for GrailsDomainGormMethodsRule
 *
 * @author Vladimir Orany
 */
class GrailsDomainGormMethodsRuleTest extends AbstractRuleTestCase<GrailsDomainGormMethodsRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'GrailsDomainGormMethods'
    }

    @Test
    void test_SimilarMethodNamesButNotEntity_NoViolations() {
        final String SOURCE = '''
            class NoEntity {
                NoEntity save() {

                }

                static List<NoEntity> list(){
                    [new NoEntity()]
                }

            }

            class SomeService {

                void someMethod() {
                    List<NoEntity> list = NoEntity.list()
                    list.first().save()
                }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    @SuppressWarnings('DuplicateStringLiteral')
    void test_GormMethodUsedOnEntity_Violations() {
        final String SOURCE = '''
            package org.grails.datastore.gorm

            trait GormEntity<D> {
                D save() {
                    pritnln "saved $this"
                }
                static <D> List<D> list() {
                    Collections.emptyList()
                }
            }

            class SomeEntity implements GormEntity<SomeEntity> {

                SomeEntity prepare() {
                    save()
                }

            }

            class SomeService {

                void someMethod() {
                    List<SomeEntity> entities = SomeEntity.list()
                    SomeEntity firstEntity = list.first()
                    firstEntity.save()
                }

            }
        '''
        assertViolations(SOURCE,
            [
                    line: 16,
                    source: 'save()',
                    message: 'Prefer GORM Data Services to GORM instance calls like \'save\'',
            ],
            [
                    line: 24,
                    source: 'List<SomeEntity> entities = SomeEntity.list()',
                    message: 'Prefer GORM Data Services to GORM static calls like \'list\'',
            ],
            [
                    line: 26,
                    source: 'firstEntity.save()',
                    message: 'Prefer GORM Data Services to GORM instance calls like \'save\'',
            ]
        )
    }

    @Test
    void test_CustomStaticMethodNames_Violations() {
        rule.gormStaticMethodsNames = 'foo,bar,foobar'

        final String SOURCE = '''
            package org.grails.datastore.gorm

            trait GormEntity<D> {
                static <D> List<D> foobar() {
                    Collections.emptyList()
                }
            }

            class SomeEntity implements GormEntity<SomeEntity> { }

            class SomeService {

                void someMethod() {
                    SomeEntity.foobar()
                }

            }
        '''
        assertViolations(SOURCE,
                [
                        line: 15,
                        source: 'SomeEntity.foobar()',
                        message: 'Prefer GORM Data Services to GORM static calls like \'foobar\'',
                ]
        )
    }

    @Test
    @SuppressWarnings([
            'DuplicateMapLiteral',
            'DuplicateNumberLiteral',
            'DuplicateStringLiteral',
    ])
    void test_CustomStaticMethodNamesList_Violations() {
        rule.gormStaticMethodsNamesList = ['foo', 'bar', 'foobar']

        final String SOURCE = '''
            package org.grails.datastore.gorm

            trait GormEntity<D> {
                static <D> List<D> foobar() {
                    Collections.emptyList()
                }
            }

            class SomeEntity implements GormEntity<SomeEntity> { }

            class SomeService {

                void someMethod() {
                    SomeEntity.foobar()
                }

            }
        '''
        assertViolations(SOURCE,
                [
                        line: 15,
                        source: 'SomeEntity.foobar()',
                        message: 'Prefer GORM Data Services to GORM static calls like \'foobar\'',
                ]
        )
    }

    @Override
    protected GrailsDomainGormMethodsRule createRule() {
        new GrailsDomainGormMethodsRule()
    }

}
