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

import org.codehaus.groovy.ast.PackageNode
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.PathUtil

import java.util.regex.Pattern

/**
 * A package source file's path should match the package declaration.
 * </p>
 * To find the package-relevant sub-path in the file path the <em>groupId</em> needs to be configured.
 * It is expected to appear in every package declaration.
 *
 * @author Simon Tost
 */
class PackageNameMatchesFilePathRule extends AbstractRule {

    String groupId
    String name = 'PackageNameMatchesFilePath'
    int priority = 2

    @Override
    boolean isReady() {
        return groupId
    }

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        PackageNode packageNode = sourceCode.ast?.package
        if (!packageNode || !sourceCode.path) {
            return
        }

        def normalizedPath = PathUtil.normalizePath(sourceCode.path - sourceCode.name)
        def dotSeparatedFolders = normalizedPath.replace('/', '.')
        dotSeparatedFolders = removeTrailingPeriod(dotSeparatedFolders)
        def packageName = removeTrailingPeriod(packageNode.name)

        if (!dotSeparatedFolders.find(groupId) || !packageName.find(groupId)) {
            violations << createViolation(sourceCode, packageNode,
                "Could not find groupId '$groupId' in package ($packageName) or file's path ($sourceCode.path)")
        } else {
            def groupPattern = Pattern.quote(groupId)
            def subfolders = dotSeparatedFolders.split(groupPattern, 2)[1]
            def subpackages = packageName.split(groupPattern, 2)[1]
            if (subfolders != subpackages) {
                violations << createViolation(sourceCode, packageNode, mismatchMessage(subfolders))
            }
        }
    }

    private String removeTrailingPeriod(String str) {
        return (str.endsWith('.')) ? str[0..-2] : str
    }

    private mismatchMessage(String subfolders) {
        def dotSeparatedPath = groupId + subfolders
        def subpath = dotSeparatedPath.replace('.', File.separator)
        "The package source file's path ($subpath) should match the package declaration"
    }
}
