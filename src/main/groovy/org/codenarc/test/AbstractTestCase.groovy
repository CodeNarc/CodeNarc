/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.test

import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import org.slf4j.LoggerFactory

/**
 * Abstract superclass for tests
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */

@SuppressWarnings(['AbstractClassWithoutAbstractMethod', 'ConfusingMethodName'])
abstract class AbstractTestCase {

    protected static final CODENARC_PROPERTIES_FILE_PROP = 'codenarc.properties.file'

    @SuppressWarnings('FieldName')
    protected final LOG = LoggerFactory.getLogger(getClass())

    @SuppressWarnings('PublicInstanceField')
    @Rule public TestName testName = new TestName()

    /**
     * Write out the specified log message, prefixing with the current class name.
     * @param message - the message to log; toString() is applied first
     */
    @SuppressWarnings('MethodParameterTypeRequired')
    protected void log(message) {
        LOG.info message.toString()
    }

    protected String getName() {
        return testName.getMethodName()
    }

    //------------------------------------------------------------------------------------
    // Test Setup and Tear Down
    //------------------------------------------------------------------------------------

    @Before
    void setUpAbstractTestCase() {
        log "----------[ ${getName()} ]----------"
    }

}
