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
import org.junit.Test

import static org.codenarc.test.TestUtil.assertContainsAll

/**
 * Tests for GenerateRuleIndexPages
 *
 * @author Chris Mair
  */
class GenerateRuleIndexPagesTest extends AbstractTestCase {

    @Test
    void testMain_GeneratesRuleSetFiles() {
        def byCategoryTempFile = File.createTempFile('GenerateRuleIndexPageTestByCategory', null)
        def byNameTempFile = File.createTempFile('GenerateRuleIndexPageTestByName', null)
        byCategoryTempFile.deleteOnExit()
        byNameTempFile.deleteOnExit()

        GenerateRuleIndexPages.ruleIndexByCategoryFile = byCategoryTempFile.path
        GenerateRuleIndexPages.ruleIndexByNameFile = byNameTempFile.path

        GenerateRuleIndexPages.main(null)

        def byCategoryOutputFileText = byCategoryTempFile.text
        log("By Category: contents=\n$byCategoryOutputFileText")

        assertContainsAll(byCategoryOutputFileText, [
            'CyclomaticComplexity', 'Requires the GMetrics jar',
            'Generic', 'IllegalRegex',
            'Unnecessary', 'UnnecessaryBigDecimalInstantiation'])

        def byNameOutputFileText = byNameTempFile.text
        log("By Name: contents=\n$byNameOutputFileText")

        assertContainsAll(byNameOutputFileText, [
            'CyclomaticComplexity', 'Requires the GMetrics jar',
            'IllegalRegex',
            'Unnecessary', 'UnnecessaryBigDecimalInstantiation'])
    }

}
