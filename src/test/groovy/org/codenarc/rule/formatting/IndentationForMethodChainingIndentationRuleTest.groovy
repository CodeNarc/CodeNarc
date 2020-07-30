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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for method chaining support in IndentationRule.
 *
 * @author Chris Mair
 */
class IndentationForMethodChainingIndentationRuleTest extends AbstractRuleTestCase<IndentationRule> {
    @Override
    protected IndentationRule createRule() {
        new IndentationRule()
    }

    @Test
    void test_MethodChaining() {
        final SOURCE = '''
            |buildFileList()
            |    .collect { File it ->
            |        MessageDigest sha1 = MessageDigest.getInstance('SHA1')
            |        String inputFile = 'COMMAND=PREPARE_LIBRARY\\n' +
            |            "FILE_PATH=${it.absolutePath}\\n"
            |        cacheDir + File.separator + inputFile + sha1
            |    }
            |    .each { name ->
            |        println name
            |    }
            |println "done"
            |
            |list2.collect { item ->
            |    item.name
            |}.each { name -> println name }
            |
            |otherList.collect { item -> item.name }.each { name -> println name }
            |
            |if (expr instanceof ConstructorCallExpression || expr instanceof CastExpression) {
            |    [Map, Iterable, List, Collection, ArrayList, Set, HashSet].findAll {
            |        AstUtil.classNodeImplementsType(expr.type, it)
            |    }.each {
            |        callbackFunction()
            |    }
            |}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MethodChaining_MultilineClosureParameter_Style1_NoViolation() {
        def source = '''
            |buildFileList().collect { item ->
            |            item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |            println name
            |        }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList().collect { item ->
            |            item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList().collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |            println name
            |        }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList().collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertNoViolations(source)
    }

