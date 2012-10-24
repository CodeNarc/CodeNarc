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
package org.codenarc.ant

import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for Report
 *
 * @author Chris Mair
 */
class ReportTest extends AbstractTestCase {
    private report = new Report()

    @Test
    void testAddConfiguredOption_AddsToOptions() {
        def option = new ReportOption(name:'a', value:'1')
        report.addConfiguredOption(option)
        assert report.options == [a:'1']

        def option2 = new ReportOption(name:'b', value:'2')
        report.addConfiguredOption(option2)
        assert report.options == [a:'1', b:'2']
    }
}
