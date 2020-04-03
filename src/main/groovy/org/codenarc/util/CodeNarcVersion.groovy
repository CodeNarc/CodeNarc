/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.util

import org.codenarc.util.io.ClassPathResource

/**
 * Utility class to return the current version of CodeNarc
 *
 * @author Chris Mair
 */
class CodeNarcVersion {

    private static final String VERSION_FILE = 'codenarc-version.txt'

    @SuppressWarnings('PropertyName')
    static final String version = readCodeNarcVersion()

    private static String readCodeNarcVersion() {
        ClassPathResource.getInputStream(VERSION_FILE).text
    }

}
