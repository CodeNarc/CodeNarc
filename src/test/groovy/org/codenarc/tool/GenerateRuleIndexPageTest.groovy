/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.tool

import org.codenarc.test.AbstractTestCase

/**
 * Tests for GenerateRuleIndexPage
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class GenerateRuleIndexPageTest extends AbstractTestCase {

    void testMain_GeneratesSampleRuleSetFile() {
        def tempFile = File.createTempFile('GenerateRuleIndexPageTest', null)
        tempFile.deleteOnExit()
        GenerateRuleIndexPage.ruleIndexFile = tempFile.path
        GenerateRuleIndexPage.main(null)

        def outputFileText = tempFile.text
        log("contents=$outputFileText")

        assertContainsAll(outputFileText, [
            'Generic', 'IllegalRegex',
            'Unnecessary', 'UnnecessaryBigDecimalInstantiation'])
    }

}