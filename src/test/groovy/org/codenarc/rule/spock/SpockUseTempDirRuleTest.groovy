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

/**
 * Tests for SpockUseTempDirRule
 *
 * @author Leonard Bruenings
 */
class SpockUseTempDirRuleTest extends AbstractRuleTestCase<SpockUseTempDirRule> {

    @Test
    void ruleProperties_AreValid() {
        assert rule.priority == 3
        assert rule.name == 'SpockUseTempDir'
        assert rule.checkAdHocCreation
        assert rule.specificationSuperclassNames == '*Specification'
        assert rule.specificationClassNames == null
    }

    //--------------------------------------------------------------------------
    // Shape 1 - @Rule / @ClassRule TemporaryFolder fields
    //--------------------------------------------------------------------------

    @Test
    void ruleTemporaryFolderField_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @Rule
                TemporaryFolder tempFolder = new TemporaryFolder()

                def "feature"() {
                    expect:
                    tempFolder != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 4, 'TemporaryFolder tempFolder = new TemporaryFolder()', ruleViolationMessage('Rule'))
    }

    @Test
    void classRuleSharedTemporaryFolderField_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @ClassRule
                @Shared
                TemporaryFolder shared = new TemporaryFolder()

                def "feature"() {
                    expect:
                    shared != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, 'TemporaryFolder shared = new TemporaryFolder()', ruleViolationMessage('ClassRule'))
    }

    @Test
    void ruleTemporaryFolderField_FullyQualifiedAnnotationAndType_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @org.junit.Rule
                org.junit.rules.TemporaryFolder tempFolder = new org.junit.rules.TemporaryFolder()

                def "feature"() {
                    expect:
                    tempFolder != null
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 4, source: 'org.junit.rules.TemporaryFolder tempFolder', message: ruleViolationMessage('Rule')])
    }

    @Test
    void ruleTemporaryFolderField_UntypedWithConstructorCall_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @Rule
                def tempFolder = new TemporaryFolder()

                def "feature"() {
                    expect:
                    tempFolder != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 4, 'def tempFolder = new TemporaryFolder()', ruleViolationMessage('Rule'))
    }

    @Test
    void ruleFieldOfAnotherType_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @Rule
                TestName testName = new TestName()

                @Rule
                MockWebServer server = new MockWebServer()

                def "feature"() {
                    expect:
                    testName != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void temporaryFolderFieldWithoutRuleAnnotation_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                TemporaryFolder tempFolder

                def "feature"() {
                    expect:
                    tempFolder == null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    //--------------------------------------------------------------------------
    // Shape 2 - ad-hoc temporary file / directory creation
    //--------------------------------------------------------------------------

    static Iterable<Arguments> adHocCreationCalls() {
        [
            Arguments.of('File.createTempDir()', 'File.createTempDir()'),
            Arguments.of("Files.createTempDirectory('x')", 'Files.createTempDirectory()'),
            Arguments.of("Files.createTempFile('a', 'b')", 'Files.createTempFile()'),
            Arguments.of("File.createTempFile('a', 'b')", 'File.createTempFile()'),
            Arguments.of("Files.createTempDirectory('x', attribute)", 'Files.createTempDirectory()'),
            Arguments.of('Files.createTempDirectory("x${index}", attribute)', 'Files.createTempDirectory()'),
            Arguments.of("Files.createTempFile('a', 'b', attribute)", 'Files.createTempFile()'),
            Arguments.of("Files.createTempFile('a', 'b', attribute, otherAttribute)", 'Files.createTempFile()'),
            // A value taken from a '@TempDir' does not make the default-directory overload safe
            Arguments.of("File.createTempDir(tempDir.name, 'x')", 'File.createTempDir()'),
        ]
    }

    @ParameterizedTest
    @MethodSource('adHocCreationCalls')
    void adHocCreationCall_SingleViolation(String expression, String describedCall) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def dir = ${expression}

                    expect:
                    dir != null
                }
            }
        """.stripIndent()
        assertSingleViolation(SOURCE, 5, "def dir = ${expression}", adHocViolationMessage(describedCall))
    }

    @Test
    void adHocCreationCall_FullyQualifiedFilesReceiver_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def dir = java.nio.file.Files.createTempDirectory('x')

                    expect:
                    dir != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, "def dir = java.nio.file.Files.createTempDirectory('x')",
            adHocViolationMessage('Files.createTempDirectory()'))
    }

    @Test
    void newFileUnderTmpDirProperty_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def dir = new File(System.getProperty('java.io.tmpdir'), 'x')

                    expect:
                    dir != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, "def dir = new File(System.getProperty('java.io.tmpdir'), 'x')", TMP_DIR_MESSAGE)
    }

    @Test
    void newFileUnderTmpDirPropertyConcatenated_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def dir = new File(System.getProperty('java.io.tmpdir') + '/x')

                    expect:
                    dir != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, "def dir = new File(System.getProperty('java.io.tmpdir') + '/x')", TMP_DIR_MESSAGE)
    }

    @Test
    void adHocCreationInFieldInitializer_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                File dir = File.createTempDir()

                def "feature"() {
                    expect:
                    dir != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 3, 'File dir = File.createTempDir()', adHocViolationMessage('File.createTempDir()'))
    }

    @Test
    void adHocCreationInSetupMethod_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                File dir

                def setup() {
                    dir = File.createTempDir()
                }

                def "feature"() {
                    expect:
                    dir != null
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 6, 'dir = File.createTempDir()', adHocViolationMessage('File.createTempDir()'))
    }

    //--------------------------------------------------------------------------
    // Do not flag
    //--------------------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = ['@TempDir Path dir', '@TempDir File dir', '@TempDir FileSystemFixture dir', '@TempDir @Shared Path dir',
        '@spock.lang.TempDir Path dir'])
    void tempDirField_NoViolations(String declaration) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                ${declaration}

                def "feature"() {
                    expect:
                    dir != null
                }
            }
        """.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void fileUnderTempDirField_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @TempDir
                File tempDir

                def "feature"() {
                    given:
                    def sub = new File(tempDir, 'sub')

                    expect:
                    sub != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void filesCreateFileUnderTempDirField_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @TempDir
                Path tempDir

                def "feature"() {
                    given:
                    def file = Files.createFile(tempDir.resolve('x'))

                    expect:
                    file != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void tempFactoryMethodsWithExplicitDirectory_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @TempDir
                Path tempDir

                @TempDir
                File tempDirFile

                def "feature"() {
                    given:
                    def a = Files.createTempFile(tempDir, 'a', 'b')
                    def b = Files.createTempDirectory(tempDir, 'x')
                    def c = File.createTempFile('a', 'b', tempDirFile)
                    def d = File.createTempFile('a', 'b', tempDir.toFile())

                    expect:
                    a != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    static Iterable<Arguments> tempDirParameters() {
        // Spock injects a Path, a File, or any type with a Path or File constructor, such as its own FileSystemFixture
        def parameters = [
            ['@TempDir Path dir', "Files.createTempFile(dir, 'a', 'b')", "new File(dir.toFile(), 'sub')"],
            ['@TempDir File dir', "File.createTempFile('a', 'b', dir)", "new File(dir, 'sub')"],
            ['@TempDir FileSystemFixture dir', "Files.createTempDirectory(dir.currentPath, 'x')", "dir.resolve('sub').toFile()"],
        ]
        def methods = ['def "feature"', 'def setup', 'def setupSpec']
        [methods, parameters].combinations().collect { String method, List<String> parameter ->
            Arguments.of(method, *parameter)
        }
    }

    @ParameterizedTest
    @MethodSource('tempDirParameters')
    void tempDirParameter_NoViolations(String method, String parameter, String createCall, String newFile) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                ${method}(${parameter}) {
                    def a = ${createCall}
                    def b = ${newFile}
                    assert a && b
                }
            }
        """.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void tempDirParameterOfDataDrivenFeature_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature #name"(String name, @TempDir Path dir) {
                    given:
                    def file = Files.createTempFile(dir, name, '.txt')

                    expect:
                    file != null

                    where:
                    name << ['a', 'b']
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void adHocCreationNextToTempDirParameter_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"(@TempDir Path dir) {
                    given:
                    def other = File.createTempDir()

                    expect:
                    dir != other.toPath()
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, 'def other = File.createTempDir()', adHocViolationMessage('File.createTempDir()'))
    }

    @Test
    void tmpDirPropertyReadForAssertion_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @TempDir
                File tempDir

                def "feature"() {
                    expect:
                    !tempDir.absolutePath.startsWith(System.getProperty('java.io.tmpdir') + '/nope')
                    System.getProperty('java.io.tmpdir') != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void fileSystemFixtureTempDirField_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @TempDir
                FileSystemFixture fsFixture

                def "feature"() {
                    given:
                    fsFixture.create {
                        dir('src') {
                            file('a.txt').text = 'a'
                        }
                    }
                    def file = Files.createTempFile(fsFixture.currentPath, 'a', 'b')
                    def sub = new File(fsFixture.resolve('src').toFile(), 'b.txt')

                    expect:
                    file != null
                    sub != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void varargsFactoryMethodsWithExplicitDirectory_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @TempDir
                Path tempDir

                def "feature"() {
                    given:
                    def attribute = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString('rwx------'))
                    def a = Files.createTempDirectory(tempDir, 'x', attribute)
                    def b = Files.createTempFile(tempDir, 'a', 'b', attribute)
                    def c = Files.createTempDirectory(tempDir.resolve('sub'), "x${1}")

                    expect:
                    a != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void varargsFactoryMethodsWithVariableFirstArgument_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def prefix = 'x'
                    def a = Files.createTempDirectory(prefix, attribute)
                    def b = Files.createTempFile(prefix, 'b', attribute)

                    expect:
                    a != null
                }
            }
        '''.stripIndent()
        // A variable could hold the prefix or the target directory, so the rule stays silent
        assertNoViolations(SOURCE)
    }

    @Test
    void tempFactoryMethodsWithLocalDirectoryVariable_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @TempDir
                Path tempDir

                def "feature"() {
                    given:
                    def dir = tempDir
                    def file = Files.createTempFile(dir, 'a', 'b')

                    expect:
                    file != null
                }

                private File newFileIn(File directory) {
                    File.createTempFile('a', 'b', directory)
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void tempDirDeclaredInBaseSpecInSameFile_NoViolations() {
        final SOURCE = '''
            class BaseSpec extends spock.lang.Specification {
                @TempDir
                Path tempDir
            }

            class MySpec extends BaseSpec {
                def "feature"() {
                    given:
                    def file = Files.createTempFile(tempDir, 'a', 'b')
                    def sub = new File(tempDir.toFile(), 'sub')

                    expect:
                    file != null
                }
            }
        '''.stripIndent()
        // Both classes are specifications, so MySpec is analysed even though it cannot see the @TempDir field
        rule.specificationClassNames = '*Spec'
        assertNoViolations(SOURCE)
    }

    @Test
    void helperClassInSameFileAsSpec_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    expect:
                    Workspace.create() != null
                }
            }

            class Workspace {
                static File create() {
                    File.createTempDir()
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void similarlyNamedHelperCalls_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    def a = fixtures.createTempDir()
                    def b = createTempDir()
                    def c = FileUtils.createTempDirectory('x')

                    expect:
                    a != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void identifiersNamedAfterMapBeanProperties_ViolationsStillReported() {
        final SOURCE = '''
            class ConfigLoaderSpec extends spock.lang.Specification {
                Properties properties = new Properties()

                def "reads the configuration from a directory"() {
                    given:
                    def dir = File.createTempDir()
                    def loader = new ConfigLoader(dir)
                    properties.load(new StringReader('a=b'))

                    when:
                    def config = loader.load(properties)

                    then:
                    loader.metaClass.respondsTo(loader, 'load')
                    config.a == 'b'
                }
            }
        '''.stripIndent()
        // 'properties' and 'metaClass' are receivers read out of the analysed source, and both name a
        // JavaBean property of Map itself - a subscript lookup would return that property instead of null
        assertSingleViolation(SOURCE, 7, 'def dir = File.createTempDir()', adHocViolationMessage('File.createTempDir()'))
    }

    @Test
    void metaClassCallOnFileReceiver_NoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "feature"() {
                    given:
                    File.metaClass {
                        createTempDir { -> new File('/nope') }
                    }
                    def properties = ['a': 'b']

                    expect:
                    properties.size() == 1
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void nonSpecification_NoViolations() {
        final SOURCE = '''
            class MyTestFixture {
                @Rule
                TemporaryFolder tempFolder = new TemporaryFolder()

                File newWorkspace() {
                    File.createTempDir()
                }

                File newTmp() {
                    new File(System.getProperty('java.io.tmpdir'), 'x')
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void specificationClassNames_SpecSuffix_Violations() {
        final SOURCE = '''
            class MySpec {
                @Rule
                TemporaryFolder tempFolder = new TemporaryFolder()

                def "feature"() {
                    given:
                    def dir = File.createTempDir()

                    expect:
                    dir != null
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)

        rule.specificationClassNames = '*Spec'
        assertViolations(SOURCE,
            [line: 4, source: 'TemporaryFolder tempFolder = new TemporaryFolder()', message: ruleViolationMessage('Rule')],
            [line: 8, source: 'def dir = File.createTempDir()', message: adHocViolationMessage('File.createTempDir()')])
    }

    @Test
    void checkAdHocCreationFalse_OnlyRuleFieldsReported() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                @Rule
                TemporaryFolder tempFolder = new TemporaryFolder()

                def "feature"() {
                    given:
                    def dir = File.createTempDir()
                    def other = new File(System.getProperty('java.io.tmpdir'), 'x')

                    expect:
                    dir != null
                }
            }
        '''.stripIndent()
        rule.checkAdHocCreation = false
        assertSingleViolation(SOURCE, 4, 'TemporaryFolder tempFolder = new TemporaryFolder()', ruleViolationMessage('Rule'))
    }

    @Override
    protected SpockUseTempDirRule createRule() {
        new SpockUseTempDirRule()
    }

    private static final String TMP_DIR_MESSAGE =
        "Creating a file under 'java.io.tmpdir' is never cleaned up - use a '@TempDir' field"

    private static String ruleViolationMessage(String annotationName) {
        "'@${annotationName} TemporaryFolder' is a JUnit 4 carry-over - use Spock's '@TempDir'"
    }

    private static String adHocViolationMessage(String describedCall) {
        "'${describedCall}' is never cleaned up - use a '@TempDir' field"
    }
}
