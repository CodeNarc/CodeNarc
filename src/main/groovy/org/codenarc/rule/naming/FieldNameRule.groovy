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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that verifies that the name of each field matches a regular expression. By default it checks that
 * field names (other than <code>static final</code>) start with a lowercase letter and contains only letters
 * or numbers. By default, <code>static final</code> field names start with an uppercase letter and contain
 * only uppercase letters, numbers and underscores. Implicit method names are ignored (i.e., 'main' and 'run'
 * methods automatically created for Groovy scripts).
 * <p/>
 * The <code>regex</code> property specifies the default regular expression to validate a field name.
 * It is required and cannot be null or empty. It defaults to '[a-z][a-zA-Z0-9]*'.
 * <p/>
 * The <code>finalRegex</code> property specifies the regular expression to validate <code>final</code>
 * field names. It is optional and defaults to null, so that <code>final</code> fields that are
 * non-<code>static</code> will be validated using <code>regex</code>.
 * <p/>
 * The <code>staticRegex</code> property specifies the regular expression to validate <code>static</code>
 * field names. It is optional and defaults to null, so that <code>static</code> fields that are
 * non-<code>final</code> will be validated using <code>regex</code>.
 * <p/>
 * The <code>staticFinalRegex</code> property specifies the regular expression to validate <code>static final</code>
 * field names. It is optional but defaults to '[A-Z][A-Z0-9_]*'.
 * <p/>
 * The order of precedence for the regular expression properties is: <code>staticFinalRegex</code>,
 * <code>finalRegex</code>, <code>staticRegex</code> and finally <code>regex</code>. In other words, the first
 * regex in that list matching the modifiers for the field is the one that is applied for the field name validation.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class FieldNameRule extends AbstractAstVisitorRule {
    String name = 'FieldName'
    int priority = 2
    String regex = /[a-z][a-zA-Z0-9]*/
    String staticRegex
    String finalRegex
    String staticFinalRegex = DEFAULT_CONST_NAME
    Class astVisitorClass = FieldNameAstVisitor

    void validate() {
        assert regex
    }
}

class FieldNameAstVisitor extends AbstractAstVisitor  {
    void visitField(FieldNode fieldNode) {
        def re = rule.regex
        def mod = fieldNode.modifiers

        if (mod & FieldNode.ACC_STATIC) {
            re = rule.staticRegex ?: re
        }
        if (mod & FieldNode.ACC_FINAL) {
            re = rule.finalRegex ?: re
        }
        if ((mod & FieldNode.ACC_FINAL) && (mod & FieldNode.ACC_STATIC)) {
            re = rule.staticFinalRegex ?: re
        }

        if (!(fieldNode.name ==~ re)) {
            addViolation(fieldNode)
        }
        super.visitField(fieldNode)
    }

}