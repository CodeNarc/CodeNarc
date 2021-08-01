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
package org.codenarc.rule.serialization

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for SerializableClassMustDefineSerialVersionUIDRule
 *
 * @author Hamlet D'Arcy
  */
class SerializableClassMustDefineSerialVersionUIDRuleTest extends AbstractRuleTestCase<SerializableClassMustDefineSerialVersionUIDRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SerializableClassMustDefineSerialVersionUID'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass implements Serializable {
                private static final long serialVersionUID = -403250971215465050L
            }
            class MyNotSerializableClass {
            }

            return "some script"
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnums_NoViolations() {
        final SOURCE = '''
            enum DeferralRequestStatus {    // implements Serializable -- fails
                PENDING(0),
                ACCEPTED(1),
                DENIED(2)
                long value

                DeferralRequestStatus(long pValue) {
                    value = pValue
                }

                long getId() {
                    return value
                }

                String getName() {
                    return GapEnumHelper.getValue(GrailsNameUtils.getShortName(DeferralRequestStatus.class.name), value).name
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInlineEnum_NoViolations() {
        final SOURCE = '''
            class MyClass {
                enum Channels { one, two, three, four }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInlineEnum_ImplementsSerializable_NoViolations() {
        final SOURCE = '''
            enum Status implements Serializable {
                PENDING,
                ACCEPTED,
                DENIED
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInterface_NoViolations() {
        final SOURCE = '''
            interface X extends Serializable {
               // X's method definitions go here
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class MyClass implements Serializable {
                // missing serialVersionUID
            } '''
        assertSingleViolation(SOURCE, 2, 'class MyClass implements Serializable',
                'The class MyClass implements Serializable but does not define a serialVersionUID')
    }

    @Override
    protected SerializableClassMustDefineSerialVersionUIDRule createRule() {
        new SerializableClassMustDefineSerialVersionUIDRule()
    }
}
