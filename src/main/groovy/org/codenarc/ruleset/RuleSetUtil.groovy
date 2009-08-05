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
package org.codenarc.ruleset

import org.codenarc.rule.Rule

/**
 * A private utility class for the <code>RuleSet</code> classes. All methods are static.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @version $Revision: 27 $ - $Date: 2009-02-02 22:41:59 -0500 (Mon, 02 Feb 2009) $
 */
public class RuleSetUtil {

    protected static void assertClassImplementsRuleInterface(Class ruleClass) {
        assert ruleClass
        assert Rule.isAssignableFrom(ruleClass), "The rule class [${ruleClass.name}] does not implement the Rule interface"
    }

    private RuleSetUtil() { }
}