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

import org.apache.log4j.BasicConfigurator

/**
 * Java application (main() method) that invokes all of the Generate* scripts.
 *
 * @author Chris Mair
  */
class GenerateAll {

    /**
     * Invoke all generation scripts
     * @param args - command-line args (not used)
     */
    static void main(String[] args) {
        BasicConfigurator.configure()

        GenerateCodeNarcRulesProperties.main(null)
        GenerateRuleSetAllRules.main(null)
        GenerateRuleSetAllRulesByCategory.main(null)
        GenerateRuleIndexPage.main(null)
    }

}
