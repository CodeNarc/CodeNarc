/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SynchronizedReadObjectMethodRule
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedReadObjectMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SynchronizedReadObjectMethod'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
          // OK, class not Serializable
          class MyClass1 {
              private synchronized void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException { }
          }

          // OK, class not Serializable
          class MyClass2 {
              private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
                  synchronized(lock) { }
              }
          }

          // OK, Synchronized block is sufficiently advanced
          class MyClass3 {
              private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
                  synchronized(lock) { }
                  doSomething()
              }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSynchronizedMethod() {
        final SOURCE = '''
            class MyClass implements Serializable {

                private synchronized void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
                    // violation, no need to synchronized
                }
            }
      '''
        assertSingleViolation(SOURCE, 4, 'synchronized void readObject', 'The Serializable class MyClass has a synchronized readObject method. It is normally unnecesary to synchronize within deserializable')
    }

    @Test
    void testSynchronizedBlock() {
        final SOURCE = '''
            class MyClass implements Serializable {

                private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
                    synchronized(lock) {
                        // violation, no need to synchronized
                    }
                }
            }
      '''
        assertSingleViolation(SOURCE, 5, 'synchronized(lock)', 'The Serializable class MyClass has a synchronized readObject method. It is normally unnecesary to synchronize within deserializable')
    }

    protected Rule createRule() {
        new SynchronizedReadObjectMethodRule()
    }
}
