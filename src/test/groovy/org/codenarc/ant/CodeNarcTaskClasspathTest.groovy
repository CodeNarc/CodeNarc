/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.ant

import groovy.util.slurpersupport.NodeChildren
import org.codehaus.groovy.ant.Groovyc
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Tests exercising CodeNarcTask with regards to configuring the classpath used for compiling analysed sources
 *
 * @author Marcin Erdmann
 */
class CodeNarcTaskClasspathTest {

    private File ruleSetsFile
    private File analysedDir
    private File reportFile

    private TestCompiler compiler

    private AntBuilder ant

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Before
    void setupSources() {
        ruleSetsFile = temporaryFolder.newFile('rulesets.groovy')
        analysedDir = temporaryFolder.newFolder('analysed')
        reportFile = new File(temporaryFolder.root, 'report.xml')

        compiler = new TestCompiler(temporaryFolder.newFolder('sources'), temporaryFolder.newFolder('classes'))
        ant = createAntBuilderWithCodenarcTask()
    }

    @Test
    void testSpecifyingAnalysisCompilationClasspathInAntTask() {
        ruleSetsFile << '''
            ruleset {
                CloneWithoutCloneable
            }
        '''
        addAnalysedFile('CloneWithoutCloneableViolation.groovy') << '''
            class CloneWithoutCloneableViolation extends CloneWithoutCloneableViolationSuperclass {
                CloneWithoutCloneableViolation clone() {
                }
            }
        '''
        compiler.addSource('CloneWithoutCloneableViolationSuperclass.groovy') << '''
            class CloneWithoutCloneableViolationSuperclass {
            }
        '''

        ant.codenarc(ruleSetFiles: ruleSetsFile.toURI().toString()) {
            fileset(dir: analysedDir.absolutePath)
            report(type: 'xml') {
                option(name: 'outputFile', value: reportFile.absolutePath)
            }
            classpath {
                pathelement(path: compiler.compiledClassesDirectory)
            }
        }

        assert violationsForFile('CloneWithoutCloneableViolation.groovy').size() == 1
    }

    @Test
    void testSpecifyingAnalysisCompilationClasspathReferenceInAntTask() {
        ruleSetsFile << '''
            ruleset {
                CloneWithoutCloneable
            }
        '''
        addAnalysedFile('CloneWithoutCloneableViolation.groovy') << '''
            class CloneWithoutCloneableViolation extends CloneWithoutCloneableViolationSuperclass {
                CloneWithoutCloneableViolation clone() {
                }
            }
        '''
        compiler.addSource('CloneWithoutCloneableViolationSuperclass.groovy') << '''
            class CloneWithoutCloneableViolationSuperclass {
            }
        '''

        ant.path(id: 'analysisCompilationClasspath') {
            pathelement(path: compiler.compiledClassesDirectory)
        }
        ant.codenarc(ruleSetFiles: ruleSetsFile.toURI().toString(), classpathRef: 'analysisCompilationClasspath') {
            fileset(dir: analysedDir.absolutePath)
            report(type: 'xml') {
                option(name: 'outputFile', value: reportFile.absolutePath)
            }
        }

        assert violationsForFile('CloneWithoutCloneableViolation.groovy').size() == 1
    }

    private NodeChildren violationsForFile(String fileName) {
        def parsedReport = new XmlSlurper().parse(reportFile)
        parsedReport.Package.File.findAll { it.@name == fileName }.Violation
    }

    private File addAnalysedFile(String fileName) {
        new File(analysedDir, fileName)
    }

    private AntBuilder createAntBuilderWithCodenarcTask() {
        def ant = new AntBuilder()
        ant.taskdef(name: 'codenarc', classname: CodeNarcTask.name)
        ant
    }

    private static class TestCompiler {
        private final File sourcesDirectory
        private final File outputDirectory

        TestCompiler(File sourcesDirectory, File outputDirectory) {
            this.sourcesDirectory = sourcesDirectory
            this.outputDirectory = outputDirectory
        }

        File getCompiledClassesDirectory() {
            compile()
            outputDirectory
        }

        File addSource(String fileName) {
            new File(sourcesDirectory, fileName)
        }

        private void compile() {
            def ant = new AntBuilder()

            ant.taskdef(name: 'groovyc', classname: Groovyc.name)

            ant.groovyc(srcdir: sourcesDirectory.absolutePath, destdir: outputDirectory.absolutePath)
        }
    }

}
