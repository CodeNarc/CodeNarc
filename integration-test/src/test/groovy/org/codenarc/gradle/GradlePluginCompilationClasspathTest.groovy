package org.codenarc.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class GradlePluginCompilationClasspathTest {

    private final static String CONFIG_FILE_DIRECTORY = 'config/codenarc'
    private final static String CONFIG_FILE_PATH = "$CONFIG_FILE_DIRECTORY/rulesets.groovy"

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test
    void testRunningEnhancedRulesFromGradleBuildOnCodeThatUsesAstTransformations() {
        gradleBuildWithCodeNarcCompilationClasspathConfigured()
        cloneWithoutCloneableRuleEnabled()
        codeViolatingCloneWithoutCloneableRuleAndUsingAstTransformations()

        def result = runFailingCodeNarcTaskForMainSourceSet()

        assert result.output.contains('CodeNarc rule violations were found')
    }

    private BuildResult runFailingCodeNarcTaskForMainSourceSet() {
        GradleRunner.create()
            .withProjectDir(temporaryFolder.root)
            .withArguments('codenarcMain')
            .buildAndFail()
    }

    private void gradleBuildWithCodeNarcCompilationClasspathConfigured() {
        temporaryFolder.newFile('build.gradle') << """
            apply plugin: "codenarc"
            apply plugin: "groovy"

            repositories {
                mavenCentral()
            }

            codenarc {
                codenarc.configFile = file('$CONFIG_FILE_PATH')
            }

            dependencies {
                compile localGroovy()
                codenarc localGroovy()
                codenarc files(${codeNarcClassesDirs().collect { "'$it'" }.join(', ')})
            }

            codenarcMain {
                compilationClasspath = sourceSets.main.compileClasspath
            }
        """
    }

    private void cloneWithoutCloneableRuleEnabled() {
        new File(temporaryFolder.root, CONFIG_FILE_DIRECTORY).mkdirs()
        temporaryFolder.newFile(CONFIG_FILE_PATH) << '''
            ruleset {
                CloneWithoutCloneable
            }
        '''
    }

    private codeViolatingCloneWithoutCloneableRuleAndUsingAstTransformations() {
        def srcDir = "src/main/groovy/"
        new File(temporaryFolder.root, srcDir).mkdirs()
        temporaryFolder.newFile("$srcDir/ViolatingClass.groovy") << '''
            import groovy.transform.EqualsAndHashCode

            @EqualsAndHashCode
            class ViolatingClass extends Tuple {
                ViolatingClass clone() {}
            }
        '''
    }

    private List<String> codeNarcClassesDirs() {
        def gradleProjectPath = System.getProperty('codenarc.test.projectPath')
        if (gradleProjectPath) {
            ["$gradleProjectPath/build/classes/groovy/main", "$gradleProjectPath/build/resources/main"]
        } else {
            [new File('out/production/CodeNarc').absolutePath]
        }
    }

}