    @Test
    void test_MethodChaining_MultilineClosureParameter_Style1_Violation() {
        def source = '''
            |buildFileList().collect { item ->
            |              item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |          println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |println name
            |        }
        '''.stripMargin()
        assertViolations(source,
            [
              lineNumber:3,
              sourceLineText:'item.name',
              messageText:'The statement on line 3 in class None is at the incorrect indent level: Depending on your chaining style, expected one of [5, 9, 13] or one of [20, 24, 28] columns, but was 15'
            ],
            [lineNumber:7, sourceLineText:'println someName', messageText:'The statement on line 7 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 11'],
            [lineNumber:11, sourceLineText:'println name', messageText:'The statement on line 11 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 1'],
        )

        source = '''
            |buildFileList().collect { item ->
            |              item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |          println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertViolations(source,
            [
              lineNumber:3,
              sourceLineText:'item.name',
              messageText:'The statement on line 3 in class None is at the incorrect indent level: Depending on your chaining style, expected one of [5, 9, 13] or one of [20, 24, 28] columns, but was 15'
            ],
            [lineNumber:7, sourceLineText:'println someName', messageText:'The statement on line 7 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 11'],
        )

        source = '''
            |buildFileList().collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |              println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |          println name
            |        }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:5, sourceLineText:'println someName', messageText:'The statement on line 5 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 15'],
            [lineNumber:9, sourceLineText:'println name', messageText:'The statement on line 9 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 11'],
        )

        source = '''
            |buildFileList().collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |              println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:5, sourceLineText:'println someName', messageText:'The statement on line 5 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 15'],
        )
    }

    @Test
    void test_MethodChaining_MultilineClosureParameter_Style2_NoViolation() {
        def source = '''
            |buildFileList()
            |        .collect { item ->
            |            item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |            println name
            |        }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList()
            |        .collect { item ->
            |            item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList()
            |        .collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |            println name
            |        }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList()
            |        .collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |            println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertNoViolations(source)
    }

    @Test
    void test_MethodChaining_MultilineClosureParameter_Style2_Violation() {
        def source = '''
            |buildFileList()
            |        .collect { item ->
            |              item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |          println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |println name
            |        }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:4, sourceLineText:'item.name', messageText:'The statement on line 4 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 15'],
            [lineNumber:8, sourceLineText:'println someName', messageText:'The statement on line 8 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 11'],
            [lineNumber:12, sourceLineText:'println name', messageText:'The statement on line 12 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 1'],
        )

        source = '''
            |buildFileList()
            |        .collect { item ->
            |              item.name
            |        }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |          println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:4, sourceLineText:'item.name', messageText:'The statement on line 4 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 15'],
            [lineNumber:8, sourceLineText:'println someName', messageText:'The statement on line 8 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 11'],
        )

        source = '''
            |buildFileList()
            |        .collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |              println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name ->
            |          println name
            |        }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:6, sourceLineText:'println someName', messageText:'The statement on line 6 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 15'],
            [lineNumber:10, sourceLineText:'println name', messageText:'The statement on line 10 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 11'],
        )

        source = '''
            |buildFileList()
            |        .collect { item -> item.name }
            |        .each1 { name -> println name }
            |        .each2 { someName ->
            |              println someName
            |        }
            |        .each3 { name -> println name }
            |        .each4 { name -> println name }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:6, sourceLineText:'println someName', messageText:'The statement on line 6 in class None is at the incorrect indent level: Expected one of columns [13, 17, 21] but was 15'],
        )
    }

    @Test
    void test_MethodChaining_MultilineClosureParameter_Style3_NoViolation() {
        def source = '''
            |buildFileList().collect { item ->
            |                   item.name
            |               }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                   println someName
            |               }
            |               .each3 { name ->
            |                   println name
            |               }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList().collect { item ->
            |                   item.name
            |               }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                   println someName
            |               }
            |               .each3 { name -> println name }
            |               .each4 { name -> println name }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList().collect { item -> item.name }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                   println someName
            |               }
            |               .each3 { name -> println name }
            |               .each4 { name ->
            |                   println name
            |               }
        '''.stripMargin()
        assertNoViolations(source)

        source = '''
            |buildFileList().collect { item -> item.name }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                   println someName
            |               }
            |               .each3 { name -> println name }
            |               .each4 { name -> println name }
        '''.stripMargin()
        assertNoViolations(source)
    }

    @Test
    void test_MethodChaining_MultilineClosureParameter_Style3_Violation() {
        def source = '''
            |buildFileList().collect { item ->
            |                     item.name
            |               }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                 println someName
            |               }
            |               .each3 { name -> println name }
            |               .each4 { name ->
            |println name
            |               }
        '''.stripMargin()
        assertViolations(source,
            [
              lineNumber:3,
              sourceLineText:'item.name',
              messageText:'The statement on line 3 in class None is at the incorrect indent level: Depending on your chaining style, expected one of [5, 9, 13] or one of [20, 24, 28] columns, but was 22'
            ],
            [lineNumber:7, sourceLineText:'println someName', messageText:'The statement on line 7 in class None is at the incorrect indent level: Expected one of columns [20, 24, 28] but was 18'],
            [lineNumber:11, sourceLineText:'println name', messageText:'The statement on line 11 in class None is at the incorrect indent level: Expected one of columns [20, 24, 28] but was 1'],
        )

        source = '''
            |buildFileList().collect { item ->
            |                     item.name
            |               }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                 println someName
            |               }
            |               .each3 { name -> println name }
            |               .each4 { name -> println name }
        '''.stripMargin()
        assertViolations(source,
            [
              lineNumber:3,
              sourceLineText:'item.name',
              messageText:'The statement on line 3 in class None is at the incorrect indent level: Depending on your chaining style, expected one of [5, 9, 13] or one of [20, 24, 28] columns, but was 22'
            ],
            [lineNumber:7, sourceLineText:'println someName', messageText:'The statement on line 7 in class None is at the incorrect indent level: Expected one of columns [20, 24, 28] but was 18'],
        )

        source = '''
            |buildFileList().collect { item -> item.name }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                     println someName
            |               }
            |               .each3 { name -> println name }
            |               .each4 { name ->
            |                 println name
            |               }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:5, sourceLineText:'println someName', messageText:'The statement on line 5 in class None is at the incorrect indent level: Expected one of columns [20, 24, 28] but was 22'],
            [lineNumber:9, sourceLineText:'println name', messageText:'The statement on line 9 in class None is at the incorrect indent level: Expected one of columns [20, 24, 28] but was 18'],
        )

        source = '''
            |buildFileList().collect { item -> item.name }
            |               .each1 { name -> println name }
            |               .each2 { someName ->
            |                     println someName
            |               }
            |               .each3 { name -> println name }
            |               .each4 { name -> println name }
        '''.stripMargin()
        assertViolations(source,
            [lineNumber:5, sourceLineText:'println someName', messageText:'The statement on line 5 in class None is at the incorrect indent level: Expected one of columns [20, 24, 28] but was 22'],
        )
    }
}
