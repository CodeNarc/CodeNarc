/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.source

import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.Phases
import org.codenarc.test.AbstractTestCase
import org.codenarc.test.TestUtil
import org.junit.Test

/**
 * Tests for CustomCompilerPhaseSourceDecorator
 */
class CustomCompilerPhaseSourceDecoratorTest extends AbstractTestCase {

    private static final PHASES_CAUSING_OUTPUT = [
        Phases.OUTPUT,
        Phases.FINALIZATION,
        Phases.ALL
    ]
    
    @Test
    void testEnsuresCompilerPhaseBeforeClassOutputToDisk() {
        def source = new SourceString('class MyClass {}')

        PHASES_CAUSING_OUTPUT.each { int phase ->
            TestUtil.shouldFail(AssertionError) {
                new CustomCompilerPhaseSourceDecorator(source, phase)
            }
        }
    }
    
    @Test
    void testOverridesReturnedAST() {
        def source = new SourceString('class SomeThrowable extends Throwable {}')
        def decoratedSource = new CustomCompilerPhaseSourceDecorator(source, Phases.SEMANTIC_ANALYSIS)
        assert decoratedSource.ast != source.ast
        assert interfacesOfSuperclassIn(source.ast).isEmpty()
        assert interfacesOfSuperclassIn(decoratedSource.ast) == [Serializable.name]
    }

    private List<String> interfacesOfSuperclassIn(ModuleNode ast) {
        assert ast.classes.size() == 1
        ast.classes.first().superClass.allInterfaces*.name    
    }
}
