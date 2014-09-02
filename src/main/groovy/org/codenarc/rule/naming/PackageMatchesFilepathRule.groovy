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

/**
 * A package source file's path should match the package itself.
 * </p>
 * The <code>prefixWhiteList</code> property ('src,main,groovy') can be used to specify subfolder names
 * that should be ignored at the beginning of file paths.
 *
 * @author Simon Tost
 */
class PackageMatchesFilepathRule extends AbstractRule {

    String prefixWhiteList = 'src,main,groovy'

    String name = 'PackageMatchesFilepath'
    int priority = 1

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        if (!sourceCode.path) return
        PackageNode packageNode = sourceCode.ast?.package
        if (!packageNode) return

        List<String> folders = sourceCode.path.tokenize(File.separator)[0..-2]
        List<String> packages = packageNode.name.tokenize('.')
        if (stripProjectFolders(folders) != packages) {
            violations << createViolation(sourceCode, packageNode,
                "A package source file\'s path ($sourceCode.path) should match the package itself")
        }
    }

    protected stripProjectFolders(folders) {
        while (folders.head() in whiteList) {
            folders = folders[1..-1]
        }
        folders
    }

    protected getWhiteList() {
        prefixWhiteList?.tokenize(',') ?: []
    }
}
