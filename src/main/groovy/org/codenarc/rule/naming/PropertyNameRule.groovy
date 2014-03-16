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

import org.codehaus.groovy.ast.PropertyNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

import java.lang.reflect.Modifier

/**
 * Rule that verifies that the name of each property matches a regular expression. By default it checks that
 * property names (other than <code>static final</code>) start with a lowercase letter and contains only letters
 * or numbers. By default, <code>static final</code> property names start with an uppercase letter and contain
 * only uppercase letters, numbers and underscores.
 * <p/>
 * NOTE: This rule checks only <i>properties</i> of a class, not regular <i>fields</i>. In Groovy,
 * <i>properties</i> are fields declared with no access modifier (public, protected, private).
 * For naming of regular <i>fields</i>, see <code>FieldNameRule</code>.
 * <p/>
 * The <code>regex</code> property specifies the default regular expression to validate a property name.
 * It is required and cannot be null or empty. It defaults to '[a-z][a-zA-Z0-9]*'.
 * <p/>
 * The <code>finalRegex</code> property specifies the regular expression to validate <code>final</code>
 * property names. It is optional and defaults to null, so that <code>final</code> properties that are
 * non-<code>static</code> will be validated using <code>regex</code>.
 * <p/>
 * The <code>staticRegex</code> property specifies the regular expression to validate <code>static</code>
 * property names. It is optional and defaults to null, so that <code>static</code> properties that are
 * non-<code>final</code> will be validated using <code>regex</code>.
 * <p/>
 * The <code>staticFinalRegex</code> property specifies the regular expression to validate <code>static final</code>
 * property names. It is optional but defaults to '[A-Z][A-Z0-9_]*'.
 * <p/>
 * The order of precedence for the regular expression properties is: <code>staticFinalRegex</code>,
 * <code>finalRegex</code>, <code>staticRegex</code> and finally <code>regex</code>. In other words, the first
 * regex in that list matching the modifiers for the property is the one that is applied for the property
 *  name validation.
 * <p/>
 * The <code>ignorePropertyNames</code> property optionally specifies one or more
 * (comma-separated) property names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class PropertyNameRule extends AbstractAstVisitorRule {
    String name = 'PropertyName'
    int priority = 2
    String regex = DEFAULT_FIELD_NAME
    String staticRegex
    String finalRegex
    String staticFinalRegex = DEFAULT_CONST_NAME
    String ignorePropertyNames
    Class astVisitorClass = PropertyNameAstVisitor

    void validate() {
        assert regex
    }
}

class PropertyNameAstVisitor extends AbstractAstVisitor  {

    void visitProperty(PropertyNode node) {
        if (!new WildcardPattern(rule.ignorePropertyNames, false).matches(node.name)) {
            def re = rule.regex
            def mod = node.modifiers

            if (Modifier.isStatic(mod)) {
                re = rule.staticRegex ?: re
            }
            if (Modifier.isFinal(mod)) {
                re = rule.finalRegex ?: re
            }
            if ((Modifier.isFinal(mod)) && (Modifier.isStatic(mod))) {
                re = rule.staticFinalRegex ?: re
            }

            if (!(node.name ==~ re)) {
                addViolation(node, "The property name $node.name in class ${node.field?.owner?.name} does not match the pattern $re")
            }
        }
        super.visitProperty(node)
    }

}
