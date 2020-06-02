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
    void test_SomeCondition_NoViolations() {
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
    void test_SomeCondition_Violations() {
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
                    lineNumber: 16,
                    sourceLineText: 'save()',
                    messageText: 'Prefer GORM Data Services to GORM instance calls like \'save\'',
            ],
            [
                    lineNumber: 24,
                    sourceLineText: 'List<SomeEntity> entities = SomeEntity.list()',
                    messageText: 'Prefer GORM Data Services to GORM static calls like \'list\'',
            ],
            [
                    lineNumber: 26,
                    sourceLineText: 'firstEntity.save()',
                    messageText: 'Prefer GORM Data Services to GORM instance calls like \'save\'',
            ]
        )
    }

    @Override
    protected GrailsDomainGormMethodsRule createRule() {
        new GrailsDomainGormMethodsRule()
    }

}
