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
package org.codenarc.rule.grails

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.generic.StatelessClassRule

/**
 * Rule that checks for non-<code>final</code> fields on a Grails service class. Grails service
 * classes are, by default, singletons, and so they should be reentrant. In most cases, this implies
 * (or at least encourages) that they should be stateless.
 * <p/>
 * This rule ignores <code>final</code> fields (either instance or static). Fields that are
 * <code>static</code> and non-<code>final</code>, however, do cause a violation.
 * <p/>
 * This rule ignores non-static properties (i.e., no visibility modifier specified) declared with "def".
 * <p/>
 * This rule also ignores fields annotated with the <code>@Inject</code> annotation.
 * <p/>
 * You can configure this rule to ignore certain fields either by name or by type. This can be
 * useful to ignore fields that hold references to (static) dependencies (such as DAOs or
 * Service objects) or static configuration.
 * <p/>
 * The <code>ignoreFieldNames</code> property specifies one or more (comma-separated) field names
 * that should be ignored (i.e., that should not cause a rule violation). The name(s) may optionally
 * include wildcard characters ('*' or '?').  You can add to the field names to be ignored by setting
 * the (write-only) <code>addIgnoreFieldNames</code> property. This is a "special" property -- each
 * call to <code>setAddIgnoreFieldNames()</code> adds to the existing <code>ignoreFieldNames</code>
 * property value.
 * <p/>
 * The <code>ignoreFieldTypes</code> property specifies one or more (comma-separated) field type names
 * that should be ignored (i.e., that should not cause a rule violation). The type name(s) may optionally
 * include wildcard characters ('*' or '?').
 * <p/>
 * Note: The <code>ignoreFieldTypes</code> property matches the field type name as indicated
 * in the field declaration, only including a full package specification IF it is included in
 * the source code. For example, the field declaration <code>BigDecimal value</code> matches
 * an <code>ignoreFieldTypes</code> value of <code>BigDecimal</code>, but not
 * <code>java.lang.BigDecimal</code>.
 * <p/>
 * There is one exception for the <code>ignoreFieldTypes</code> property: if the field is declared
 * with a modifier/type of <code>def</code>, then the type resolves to <code>java.lang.Object</code>.
 * <p/>
 * The <code>ignoreFieldNames</code> property of this rule is preconfigured to ignore the standard
 * Grails service configuration field names ('scope', 'transactional') and injected bean names
 * ('dataSource', 'sessionFactory'), as well as all other field names ending with 'Service'.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match files
 * under the 'grails-app/services' folder. You can override this with a different regular
 * expression value if appropriate.
 * <p/>
 * This rule also sets the default value of <code>applyToClassNames</code> to only match class
 * names ending in 'Service'. You can override this with a different class name pattern
 * (String) if appropriate.
 *
 * @author Chris Mair
  */
class GrailsStatelessServiceRule extends StatelessClassRule {

    String name = 'GrailsStatelessService'
    int priority = 2
    String applyToFilesMatching = GrailsUtil.SERVICE_FILES
    String applyToClassNames = GrailsUtil.SERVICE_CLASSES

    GrailsStatelessServiceRule() {
        ignoreFieldNames = 'dataSource,scope,sessionFactory,transactional,*Service'
    }

    @Override
    protected boolean shouldIgnoreField(FieldNode fieldNode) {
        return super.shouldIgnoreField(fieldNode) ||
            fieldNode.isDynamicTyped() && !fieldNode.static && fieldNode.synthetic
    }

}
