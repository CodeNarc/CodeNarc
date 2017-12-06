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
            |    def myMethod1() { } 
            |    private String doStuff() {
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

    // TODO Test for method annotation -- method correct + annotation wrong and vice versa
    // TODO Test for class annotation  -- class correct + annotation wrong and vice versa

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

    @Override
    protected Rule createRule() {
        new IndentationRule()
    }
}
