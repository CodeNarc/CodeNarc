/*
 * Copyright 2014 the original author or authors.
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

import java.util.regex.Pattern
import org.codehaus.groovy.ast.PackageNode
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * A package source file's path should match the package itself.
 * </p>
 * To find the package-relevant subpath in the file path the <em>groupId</em> needs to be configured.
 * It is expected to appear in every package declaration.
 *
 * @author Simon Tost
 */
class PackageMatchesFilepathRule extends AbstractRule {

    String groupId

    String name = 'PackageMatchesFilepath'
    int priority = 1

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        PackageNode packageNode = sourceCode.ast?.package
        if (!packageNode || !groupId || !sourceCode.path) return
        def violation = false

        def dotSeparatedFolders = (sourceCode.path - sourceCode.name).replace(File.separator, '.')
        def packages = packageNode.name
        if (!(dotSeparatedFolders.find(groupId) && packages.find(groupId))) {
            violations << createViolation(sourceCode, packageNode,
                "Could not find groupId '$groupId' in package or file\'s path ($sourceCode.path)")
        } else {
            def subfolders = dotSeparatedFolders.split(groupPattern)[1..-1]
            def subpackages = packages.split(groupPattern)[1..-1]
            if (subfolders != subpackages) {
                violations << createViolation(sourceCode, packageNode,
                    "The package source file\'s path ($sourceCode.path) should match the package itself")
            }
        }

        if (violation) {
        }
    }

    protected getGroupPattern() {
        Pattern.quote(groupId)
    }
}
