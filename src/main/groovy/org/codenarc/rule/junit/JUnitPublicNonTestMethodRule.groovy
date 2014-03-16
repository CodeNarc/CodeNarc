/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * Rule that checks if a JUnit test class contains public methods other than:
 * <ul>
 *   <li>Zero-argument methods with names starting with "test"</li>
 *   <li>The setUp() and tearDown() methods</li>
 *   <li>Methods annotated with @Test</li>
 *   <li>Methods annotated with @Before and @After</li>
 *   <li>Methods annotated with @BeforeClass and @AfterClass</li>
 * </ul>
 * Public, non-test methods on a test class violate conventional usage of test classes,
 * and can be confusing.
 * <p/>
 * Public, non-test methods may also hide unintentional 'Lost Tests'. For instance, the test method
 * declaration may accidentally include methods parameters, and thus be ignored by JUnit. Or the
 * method may accidentally not follow the "test.." naming convention and not have the @Test annotation,
 * and thus be ignored by JUnit.
 * <p/>
 * This rule sets the default value of the <applyToClassNames> property to only match class names
 * ending in 'Test', 'Tests' or 'TestCase'.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class JUnitPublicNonTestMethodRule extends AbstractAstVisitorRule {
    String name = 'JUnitPublicNonTestMethod'
    int priority = 2
    Class astVisitorClass = JUnitPublicNonTestMethodAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitPublicNonTestMethodAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode methodNode) {
        if (Modifier.isPublic(methodNode.modifiers)
            && !(Modifier.isStatic(methodNode.modifiers))
            && !JUnitUtil.isTestMethod(methodNode)
            && !AstUtil.isMethodNode(methodNode, 'setUp|tearDown', 0)
            && !AstUtil.getAnnotation(methodNode, 'Override')
            && !AstUtil.getAnnotation(methodNode, 'Test')
            && !AstUtil.getAnnotation(methodNode, 'Before')
            && !AstUtil.getAnnotation(methodNode, 'After')
            && !AstUtil.getAnnotation(methodNode, 'BeforeClass')
            && !AstUtil.getAnnotation(methodNode, 'AfterClass') ) {

                addViolation(methodNode, "The method $methodNode.name is public but not a test method")
        }
    }
}
