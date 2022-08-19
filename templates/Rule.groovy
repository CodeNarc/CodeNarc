/*
 * Copyright 2021 the original author or authors.
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
package org.codenarc.rule.${ruleCategory}

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * ${ruleDescription}
 *
 * @author ${authorName}
 */
class ${ruleName}Rule extends AbstractAstVisitorRule {

    String name = '${ruleName}'
    int priority = 2
    Class astVisitorClass = ${ruleName}AstVisitor
}

class ${ruleName}AstVisitor extends AbstractAstVisitor {

}
