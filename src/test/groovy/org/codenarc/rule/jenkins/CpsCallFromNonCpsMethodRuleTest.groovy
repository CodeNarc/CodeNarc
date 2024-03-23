/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.junit.jupiter.api.Test

/**
 * Tests for CpsCallFromNonCpsMethodRule.
 *
 * Note: Usage of com.cloudbees.groovy.cps.NonCPS intentionally commented out for test purposes to mark where it would be used in a real use case.
 * This is necessary because we don't want to add this to the test dependencies.
 *
 * @author Daniel Zänker
 */
class CpsCallFromNonCpsMethodRuleTest extends AbstractJenkinsRuleTestCase<CpsCallFromNonCpsMethodRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CpsCallFromNonCpsMethod'
    }

    @Test
    void testNonCpsCallFromNonCps_NoViolations() {
        addNonCPSMethod('runSafe')
        addNonCPSMethod('safeMethod')
        addNonCPSMethod('staticSafeMethod')

        final SOURCE = '''
            //import com.cloudbees.groovy.cps.NonCPS
            
            class SomeClass {
                SomeClass() {}
                
                //@NonCPS
                void safeMethod() {}
                
                //@NonCPS
                static void staticSafeMethod() {}
            }
             
            class Main {
            
                //@NonCPS
                void runSafe(def script) {
                    List l = [1,2,3]
                    l.get(0)
                    SomeClass s = new SomeClass()         
                    s.safeMethod()
                    SomeClass.staticSafeMethod()
                    s.equals(s)
                    
                    script.echo('hello world')
                    script.properties([])
                    script.getContext(hudson.model.Node)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCpsAndNonCpsCallFromCps_NoViolations() {
        addNonCPSMethod('safeMethod')
        addNonCPSMethod('staticSafeMethod')

        final SOURCE = '''
            //import com.cloudbees.groovy.cps.NonCPS
            
            class SomeClass implements Serializable {
                SomeClass() {}
                
                //@NonCPS
                void safeMethod() {}
                
                //@NonCPS
                static void staticSafeMethod() {}
                
                void unsafeMethod() {}
            }
             
            class Main {
                
                void runUnsafe() {
                    SomeClass s = new SomeClass()         
                    s.safeMethod()
                    SomeClass.staticSafeMethod()
                    s.equals(s)
                    
                    s.unsafeMethod()
                    
                    script.sh('echo')
                } 
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCpsCallFromNonCps_Violations() {
        addNonCPSMethod('runSafe')

        final SOURCE = '''
            //import com.cloudbees.groovy.cps.NonCPS
            
            class SomeClass {
            
                SomeClass() {
                    unsafeMethod()
                    unsafeStaticMethod()
                }
                
                void unsafeMethod() {}
                
                static void unsafeStaticMethod() {}
            }
             
            class Main {
            
                //@NonCPS
                void runSafe() {
                    SomeClass s = new SomeClass()         
                    s.unsafeMethod()
                    SomeClass.unsafeStaticMethod()
                    def script = new Object()
                    script.sh("echo")
                }
            }
        '''
        assertViolations(SOURCE,
            [line: 7, source: 'unsafeMethod()', message: 'The method SomeClass.unsafeMethod is CPS transformed and may not be called from non-CPS transformed method SomeClass.<init>'],
            [line: 8, source: 'unsafeStaticMethod()', message: 'The method SomeClass.unsafeStaticMethod is CPS transformed and may not be called from non-CPS transformed method SomeClass.<init>'],
            [line: 21, source: 's.unsafeMethod()', message: 'The method SomeClass.unsafeMethod is CPS transformed and may not be called from non-CPS transformed method Main.runSafe'],
            [line: 22, source: 'SomeClass.unsafeStaticMethod()', message: 'The method SomeClass.unsafeStaticMethod is CPS transformed and may not be called from non-CPS transformed method Main.runSafe'],
            [line: 24, source: 'script.sh("echo")', message: 'The method sh is a CPS transformed pipeline step and may not be called from non-CPS transformed method Main.runSafe'])
    }

    @Test
    void testCpsCallFromNonCpsInParameters_Violations() {
        addNonCPSMethod('runSafe')
        final SOURCE = '''
            //import com.cloudbees.groovy.cps.NonCPS
            
            class Main {
                Object script = new Object()
                
                Main(int i = getValueStatic()) {
                
                }
            
                static int getValueStatic() {
                    return 42
                }
                
                int getValue() {
                    return 42
                }
            
                //@NonCPS
                void runSafe(int i = getValue(), 
                             int j = getValueStatic(), 
                             String s = script.sh('echo hello')) {
                    
                }
                
            }
        '''
        assertViolations(SOURCE,
            [line: 7, source: 'Main(int i = getValueStatic()) {', message: 'The method Main.getValueStatic is CPS transformed and may not be called from non-CPS transformed method Main.<init>'],
            [line: 20, source: 'void runSafe(int i = getValue(),', message: 'The method Main.getValue is CPS transformed and may not be called from non-CPS transformed method Main.runSafe'],
            [line: 21, source: 'int j = getValueStatic(),', message: 'The method Main.getValueStatic is CPS transformed and may not be called from non-CPS transformed method Main.runSafe'],
            [line: 22, source: 'String s = script.sh(\'echo hello\')) {', message: 'The method sh is a CPS transformed pipeline step and may not be called from non-CPS transformed method Main.runSafe'])
    }

    @Test
    void testNonCpsCallInParameters_NoViolations() {
        addNonCPSMethod('getValueStatic')
        addNonCPSMethod('getValue')
        addNonCPSMethod('runSafe')
        final SOURCE = '''
            //import com.cloudbees.groovy.cps.NonCPS
            
            class Main {
                Object script = new Object()
                
                Main(int i = getValueStatic()) {
                
                }
            
                //@NonCPS
                static int getValueStatic() {
                    return 42
                }
                
                //@NonCPS
                int getValue() {
                    return 42
                }
                
                int getValueUnsafe() {
                    return 42
                }
            
                //@NonCPS
                void runSafe(int i = getValue(),
                             int j = getValueStatic()) {
                    
                }
                
                void runUnsafe(int i = getValue(), 
                               int j = getValueUnsafe(), 
                               int k = getValueStatic(), 
                               String s = script.sh('echo hello')) {
                
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCpsCallFromInitializationCode_Violations() {
        final SOURCE = '''
            
            class SomeClass {
                public int value = getValueStatic()
                public int otherValue = Main.getValueStatic()
                
                static int getValueStatic() {
                    return 42
                }
                
            }
             
            class Main {
            
                static int getValueStatic() {
                    return 42
                }
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'public int value = getValueStatic()', message: 'The method SomeClass.getValueStatic is CPS transformed and may not be called from non-CPS transformed initialization code in SomeClass'],
            [line: 5, source: 'public int otherValue = Main.getValueStatic()', message: 'The method Main.getValueStatic is CPS transformed and may not be called from non-CPS transformed initialization code in SomeClass'])
    }

    @Test
    void testNonCpsCallFromInitializationCode_NoViolations() {
        addNonCPSMethod('getValueStatic')
        final SOURCE = '''
            //import com.cloudbees.groovy.cps.NonCPS
            
            class SomeClass {
                public int value = getValueStatic()
                public int otherValue = Main.getValueStatic()
                
                //@NonCPS
                static int getValueStatic() {
                    return 42
                }
                
            }
             
            class Main {
            
                //@NonCPS
                static int getValueStatic() {
                    return 42
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected CpsCallFromNonCpsMethodRule createRule() {
        new CpsCallFromNonCpsMethodRule()
    }
}
