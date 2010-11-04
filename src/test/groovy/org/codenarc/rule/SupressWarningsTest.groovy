package org.codenarc.rule

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ConstructorNode

/**
 * This class tests the @SuppressWarnings functionality.
 *
 * @author Hamlet D'Arcy
 */
class SupressWarningsTest extends AbstractRuleTestCase {

    static boolean failOnClass = false
    static boolean failOnConstructor = false
    static boolean failOnMethod = false
    static boolean failOnProperty = false
    static boolean failOnField = false

    def void setUp() {
        super.setUp()
        failOnClass = false
        failOnConstructor = false
        failOnMethod = false
        failOnProperty = false    
        failOnField = false
    }

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "ForceViolations"
    }

    def void testThatUnrelatedCodeHasNoViolations() {
        // make sure parent does not run
    }

    public void testSuppressOnClass() {
        failOnClass = true

        final SOURCE = '''
            @SuppressWarnings('ForceViolations') class MyClass1 {}
            @SuppressWarnings(["ForceViolations", "SomethingElse"]) class MyClass2 {}
            @SuppressWarnings class MyClass3 {}
            class MyClass4 {}
        '''

        assertTwoViolations SOURCE, 4, 'class MyClass3 {}', 5, 'class MyClass4 {}'
    }

    public void testSuppressOnConstructor() {
        failOnConstructor = true
        final SOURCE = '''
            class MyClass1 {
                @SuppressWarnings('ForceViolations')
                MyClass1() {}
            }

            class MyClass2 {
                @SuppressWarnings(["ForceViolations", "SomethingElse"])
                MyClass2() {}
            }

            class MyClass3 {
                @SuppressWarnings MyClass3() {}
            }
            class MyClass4 {
                MyClass4() {}
            }
        '''

        assertTwoViolations SOURCE,
                13, '@SuppressWarnings MyClass3() {}',
                16, 'MyClass4() {}'
    }

    public void testVisitProperty() {
        failOnProperty = true
        final SOURCE = '''
            class MyClass1 {
                @SuppressWarnings('ForceViolations')
                def someProperty
            }

            class MyClass2 {
                @SuppressWarnings(["ForceViolations", "SomethingElse"])
                def someProperty
            }

            class MyClass3 {
                @SuppressWarnings def someProperty
            }
            class MyClass4 {
                def someProperty
            }
        '''

        assertTwoViolations SOURCE,
                13, 'def someProperty',
                16, 'def someProperty'
    }

    public void testVisitField() {
        failOnField = true
        final SOURCE = '''
            class MyClass1 {
                @SuppressWarnings('ForceViolations')
                def someProperty
            }

            class MyClass2 {
                @SuppressWarnings(["ForceViolations", "SomethingElse"])
                def someProperty
            }

            class MyClass3 {
                @SuppressWarnings def someProperty
            }
            class MyClass4 {
                def someProperty
            }
        '''

        assertTwoViolations SOURCE,
                13, 'def someProperty',
                16, 'def someProperty'
    }

    public void testVisitMethod() {
        failOnMethod = true
        final SOURCE = '''
            class MyClass {
                @SuppressWarnings('ForceViolations')
                def method1() {}

                @SuppressWarnings(["ForceViolations", "SomethingElse"])
                def method2() {}

                @SuppressWarnings def method3() {}

                def method4() {}
            }
        '''

        assertTwoViolations SOURCE,
                9, 'def method3()',
                11, 'def method4()'
    }

    protected Rule createRule() {
        return new ForceViolationsRule()
    }
}

class ForceViolationsRule extends AbstractAstVisitorRule {
    String name = 'ForceViolations'
    int priority = 2
    Class astVisitorClass = ForceViolationsRuleAstVisitor
}

class ForceViolationsRuleAstVisitor extends AbstractAstVisitor {

    protected void visitClassEx(ClassNode node) {
        if (SupressWarningsTest.failOnClass && isFirstVisit(node)) {
            addViolation node, 'visitClassEx'
        }
    }

    def void visitPropertyEx(PropertyNode node) {
        if (SupressWarningsTest.failOnProperty && isFirstVisit(node)) {
            addViolation node, 'visitPropertyEx'
        }
    }

    def void visitFieldEx(FieldNode node) {
        if (SupressWarningsTest.failOnField && isFirstVisit(node)) {
            addViolation node, 'visitFieldEx'
        }
    }

    def void visitMethodEx(MethodNode node) {
        if (SupressWarningsTest.failOnMethod && isFirstVisit(node)) {
            addViolation node, 'visitMethodEx'
        }
    }

    def void visitConstructorEx(ConstructorNode node) {
        if (SupressWarningsTest.failOnConstructor && isFirstVisit(node)) {
            addViolation node, 'visitConstructorEx'
        }
    }

    protected void visitConstructorOrMethodEx(MethodNode node, boolean isConstructor) {
        if (isConstructor && SupressWarningsTest.failOnConstructor && isFirstVisit(node)) {
            addViolation node, 'visitConstructorOrMethodEx'
        }
        if (!isConstructor && SupressWarningsTest.failOnMethod && isFirstVisit(node)) {
            addViolation node, 'visitConstructorOrMethodEx'
        }
    }


}
