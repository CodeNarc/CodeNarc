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
import org.codehaus.groovy.ast.PropertyNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

import java.lang.reflect.Modifier

/**
 * Rule that verifies that the name of each field matches a regular expression. By default it checks that
 * non-<code>final</code> field names start with a lowercase letter and contains only letters or numbers.
 * By default, <code>final</code> field names start with an uppercase letter and contain only uppercase
 * letters, numbers and underscores. 
 * <p/>
 * NOTE: This rule checks only regular <i>fields</i> of a class, not <i>properties</i>. In Groovy,
 * <i>properties</i> are fields declared with no access modifier (public, protected, private). Thus,
 * this rule only checks fields that specify an access modifier. For naming of regular
 * <i>properties</i>, see <code>PropertyNameRule</code>.
 * <p/>
 * The <code>regex</code> property specifies the default regular expression to validate a field name.
 * It is required and cannot be null or empty. It defaults to '[a-z][a-zA-Z0-9]*'.
 * <p/>
 * The <code>finalRegex</code> property specifies the regular expression to validate <code>final</code>
 * field names. It is optional and defaults to null, so that <code>final</code> fields that are not
 * <code>static</code> will be validated using <code>regex</code>.
 * <p/>
 * The <code>staticRegex</code> property specifies the regular expression to validate <code>static</code>
 * field names. It is optional and defaults to null, so that <code>static</code> fields that are
 * non-<code>final</code> will be validated using <code>regex</code>.
 * <p/>
 * The <code>staticFinalRegex</code> property specifies the regular expression to validate <code>static final</code>
 * field names. It is optional, but defaults to '[A-Z][A-Z0-9_]*'.
 * <p/>
 * The <code>privateStaticFinalRegex</code> property specifies the regular expression to validate <code>private static final</code>
 * field names. It is optional, but defaults to '[A-Z][A-Z0-9_]*'.
 * <p/>
 * The order of precedence for the regular expression properties is: <code>privateStaticFinalRegex</code>, <code>staticFinalRegex</code>,
 * <code>finalRegex</code>, <code>staticRegex</code> and finally <code>regex</code>. In other words, the first
 * regex in that list matching the modifiers for the field is the one that is applied for the field name validation.
 * <p/>
 * The <code>ignoreFieldNames</code> property optionally specifies one or more
 * (comma-separated) field names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class FieldNameRule extends AbstractAstVisitorRule {
    String name = 'FieldName'
    int priority = 2
    String regex = DEFAULT_FIELD_NAME
    String staticRegex
    String finalRegex
    String staticFinalRegex = DEFAULT_CONST_NAME
    String privateStaticFinalRegex
    String ignoreFieldNames = 'serialVersionUID'
    Class astVisitorClass = FieldNameAstVisitor

    void validate() {
        assert regex
    }
}

class FieldNameAstVisitor extends AbstractAstVisitor  {

    private final Set propertyNames = []

    void visitField(FieldNode fieldNode) {
        if (!isProperty(fieldNode) && !isIgnoredPropertyName(fieldNode)) {
            def mod = fieldNode.modifiers
            def re = rule.regex

            if (Modifier.isStatic(mod)) {
                re = rule.staticRegex ?: re
            }
            if (Modifier.isFinal(mod)) {
                re = rule.finalRegex ?: re
            }
            if ((Modifier.isFinal(mod)) && (Modifier.isStatic(mod))) {
                re = rule.staticFinalRegex ?: re
            }

            if ((Modifier.isFinal(mod)) && Modifier.isStatic(mod) && Modifier.isPrivate(mod)) {
                re = rule.privateStaticFinalRegex ?: re
            }

            if (!(fieldNode.name ==~ re)) {
                addViolation(fieldNode, "The fieldname $fieldNode.name in class ${fieldNode.owner?.name} does not match ${re.toString()}")
            }
        }
        super.visitField(fieldNode)
    }

    void visitProperty(PropertyNode node) {
        propertyNames << node.name
        super.visitProperty(node)
    }

    private boolean isIgnoredPropertyName(FieldNode node) {
        new WildcardPattern(rule.ignoreFieldNames, false).matches(node.name)
    }

    private boolean isProperty(FieldNode node) {
        // This assumes that the property node is visited before the (regular) field node
        propertyNames.contains(node.name)
    }
}
