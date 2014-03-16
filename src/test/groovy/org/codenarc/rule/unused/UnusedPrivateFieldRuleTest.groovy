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
package org.codenarc.rule.unused

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnusedPrivateFieldRule
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 *
  */
class UnusedPrivateFieldRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedPrivateField'
    }

    @Test
    void testGroovyCoreBug() {
        final SOURCE = '''
            class CallOnOwner {
                @AnnWithClassElement()
                private aField
            }'''
        assertSingleViolation(SOURCE, 4, 'private aField', 'The field aField is not used within the class CallOnOwner')
    }
    
    @Test
    void testApplyTo_SingleUnusedPrivateField() {
        final SOURCE = '''
          class MyClass {
              @SomeAnnotationToCorruptLineNumbers
              private int count
          }
        '''
        assertSingleViolation(SOURCE, 4, 'private int count', 'The field count is not used within the class MyClass')
    }

    @Test
    void testApplyTo_MultipleUnusedPrivateFields() {
        final SOURCE = '''
          class MyClass {
              private int count
              def otherField
              private static String globalName = 'xxx'
              MyClass() {
                  otherField = 'abc'
              }
          }
        '''
        assertTwoViolations(SOURCE, 3, 'private int count', 5, "private static String globalName = 'xxx'")
    }

    @Test
    void testApplyTo_UnusedPrivateFieldWithAssignment() {
        final SOURCE = '''
          class MyClass {
              private int count = 23
          }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count = 23')
    }

    @Test
    void testSuppressWarningsOnClass() {
        final SOURCE = '''
            @SuppressWarnings('UnusedPrivateField')
            class MyClass {
                private int count = 23
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuppressWarningsOnField() {
        final SOURCE = '''
            class MyClass {
                @SuppressWarnings('UnusedPrivateField')
                private int count = 23
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AllPrivateFieldsUsed() {
        final SOURCE = '''
            @MyAnnotation(elem = { 1 + 2 })
            class MyClass {
                private int count
                def otherField
                static String globalName = 'xxx'
                private defaultName = 'abc'
                private startName = defaultName
                def dateFormat = java.text.DateFormat.getDateTimeInstance()

                @MyAnnotation(elem = { 1 + 2 })
                def doStuff() {
                    this.count = 23
                    def newName = globalName + startName
                }

                def myClosure = { println "using $otherField" }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonPrivateFields() {
        final SOURCE = '''
            class MyClass {
                public int count
                protected String name
                def myOtherField        // property
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ReferencePropertyOfAnotherObject() {
        final SOURCE = '''
            class MyClass {
                private int count
                def doSomething() {
                    someOtherObject.count = 23
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count')
    }

    @Test
    void testApplyTo_StringPropertyReference() {
        final SOURCE = '''
            class MyClass {
                private int count
                def other = this."count"
                private serialVersionUID = 6700367864074699984L // should be ignored
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_GStringPropertyReference() {
        final SOURCE = '''
            class MyClass {
                private int count
                def other = this."${'count'}"
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count')
    }

    @Test
    void testApplyTo_DereferencedGStringPropertyReference() {
        final SOURCE = '''
            class MyClass {
                private int count
                def varName = "count"
                def other = this."${varName}"     // can't see this
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int count')
    }

    @Test
    void testApplyTo_OnlyReferenceIsAMethodDefaultValue() {
        final SOURCE = '''
            class MyClass {
                private static final COUNT = 3
                private defaultName = 'abc'
                
                private String doStuff(int repeat = COUNT) { }
                private String doOtherStuff(int number, name = defaultName) { }
                private otherMethod_NoParameters() { }
                private otherMethod_ParameterDefaultValueIsALiteral(value=123) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_OnlyReferenceIsAMapKeyOrValue() {
        final SOURCE = '''
            class MyClass {
                private static final NAME = 'abc'
                private static final VALUE = 123
                def doStuff() {
                    [(NAME):VALUE] 
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MoreThanOneClassInASourceFile() {
        final SOURCE = '''
            class MyClass {
                private int count
            }
            class OtherClass {
                int defaultCount = count
            }
        '''
        // TODO This "should" cause a violation for count
        assertNoViolations(SOURCE)
    }
                                              
    @Test
    void testApplyTo_ClosureField() {
        final SOURCE = '''
            class MyClass {
                private myClosure1 = { println '1' }
                private myClosure2 = { println '2' }
                private otherClosure = { println '3' }
                def getValue() {
                    def value = myClosure1()
                    def otherValue = someOtherObject.otherClosure()     // different object/method  
                    this.myClosure2(value, otherValue)
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, "private otherClosure = { println '3' }")
    }

    @Test
    void testApplyTo_StaticFieldReferencedThroughClassName() {
        final SOURCE = '''
            package com.example
            class MyClass {
                private static count = 0
                private static calc = { val -> val * 2 }
                void printReport(){
                    println MyClass.count
                    println MyClass.calc.call(99)
                }
            }
         '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoFields() {
        final SOURCE = '''
            class DataServiceException extends RuntimeException{
                DataServiceException(){
                }
                DataServiceException(String message){
                    super(message)
                }
                DataServiceException(String message, Throwable cause){
                    super(message, cause)
                }
                DataServiceException(Throwable cause){
                    super(cause)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Script() {
        final SOURCE = '''
            private BigDecimal depositAmount       // not considered a field
            private int count                      // not considered a field
        '''
        assertNoViolations(SOURCE)
    }
    
    @Test
    void testAnonymousInnerClassAsField() {
        final SOURCE = '''
            class MyClass {
                private static def x

                def y = new Class() {
                    def call() {
                        println(x)
                    }
                }
            } '''
        assertNoViolations SOURCE
    }

    @Test
    void testInnerClass() {
        final SOURCE = '''
            class MyClass {
                private static def x = 5

                class MyInnerClass {
                    def call() {
                        x++
                    }
                }
            } '''
        assertNoViolations SOURCE
    }

    @Test
    void testApplyTo_IgnoreFieldNames() {
        final SOURCE = '''
          class MyClass {
               private field1
               private int count
               private field2
          }
        '''
        rule.ignoreFieldNames = 'count, fiel*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testAnonymousInnerClassAsLocalVariable() {
        final SOURCE = '''
            class MyClass {
                private static def foo = {}

                def myMethod() {
                    return new Class() {
                        def call() {
                            foo()
                        }
                    }
                }
            } '''
        assertNoViolations SOURCE
    }

    @Test
    void testSuperPropertyReferenceFromInner() {
        final SOURCE = '''
            class ImportForm {

                private String importFilePath = null

                class ChooseFileHandler implements IFileChooseHandler {
                    void onSuccess(String[] paths, String[] names) {
                        ImportForm.this.importFilePath = paths[0]
                    }

                    void onFailure(int i, String s) {
                        println ImportForm.this.importFilePath
                    }
                }
            }
'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuperMethodReferenceFromInner() {
        final SOURCE = '''
            class ImportForm {

                private importFilePath = {}

                class ChooseFileHandler implements IFileChooseHandler {
                    void onFailure(int i, String s) {
                        ImportForm.this.importFilePath()
                    }
                }
            }
'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuperMethodReferenceFromInner_InMethodParmDefault() {
        final SOURCE = '''
            class ImportForm {

                private String importFilePath = 'xxx'

                class ChooseFileHandler implements IFileChooseHandler {
                    def myMethod(foo = ImportForm.this.importFilePath) {
                    }
                }
            }
'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testBugFix_CannotCastFieldNodeToMetaClass() {
        final SOURCE = '''
            class FlowBuilder extends AbstractFlowBuilder implements GroovyObject, ApplicationContextAware {
                private MetaClass metaClass
                FlowBuilder() {
                    println metaClass
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoFieldDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnusedPrivateFieldRule()
    }
}
