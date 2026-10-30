/*
 * Copyright 2026 the original author or authors.
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
package org.codenarc.rule.spock

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

import java.util.stream.Stream

/**
 * Tests for SpockUseRestoreSystemPropertiesRule
 *
 * @author Leonard Bruenings
 */
class SpockUseRestoreSystemPropertiesRuleTest extends AbstractRuleTestCase<SpockUseRestoreSystemPropertiesRule> {

    @Test
    void ruleProperties_AreValid() {
        assert rule.priority == 2
        assert rule.name == 'SpockUseRestoreSystemProperties'
        assert rule.specificationSuperclassNames == '*Specification'
        assert rule.specificationClassNames == null
    }

    @ParameterizedTest
    @MethodSource('mutations')
    void mutation_WithoutAnnotation_SingleViolation(String mutation, String description) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    ${mutation}

                    expect:
                    true
                }
            }
        """.stripIndent()
        assertSingleViolation(SOURCE, 5, mutation, violationMessage(description))
    }

    @ParameterizedTest
    @MethodSource('mutations')
    void mutation_AnnotationOnFeatureMethod_NoViolations(String mutation) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                @spock.util.environment.RestoreSystemProperties
                def "feature"() {
                    given:
                    ${mutation}

                    expect:
                    true
                }
            }
        """.stripIndent()
        assertNoViolations(SOURCE)
    }

    @ParameterizedTest
    @MethodSource('mutations')
    void mutation_AnnotationOnClass_NoViolations(String mutation) {
        final SOURCE = """
            @RestoreSystemProperties
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    ${mutation}

                    expect:
                    true
                }
            }
        """.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void mutation_AnnotationOnSameFileSuperclass_NoViolations() {
        final SOURCE = '''
            @RestoreSystemProperties
            abstract class BaseSpec extends spock.lang.Specification {
            }

            class MySpec extends BaseSpec {
                def "feature"() {
                    given:
                    System.setProperty('foo', 'bar')

                    expect:
                    true
                }
            }
        '''.stripIndent()
        rule.specificationSuperclassNames = '*Specification,BaseSpec'
        assertNoViolations(SOURCE)
    }

    @Test
    void mutation_AnnotationOnCrossFileSuperclassIsNotResolvable_SingleViolation() {
        // Documents the accepted false positive: a base spec in another file cannot be resolved
        final SOURCE = '''
            class MySpec extends AbstractSystemPropertySpecification {
                def "feature"() {
                    given:
                    System.setProperty('foo', 'bar')

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, "System.setProperty('foo', 'bar')", violationMessage('System.setProperty'))
    }

    @ParameterizedTest
    @ValueSource(strings = ['setup', 'cleanup', 'setupSpec', 'cleanupSpec'])
    void mutation_FixtureMethod_SingleViolation(String fixtureMethod) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                def ${fixtureMethod}() {
                    System.setProperty('foo', 'bar')
                }

                def "feature"() {
                    expect:
                    true
                }
            }
        """.stripIndent()
        assertSingleViolation(SOURCE, 4, "System.setProperty('foo', 'bar')", violationMessage('System.setProperty'))
    }

    @Test
    void mutation_HelperMethod_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    useTempDir()

                    expect:
                    true
                }

                private void useTempDir() {
                    System.setProperty('java.io.tmpdir', '/tmp/x')
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 12, "System.setProperty('java.io.tmpdir', '/tmp/x')", violationMessage('System.setProperty'))
    }

    @Test
    void mutation_NestedInsideClosureOrControlStructure_Violations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    if (windows) {
                        System.setProperty('os', 'windows')
                    }
                    keys.each { System.clearProperty(it) }

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 6, source: "System.setProperty('os', 'windows')", message: violationMessage('System.setProperty')],
            [line: 8, source: 'keys.each { System.clearProperty(it) }', message: violationMessage('System.clearProperty')])
    }

    @Test
    void reads_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def home = System.getProperty('user.home')
                    def tmp = System.properties['java.io.tmpdir']
                    def all = System.properties
                    def copy = new Properties(System.getProperties())
                    def path = System.getenv('PATH')
                    def known = System.properties.containsKey('foo')

                    expect:
                    System.properties['user.home'] == home
                    System.getProperty('user.home') == home
                    System.properties.size() > 0
                    tmp && all && copy && path && known
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void nonMutatingCallsOnSystem_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    System.out.println('hello')
                    System.err.flush()
                    System.currentTimeMillis()
                    System.properties.each { k, v -> println k }

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void mutationOfSomethingElseNamedProperties_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def properties = new Properties()
                    properties.put('foo', 'bar')
                    properties['baz'] = 'qux'
                    properties.remove('foo')
                    this.properties.put('foo', 'bar')
                    config.properties.putAll(other)
                    config.properties['foo'] = 'bar'

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void mutationThroughLocalAlias_NoViolations() {
        // Intentional: without a classpath the rule cannot know what a local name holds, and absence
        // of evidence must produce silence
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def props = System.properties
                    props.put('foo', 'bar')
                    props['baz'] = 'qux'

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void mutationInNestedNonSpecClass_NoViolations() {
        // The Specification gate is applied per class, and a nested helper class is not a Specification
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    expect:
                    Helper.enable()
                }

                static class Helper {
                    static void enable() {
                        System.setProperty('foo', 'bar')
                    }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void mutationInFieldInitializer_NoViolations() {
        // No enclosing method, so the annotation cannot be checked - stay silent
        final SOURCE = '''
            @RestoreSystemProperties
            class MySpec extends spock.lang.Specification {
                def unused = System.setProperty('foo', 'bar')

                def "feature"() {
                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void nonSpecification_NoViolations() {
        final SOURCE = '''
            class MyThing {
                def doIt() {
                    System.setProperty('foo', 'bar')
                    System.properties['foo'] = 'bar'
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void specificationClassNames_SpecSuffix() {
        final SOURCE = '''
            class MySpec {
                def "feature"() {
                    given:
                    System.setProperty('foo', 'bar')

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)

        rule.specificationClassNames = '*Spec'
        assertSingleViolation(SOURCE, 5, "System.setProperty('foo', 'bar')", violationMessage('System.setProperty'))
    }

    @Override
    protected SpockUseRestoreSystemPropertiesRule createRule() {
        new SpockUseRestoreSystemPropertiesRule()
    }

    @SuppressWarnings('UnusedPrivateMethod')
    private static Stream<Arguments> mutations() {
        return Stream.of(
            Arguments.of("System.setProperty('foo', 'bar')", 'System.setProperty'),
            Arguments.of("java.lang.System.setProperty('foo', 'bar')", 'System.setProperty'),
            Arguments.of("System.clearProperty('foo')", 'System.clearProperty'),
            Arguments.of('System.setProperties(props)', 'System.setProperties'),
            Arguments.of("System.properties['foo'] = 'bar'", 'System.properties[...]'),
            Arguments.of("System.properties.foo = 'bar'", 'System.properties.foo'),
            Arguments.of('System.properties = props', 'System.properties'),
            Arguments.of("System.properties.put('foo', 'bar')", 'System.properties.put'),
            Arguments.of("System.properties.remove('foo')", 'System.properties.remove'),
            Arguments.of('System.properties.putAll(m)', 'System.properties.putAll'),
            Arguments.of('System.properties.clear()', 'System.properties.clear'),
            Arguments.of("System.getProperties().setProperty('foo', 'bar')", 'System.getProperties().setProperty'),
            Arguments.of("System.getProperties()['foo'] = 'bar'", 'System.getProperties()[...]'),
            Arguments.of("System.properties['foo'] += 'bar'", 'System.properties[...]')
        )
    }

    private static String violationMessage(String description) {
        "${description} mutates global state; annotate the feature or class with @RestoreSystemProperties"
    }
}
