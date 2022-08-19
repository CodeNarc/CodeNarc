/*
 * Copyright 2017 the original author or authors.
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

import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for MultilineCommentChecker
 *
 * @author Russell Sanborn
 */
class MultilineCommentCheckerTest extends AbstractTestCase {

    @Test
    void testInitialStateFromConstructor() {
        MultilineCommentChecker multilineCommentChecker = new MultilineCommentChecker()
        assert !multilineCommentChecker.inMultilineComment
    }

    @Test
    void testProcessingLines() {
        MultilineCommentChecker multilineCommentChecker = new MultilineCommentChecker()

        multilineCommentChecker.processLine('not a multiline comment')
        assert !multilineCommentChecker.inMultilineComment

        multilineCommentChecker.processLine('/* start multiline comment')
        assert multilineCommentChecker.inMultilineComment

        multilineCommentChecker.processLine('still inside multiline comment')
        assert multilineCommentChecker.inMultilineComment

        multilineCommentChecker.processLine('close multiline comment */')
        assert !multilineCommentChecker.inMultilineComment

        multilineCommentChecker.processLine('still not a multiline comment')
        assert !multilineCommentChecker.inMultilineComment

        multilineCommentChecker.processLine('/* single line comment block is not a multiline comment */')
        assert !multilineCommentChecker.inMultilineComment
    }

}
