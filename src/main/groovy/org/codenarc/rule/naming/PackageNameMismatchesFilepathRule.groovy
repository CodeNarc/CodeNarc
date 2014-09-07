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
 * A package source file's path should match the package declaration.
 * </p>
 * To find the package-relevant subpath in the file path the <em>groupId</em> needs to be configured.
 * It is expected to appear in every package declaration.
 *
 * @author Simon Tost
 */
class PackageNameMismatchesFilepathRule extends AbstractRule {

    String groupId

    String name = 'PackageNameMismatchesFilepath'
    int priority = 1

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        PackageNode packageNode = sourceCode.ast?.package
        if (!packageNode || !sourceCode.path) {
            return
        }
        if (!groupId) {
            violations << createViolation(sourceCode, packageNode,
                'GroupId not configured. Cannot locate package path root.')
            return
        }

        def dotSeparatedFolders = sourceCode.with { path - name }.replace(File.separator, '.')
        def packages = packageNode.name
        if (!dotSeparatedFolders.find(groupId) || !packages.find(groupId)) {
            violations << createViolation(sourceCode, packageNode,
                "Could not find groupId '$groupId' in package or file's path ($sourceCode.path)")
        } else {
            def subfolders = dotSeparatedFolders.split(groupPattern, 2)[1]
            def subpackages = packages.split(groupPattern, 2)[1]
            if (subfolders != subpackages) {
                violations << createViolation(sourceCode, packageNode, mismatchMessage(subfolders))
            }
        }
    }

    protected getGroupPattern() {
        Pattern.quote(groupId)
    }

    protected mismatchMessage(subfolders) {
        def dotSeparatedPath = groupId + subfolders
        def subpath = dotSeparatedPath.replace('.', File.separator)
        "The package source file's path ($subpath) should match the package declaration"
    }
}
