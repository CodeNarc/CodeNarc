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
package org.codenarc.source

import org.codenarc.test.AbstractTest

/**
 * Tests for SourceCodeUtil
 *
 * @author Chris Mair
 * @version $Revision: 7 $ - $Date: 2009-01-21 21:52:00 -0500 (Wed, 21 Jan 2009) $
 */
class SourceCodeUtilTest extends AbstractTest {
    static final NAME = 'MyTest.groovy'
    static final PATH = "src/$NAME"
    static final MATCH = /.*Test\.groovy/
    static final NO_MATCH = /.*Other\.groovy/
    private sourceCode

    void testShouldApplyTo_NullPathAndName() {
        assert SourceCodeUtil.shouldApplyTo(sourceCode, null, null)
        assert SourceCodeUtil.shouldApplyTo(sourceCode, null, MATCH)
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, MATCH, null)
    }

    void testShouldApplyTo() {
        sourceCode.path = PATH
        assert SourceCodeUtil.shouldApplyTo(sourceCode, null, null)
        assert SourceCodeUtil.shouldApplyTo(sourceCode, MATCH, null)
        assert SourceCodeUtil.shouldApplyTo(sourceCode, null, NO_MATCH)

        assert !SourceCodeUtil.shouldApplyTo(sourceCode, NO_MATCH, null)
        assert !SourceCodeUtil.shouldApplyTo(sourceCode, NO_MATCH, MATCH)
    }

    void setUp() {
        super.setUp()
        sourceCode = new SourceString("class ABC { }")
    }
}