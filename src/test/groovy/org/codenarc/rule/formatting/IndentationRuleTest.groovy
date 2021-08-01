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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.util.GroovyVersion
import org.junit.Test

/**
 * Tests for IndentationRule
 *
 * @author Chris Mair
 */
class IndentationRuleTest extends AbstractRuleTestCase<IndentationRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'Indentation'
        assert rule.spacesPerIndentLevel == 4
    }

    @Test
    void test_ProperIndentation_NoViolations() {
        final SOURCE = '''
            |class MyClass {
            |    private static final NAME = "Joe"
            |    protected int count
            |    String id = "12345"
            |
            |    static void reset() { violationCounts = [1:0, 2:0] }
            |
            |    MyClass(String id) {
            |        this.id = id
            |    }
            |
            |    def myMethod1() { }
            |    private String doStuff() {
            |        def internalCounts = [1, 4, 2]
            |        id.trim()
            |        new Object().toString()
            |    }
            |    static void printReport(String filename) { }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    // Tests for class declarations

    @Test
    void test_Class_WrongIndentation_Violation() {
        final SOURCE = '''
            | class MyClass { }
            |   class MyClass2 { }
            |\tclass MyClass3 { }
        '''.stripMargin()
        assertViolations(SOURCE,
            [line:2, source:'class MyClass { }', message:'The class MyClass'],
            [line:3, source:'class MyClass2 { }', message:'The class MyClass2'],
            [line:4, source:'class MyClass3 { }', message:'The class MyClass3'],
        )
    }

    @Test
    void test_NestedClass_ProperIndentation_NoViolations() {
        final SOURCE = '''
            |class MyClass {
            |    private class MyNestedClass {
            |        private void innerMethod() { }
            |        void execute() {
            |            def runnable = new Runnable() {
            |                @Override
            |                void run() { }
            |            }
            |        }
            |    }
            |    protected void outerMethod() { }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_NestedClass_Issue504() {
        final SOURCE = '''
            |class Artifact implements Serializable {
            |  protected static final class ArtifactLocation {
            |    /* stuff */
            |  }
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:3, source:'protected static final class ArtifactLocation {',
                        message:'The class Artifact$ArtifactLocation is at the incorrect indent level: Expected column 5 but was 3'])
    }

    @Test
    void test_ClassDefinedWithinFieldDeclaration_ProperIndentation_NoViolations() {
        final SOURCE = '''
            |class MyClass {
            |    private Runnable runnable = new Runnable() {
            |        @Override
            |        void run() { }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    // Tests for Annotations

    @Test
    void test_Annotations_ProperIndentation_NoViolations() {
        final SOURCE = '''
            |@SuppressWarnings
            |class MyClass {
            |    @Component
            |    private class MyNestedClass {
            |        @Provider
            |        private void innerMethod() { }
            |        void execute() {
            |            def runnable = new Runnable() {
            |                @Override
            |                void run() { }
            |            }
            |        }
            |    }
            |    protected void outerMethod() { }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Annotations_WrongIndentation_KnownIssue() {
        final SOURCE = '''
            |@SuppressWarnings          // Annotation: correct
            | class MyClass { }         // Class: incorect --> IGNORED
            |
            |  @Component              // Annotation: incorrect --> VIOLATION
            |class MyOtherClass { }    // Class: correct
            |
            |@SuppressWarnings         // 1st Annotation: correct
            |  @Component              // 2nd Annotation: incorrect --> IGNORED
            | class TestClass {        // Class: incorrect --> IGNORED
            |
            |    @Provider                  // Annotation: correct
            | private void doStuff() { }    // Method: incorrect --> IGNORED
            |
            |    @Package void one() { }    // Method: correct
            |  @Package void two() { }      // Method: incorrect --> VIOLATION
            |
            |    @SuppressWarnings          // Field: correct
            |       private String id       // Field: incorrect --> IGNORED
            |
            |        @SuppressWarnings      // Field: incorrect --> VIOLATION
            |    private String name        // Field: correct
            |}
        '''.stripMargin()
        if (GroovyVersion.isGroovyVersion2()) {
            assertViolations(SOURCE,
                    [line: 6, source: 'class MyOtherClass', message: 'The class MyOtherClass'],
                    [line: 16, source: '@Package void two()', message: 'The method two in class TestClass'],
                    [line: 22, source: 'private String name', message: 'The field name in class TestClass'])
        } else {
            assertViolations(SOURCE,
                    [line: 5, source: '@Component', message: 'The class MyOtherClass'],
                    [line: 16, source: '@Package void two()', message: 'The method two in class TestClass'],
                    [line: 22, source: 'private String name', message: 'The field name in class TestClass'])
        }
    }

    // Tests for method declarations

    @Test
    void test_Method_WrongIndentation_Violation() {
        final SOURCE = '''
            |abstract class MyClass implements Runnable {
            |  def myMethod1() { }
            |         private String doStuff() {
            |         }
            |\tstatic void printReport(String filename) { }
            |protected static void count() { }
            |
            |  public abstract void doStuff()
            |
            |  @Override
            |  public abstract void run()
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:3, source:'def myMethod1()', message:'The method myMethod1 in class MyClass'],
                [line:4, source:'private String doStuff()', message:'The method doStuff in class MyClass'],
                [line:6, source:'static void printReport(String filename)', message:'The method printReport in class MyClass'],
                [line:7, source:'protected static void count()', message:'The method count in class MyClass'],
                [line:9, source:'public abstract void doStuff()', message:'The method doStuff in class MyClass'],
                [line:12, source:'public abstract void run()', message:'The method run in class MyClass'],
        )
    }

    @Test
    void test_Method_AbstractMethod_NoViolation() {
        final SOURCE = '''
            |abstract class AbstractRuleFactory implements RuleFactory {
            |    @Override
            |    protected abstract Rule createRule()
            |
            |    protected abstract Rule createErrorRule()
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Method_spacesPerIndentLevel_NoViolation() {
        final SOURCE = '''
            |class MyClass {
            |  def myMethod1() { }
            |  static void printReport(String filename) { }
            |}
        '''.stripMargin()
        rule.spacesPerIndentLevel = 2
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Method_spacesPerIndentLevel_Violation() {
        final SOURCE = '''
            |class MyClass {
            |   def myMethod1() { }
            | static void printReport(String filename) { }
            |}
        '''.stripMargin()
        rule.spacesPerIndentLevel = 2
        assertViolations(SOURCE,
            [line:3, source:'def myMethod1()', message:'The method myMethod1 in class MyClass'],
            [line:4, source:'static void printReport(String filename)', message:'The method printReport in class MyClass'],
        )
    }

    @Test
    void test_Constructor() {
        final SOURCE = '''
            |class MyAstVisitor extends OtherAstVisitor {
            |    MyAstVisitor(String name) {
            |        super(name)
            |    }
            |    MyAstVisitor(int count) { println 'int' }
            |    MyAstVisitor() {
            |        super()
            |    }
            |    MyAstVisitor() {
            |        this(0)
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Constructor_WrongIndentation_Violation() {
        final SOURCE = '''
            |class MyAstVisitor extends OtherAstVisitor {
            |  MyAstVisitor() {
            |                this(23)                      // ignored; known issue
            |             println 123                      // violation
            |  }
            |    MyAstVisitor(int count) {
            |           super(Long, [String, Long], 'L')   // ignored; known issue
            |    }
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:3, source:'MyAstVisitor() {', message:'The constructor in class MyAstVisitor'],
                [line:5, source:'println 123', message:'The statement on line 5 in class MyAstVisitor'],
        )
    }

    @Test
    void test_Constructor_CalledInOtherClass() {
        final SOURCE = '''
            |class MyClass { }
            |
            |class MyClassFactory {
            |    static MyClass createMyClass() {
            |        new MyClass()
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Constructor_CalledInScriptMethod() {
        final SOURCE = '''
            |class MyClass { }
            |
            |def myMethod() {
            |    new MyClass()
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    // Tests for field and property declarations

    @Test
    void test_Field_WrongIndentation_Violation() {
        final SOURCE = '''
            |class MyClass {
            |  private static final NAME = "Joe"
            |      protected int count
            | Date date
            |  def lastIndex
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:3, source:'private static final NAME = "Joe"', message:'The field NAME in class MyClass'],
                [line:4, source:'protected int count', message:'The field count in class MyClass'],
                [line:5, source:'Date date', message:'The field date in class MyClass'],
                [line:6, source:'def lastIndex', message:'The field lastIndex in class MyClass'],
        )
    }

    @Test
    void test_Field_MultipleFieldsDeclaredOnSameLine() {
        final SOURCE = '''
            |class MyClass {
            |    protected firstName, lastName
            |  def max, min
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:4, source:'def max, min', message:'The field max in class MyClass'],
        )
    }

    @Test
    void test_Field_DefinedWithinSingleLineClass() {
        final SOURCE = '''
            |static class Pojo { int count; String name }
            |
            |@SuppressWarnings
            |// nothing
            |static class Pojo2 { int count; String name }
            |
            |@SuppressWarnings
            |@Ignore
            |class Pojo3 { int count; String name }
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Field_Enum() {
        final SOURCE = '''
            |enum Planet {
            |    MERCURY(1), VENUS(2), EARTH(3),
            |    MARS(4)
            |
            |    final int index
            |
            |    private Planet(int index) {
            |        this.index = index
            |    }
            |}
            |class MyClass {
            |    enum WebServiceType { REST, SOAP }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Field_AssignmentToClosureWithStatements() {
        final SOURCE = '''
            |class MyClass {
            |    protected createRunner = { new ProcessRunner() }
            |    protected createOtherRunner = {
            |        if (excludeBaseline) {
            |            LOG.info("Loading baseline violations from [$excludeBaseline]")
            |            def resource = resourceFactory.getResource(excludeBaseline)
            |            def resultsProcessor = new BaselineResultsProcessor(resource)
            |            return new CodeNarcRunner(resultsProcessor:resultsProcessor)
            |        }
            |        return new CodeNarcRunner()
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    // Tests for method statements

    @Test
    void test_Statement_WrongIndentation_Violation() {
        final SOURCE = '''
            |class MyClass {
            |    private String doStuff() {
            |      def internalCounts = [1, 4, 2]
            |            id.trim()
            |           new Object()
            |    }
            |
            |    @Override
            |    protected int countOther() {
            |        // empty line
            |           return 99   // violation
            |    }
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:4, source:'def internalCounts = [1, 4, 2]', message:'The statement on line 4 in class MyClass'],
                [line:5, source:'id.trim()', message:'The statement on line 5 in class MyClass'],
                [line:6, source:'new Object()', message:'The statement on line 6 in class MyClass'],
                [line:12, source:'return 99', message:'The statement on line 12 in class MyClass'],
        )
    }

    @Test
    void test_Statement_SingleLineClosureAsMethodParameter_SameLineAsMethodCall() {
        final SOURCE = '''
            |class MyClass {
            |    void test_processResults() {
            |        shouldFailWithMessageContaining('results') { processor.processResults(null) }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_ClosureParameter_SameLineAsFirstStatement() {
        final SOURCE = '''
            |class MyClass {
            |    void doStuff() {
            |        boolean isFailureException = failureExceptionClasses.find {
            |            Class<Exception> failureExceptionClass -> failureExceptionClass.isAssignableFrom(e.class)
            |        }
            |
            |        process {
            |            int count,
            |            String name,
            |            String id -> println "$name($id)"
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_ClosureAsParameter_MultipleLines() {
        final SOURCE = '''
            |class MyClass {
            |    void doStuff(String name) {
            |        doWith {
            |            println  name
            |            processResults(name)
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_ClosureParameter_FlexibleIndentForClosureParameterBlocks() {
        final SOURCE = '''
            |class MyClass {
            |    void doStuff(String name) {
            |        String otherName = name
            |        doWith(
            |            name,
            |            { String someParam ->
            |                    println someParam  // note this extra level of indent; allowed
            |            }
            |        )
            |
            |         println 123                   // violation
            |        doWith(
            |              name,                    // wrong column, but not a statement -- ignored
            |            { String someParam ->
            |             println someParam         // violation
            |                  println 123          // violation
            |            println 999                // allowed
            |                println 999            // allowed
            |                    println 999        // allowed
            |                        println 123    // too far; violation
            |            }
            |        )
            |    }
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:12, source:'println 123', message:'The statement on line 12 in class MyClass'],
                [line:16, source:'println someParam', message:'The statement on line 16 in class MyClass'],
                [line:17, source:'println 123', message:'The statement on line 17 in class MyClass'],
                [line:21, source:'println 123', message:'The statement on line 21 in class MyClass'])
    }

    @Test
    void test_Statement_ReturningAMultiLineClosure() {
        final SOURCE = '''
            |class MyClass {
            |    def doStuff(String name) {
            |        return {
            |            def cssInputStream = ClassPathResource.getInputStream(getCssFile())
            |            assert cssInputStream, "CSS File [$getCssFile()] not found"
            |            def css = cssInputStream.text
            |            style(type: 'text/css') {
            |                unescaped << css
            |            }
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_StatementOnSameLineAsAnnotatedMethodDeclaration() {
        final SOURCE = '''
            |class MyClass {
            |    @Override
            |    protected boolean shouldLog() { return false }
            |
            |    @Override
            |    protected void logExtra() { }
            |
            |    void setUp() {
            |        dao = new AbstractDao() {
            |            @Override
            |            protected boolean shouldLog() { return true }
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_MultipleLineMethodCalls() {
        final SOURCE = '''
            |class MyClass {
            |    void doStuff(String name) {
            |        doWith('results') {
            |            println  name
            |            processResults(name)
            |        }
            |        doWith(
            |            name,
            |            null) {
            |            println  name
            |            processResults(name)
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_IfElseBlock_AndClosure() {
        final SOURCE = '''
            |class MyClass {
            |    private DirectoryResults processDirectory(String dir, RuleSet ruleSet) {
            |        def dirResults = new DirectoryResults(dir)
            |        def dirFile = new File((String) baseDirectory, (String) dir)
            |        dirFile.eachFile { file ->
            |            def dirPrefix = dir ? dir + SEP : dir
            |            def filePath = dirPrefix + file.name
            |            if (file.directory) {
            |                def subdirResults = processDirectory(filePath, ruleSet)
            |                // If any of the descendent directories have matching files, then include in final results
            |                if (subdirResults.getTotalNumberOfFiles(true)) {
            |                    dirResults.addChild(subdirResults)
            |                }
            |            }
            |            else {
            |                processFile(filePath, dirResults, ruleSet)
            |            }
            |        }
            |        dirResults
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_TryCatchFinally() {
        final SOURCE = '''
            |class MyClass {
            |    private void execute() {
            |        try {
            |            executeWithArgs(args)
            |        }
            |        catch(Throwable t) {
            |            println "ERROR: ${t.message}"
            |            t.printStackTrace()
            |        }
            |        finally {
            |            closeResources()
            |        }
            |    }
            |    private void executeOtherOne() {
            |        try {
            |            executeWithArgs(args)
            |        } catch(Throwable t) {
            |            t.printStackTrace()
            |        } finally {
            |            closeResources()
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_SwitchStatement() {
        final SOURCE = '''
            |class MyClass {
            |    private void execute() {
            |        switch(name) {
            |            case '1': println 'ok'; break
            |            case '2':
            |                println 'too much'
            |                break
            |        }
            |        switch(age) {
            |            case 21: println 'ok'; break
            |            case 11: println 'wrong'
            |                break
            |            default: println 'ok'
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_ForLoop() {
        final SOURCE = '''
            |class MyClass {
            |    void processResults() {
            |        for (Rule rule: validRules) {
            |            def name = rule.name
            |            allNames.addAll(name)
            |        }
            |
            |        for(int i=1; i < 99; i++) { println i }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_WhileLoop() {
        final SOURCE = '''
            |class MyClass {
            |    void processResults() {
            |        while(!terminate) {
            |            def name = rule.name
            |            allNames.addAll(name)
            |        }
            |
            |        while(!terminate) { println 'ok' }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_ClosureWithinMapLiteral() {
        final SOURCE = '''
            |class MyClass {
            |    def processResults() {
            |        return new MockRule(
            |            compilerPhase: compilerPhase,
            |            applyTo: { SourceCode source ->
            |                assert source.astCompilerPhase == compilerPhase
            |                []
            |            }
            |        )
            |    }
            |    def execute() {
            |        def resultsProcessor = [processResults:{ results ->
            |            assert results == RESULTS
            |            resultsProcessorCalled = true
            |        }] as ResultsProcessor
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_ClosureOnSingleLine() {
        final SOURCE = '''
            |class MyClass {
            |    def processResults() {
            |        return list.findAll { it instanceof Date }
            |    }
            |    def processOtherResults() {
            |        return list.
            |            findAll { it instanceof Date }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Statement_StaticAndInstanceInitializers() {
        final SOURCE = '''
            |class MyClass {
            |    static {
            |        println "Static initializer"
            |        ClosureExpression.metaClass.getText = { return CLOSURE_TEXT }
            |    }
            |
            |    static { println "init" }
            |
            |    static {
            |        [1, 2, 3].each { n ->
            |            println n
            |        }
            |    }
            |
            |    // Instance initializer
            |    {
            |        println "Instance initializer"
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    // Tests for @SuppressWarnings

    @Test
    void test_SuppressWarnings() {
        final SOURCE = '''
            |  @SuppressWarnings('Indentation')
            |   class MyClass {
            |               def processResults() {
            |                       return list.findAll { it instanceof Date }
            |       }
            |}
            |
            |class MyOtherClass {
            |       @SuppressWarnings('Indentation')
            |               def processResults() {
            |                       return list.findAll { it instanceof Date }
            |       }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_SpockTestWithLabels_NoViolation() {
        final SOURCE = '''
            |class MySpec extends Specification {
            |    void 'some feature'() {
            |        given: 'something'
            |        def a = new Object()
            |
            |        when: 'something is done'
            |        def b = a.toString()
            |
            |        then: 'something happens'
            |        b != ''
            |
            |        and:
            |        b != 'raccoon'
            |    }
            |
            |    void 'androidLint is run'() {
            |        given:
            |        writeAndroidBuildFile(androidVersion)
            |        useSimpleAndroidLintConfig()
            |        writeAndroidManifest()
            |        goodCode()
            |
            |        when:
            |        BuildResult result = gradleRunner()
            |            .withGradleVersion(version)
            |            .build()
            |
            |        then:
            |        if (GradleVersion.version(version) >= GradleVersion.version('2.5')) {
            |            // Executed task capture is only available in Gradle 2.5+
            |            result.task(taskName()).outcome == SUCCESS
            |            result.task(':resolveAndroidLint').outcome == SUCCESS
            |            result.task(':cleanupAndroidLint').outcome == SUCCESS
            |        }
            |
            |        // Make sure report exists and was using the expected tool version
            |        reportFile().exists()
            |
            |        where:
            |        version << ['2.3', '2.4', '2.7', '2.10', '2.14.1'] +
            |            (Jvm.current.java8Compatible ? ['3.0', '3.1'] : [])
            |        androidVersion = GradleVersion.version(version) < GradleVersion.version('3.0') ?
            |            DEFAULT_ANDROID_VERSION : '2.2.0\'
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ListExpressions() {
        final SOURCE = '''
            |[
            |    a {
            |        b
            |    }
            |]
            |
            |[
            |     1,
            |     2,
            |     3,
            |]
            |[1, 3, 5]
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Script() {
        final SOURCE = '''
            |println 1234
            |void doStuff(String name) {
            |    doWith {
            |        println  name
            |        processResults(name)
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Script2() {
        final SOURCE = '''
            |job('job') {
            |    label('label')
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_NestedClassWithinExpression() {
        final SOURCE = '''
            |class MyClass {
            |    private void nestedClassUnderCondition() {
            |        if (true) {
            |            println 1234
            |            def runnable = new Runnable() {
            |                @Override
            |                void run() {
            |                    println 6789
            |                }
            |            }
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_NestedClosure() {
        final SOURCE = '''
            |project.files(project.configurations.scaconfig.files.findAll { File it -> it.name.endsWith '.aar' }.collect { File it ->
            |    MessageDigest sha1 = MessageDigest.getInstance('SHA1')
            |    String inputFile = 'COMMAND=PREPARE_LIBRARY\\n' +
            |        "FILE_PATH=${it.absolutePath}\\n"
            |    String hash = new BigInteger(1, sha1.digest(inputFile.bytes)).toString(16)
            |    cacheDir + hash + File.separator + 'output/jars/classes.jar\'
            |}).asFileTree
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_GString_IfStatement() {
        final SOURCE = '''
            |"${if (true) 'content' else ''}"
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Trait_NoViolations() {
        final SOURCE = '''
            |package com.sample
            |
            |trait RegressionTest {
            |
            |    String callStackPath = "src/test/resources/callstacks/"
            |
            |   /**
            |     * Checks the current callstack is the same as the reference callstack.
            |     * The reference callstack can be updated into a txt file in the callStackPath
            |     *
            |     * Pattern: <RegressionTest.callStackPath>/<ClassTestSimpleName><_subname>.txt
            |     * @param subname optional subname, used in the reference callstack filename
            |     */
            |    void testNonRegression(String subname = '') {
            |        String targetFileName = "${callStackPath}${this.class.simpleName}"
            |        if (subname) {
            |            targetFileName += "_${subname}"
            |        }
            |        RegressionTestHelper.testNonRegression(helper, targetFileName)
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_InlineAnonymousClass() {
        final SOURCE = '''
            |class IndentationTest {
            |    private static final CacheLoader<String, List<String>> OK = new CacheLoader<String, List<String>>() {
            |        List<String> load(String key) {
            |            key.upperCase()
            |        }
            |    }
            |    private static final LoadingCache<String, List<String>> NOT_OK = CacheBuilder.newBuilder().build(
            |        new CacheLoader<String, List<String>>() {
            |            List<String> load(String key) {
            |                key.upperCase()
            |            }
            |        }
            |    )
            |
            |    void someMethod() {
            |        CacheBuilder2.newBuilder().build(
            |            new CacheLoader2<String, List<String>>() {
            |                List<String> load2(String key) {
            |                    key.upperCase()
            |                }
            |            }
            |        )
            |    }
            |
            |    AstVisitor getAstVisitor() {
            |        println 'start'
            |        new ExplicitTypeInstantiationAstVisitor('ArrayList') {
            |            @Override
            |            protected String createErrorMessage() {
            |                'ArrayList objects ..'
            |            }
            |        }
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_InlineAnonymousClass_Violations() {
        final SOURCE = '''
            |class IndentationTest {
            |    private static final CACHE2 = build(
            |       new CacheLoader<String>() { })     // Violation - wrong column
            |
            |    void someMethod() {
            |        CacheBuilder2.build(
            |    new CacheLoader2<String>() {           // Violation - valid indent offset, not indented enough
            |        void load2(String key) {
            |            key.upperCase()
            |        }
                 })
            |    }
            |
            |    void doStuff() {
            |         new BigDeal() {                   // Violation - wrong column
            |            String errorMessage() { }
            |        }
            |
            |      new Runner() { }.run()               // Violation - wrong column
            |
            |        CacheBuilder2.build(
            |         new CacheLoader2() { })           // Violation - wrong column
            |
            |        def processor2 =
            |          new Processor2() { }             // Violation - Wrong column
            |    }
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [line:4, source:'new CacheLoader<String>() { }', message:'The inner class IndentationTest$'],
                [line:8, source:'new CacheLoader2<String>() {', message:'The inner class IndentationTest$'],
                [line:16, source:'new BigDeal() {', message:'The statement on line 16'],
                [line:20, source:'new Runner() { }', message:'The statement on line 20'],
                [line:23, source:'new CacheLoader2() { }', message:'The inner class IndentationTest$'],
                [line:26, source:'new Processor2() { }', message:'The inner class IndentationTest$'],
        )
    }

    @Test
    void test_InlineAnonymousClass_KnownLimitations() {
        final SOURCE = '''
            |class IndentationTest {
            |    static final CACHE = build(
            |    new Cache<String>() { })          // Known Limitation: valid indent offset, but not indented enough; no violation
            |
            |    def list = [1, 2,
            |      new Processor() { }]             // Known Limitation: Skips List expressions; Wrong column, but no violation
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_GeneratedMethodCall_NoViolations() {
        final SOURCE = '''
            |def queueTests() {
            |  { stages ->
            |    queue(queueTestStage())(stages, 'tests', []) {
            |      'tmp/script.sh\'
            |    }
            |  }
            |}
        '''.stripMargin()
        rule.spacesPerIndentLevel = 2
        assertNoViolations(SOURCE)
    }

    @Test
    void test_firstNonWhitespaceColumn() {
        assert IndentationAstVisitor.firstNonWhitespaceColumn('') == -1
        assert IndentationAstVisitor.firstNonWhitespaceColumn('    ') == -1
        assert IndentationAstVisitor.firstNonWhitespaceColumn('abc') == 1
        assert IndentationAstVisitor.firstNonWhitespaceColumn(' a b c') == 2
        assert IndentationAstVisitor.firstNonWhitespaceColumn('     abc') == 6
    }

    @Test
    void test_isValidColumn() {
        def visitor = new IndentationAstVisitor()
        visitor.rule = rule
        [1, 5, 9].each { col -> assert visitor.isValidColumn(col) }

        [0, 2, 3, 6].each { col -> assert !visitor.isValidColumn(col) }
    }

    @Override
    protected IndentationRule createRule() {
        new IndentationRule()
    }
}
