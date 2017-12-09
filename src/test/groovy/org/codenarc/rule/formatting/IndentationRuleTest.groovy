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

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for IndentationRule
 *
 * @author Chris Mair
 */
class IndentationRuleTest extends AbstractRuleTestCase {

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
            |    def myMethod1() { } 
            |    private String doStuff() {
            |        def internalCounts = [1, 4, 2]
            |        id.trim() 
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
            [lineNumber:2, sourceLineText:'class MyClass { }', messageText:'The class MyClass'],
            [lineNumber:3, sourceLineText:'class MyClass2 { }', messageText:'The class MyClass2'],
            [lineNumber:4, sourceLineText:'class MyClass3 { }', messageText:'The class MyClass3'],
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
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [lineNumber:6, sourceLineText:'class MyOtherClass', messageText:'The class MyOtherClass'],
                [lineNumber:16, sourceLineText:'@Package void two()', messageText:'The method two'],
        )
    }

    // Tests for method declarations

    @Test
    void test_Method_WrongIndentation_Violation() {
        final SOURCE = '''
            |class MyClass {
            |  def myMethod1() { } 
            |         private String doStuff() {
            |         } 
            |\tstatic void printReport(String filename) { } 
            |protected static void count() { } 
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'def myMethod1()', messageText:'The method myMethod1 in class MyClass'],
                [lineNumber:4, sourceLineText:'private String doStuff()', messageText:'The method doStuff in class MyClass'],
                [lineNumber:6, sourceLineText:'static void printReport(String filename)', messageText:'The method printReport in class MyClass'],
                [lineNumber:7, sourceLineText:'protected static void count()', messageText:'The method count in class MyClass'],
        )
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
            [lineNumber:3, sourceLineText:'def myMethod1()', messageText:'The method myMethod1 in class MyClass'],
            [lineNumber:4, sourceLineText:'static void printReport(String filename)', messageText:'The method printReport in class MyClass'],
        )
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
                [lineNumber:3, sourceLineText:'private static final NAME = "Joe"', messageText:'The field NAME in class MyClass'],
                [lineNumber:4, sourceLineText:'protected int count', messageText:'The field count in class MyClass'],
                [lineNumber:5, sourceLineText:'Date date', messageText:'The field date in class MyClass'],
                [lineNumber:6, sourceLineText:'def lastIndex', messageText:'The field lastIndex in class MyClass'],
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
                [lineNumber:4, sourceLineText:'def max, min', messageText:'The field max in class MyClass'],
        )
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
            |    } 
            |}
        '''.stripMargin()
        assertViolations(SOURCE,
                [lineNumber:4, sourceLineText:'def internalCounts = [1, 4, 2]', messageText:'The statement on line 4 in class MyClass'],
                [lineNumber:5, sourceLineText:'id.trim()', messageText:'The statement on line 5 in class MyClass'],
        )
    }

    @Test
    void test_Statement_Closure_SingleLine() {
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

    @Override
    protected Rule createRule() {
        new IndentationRule()
    }
}
