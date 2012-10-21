/*
 * Copyright 2012 the original author or authors.
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
 package org.codenarc.test

import org.apache.log4j.Logger
import org.apache.log4j.spi.LoggingEvent

/**
 * Contains common static utility methods for tests
 *
 * @author Chris Mair
 */
class TestUtil {

    /**
     * Assert that the specified code throws an exception of the specified type. Return the thrown exception message.
     * @param expectedExceptionClass - the Class of exception that is expected; may be null
     * @param code - the Closure containing the code to be executed, which is expected to throw an exception of the specified type
     * @return the message from the thrown Exception
     *
     * @throws AssertionError - if no exception is thrown by the code or if the thrown exception is not of the expected type
     */
    static String shouldFail(Class expectedExceptionClass, Closure code) {
        def actualException = null
        try {
            code.call()
        } catch (Throwable thrown) {
            actualException = thrown
        }
        assert actualException, "No exception thrown. Expected [${expectedExceptionClass?.getName()}]"
        if (expectedExceptionClass) {
            assert expectedExceptionClass.isAssignableFrom(actualException.class), "Expected [${expectedExceptionClass.getName()}] but was [${actualException.class.name}]"
        }
        return actualException.getMessage()
    }

    /**
     * Assert that the specified code throws an exception (of any type). Return the thrown exception message.
     * @param code - the Closure containing the code to be executed, which is expected to throw an exception of the specified type
     * @return the message from the thrown Exception
     *
     * @throws AssertionError - if no exception is thrown by the code or if the thrown exception is not of the expected type
     */
    static String shouldFail(Closure code) {
        shouldFail(null, code)
    }

    /**
     * Assert that the specified code throws an exception whose message contains the specified text.
     * @param expectedExceptionClass - the class of the Throwable that is expected to be thrown
     * @param expectedText - the text expected within the exception message
     * @param code - the Closure containing the code to be executed, which is expected to throw an exception of the specified type
     * @return the thrown Exception message
     *
     * @throws AssertionError - if no exception is thrown by the code or if the text is not contained in the exception message
     */
    static String shouldFailWithMessageContaining(Class expectedExceptionClass, String expectedText, Closure code) {
        def msg = shouldFail(expectedExceptionClass, code)
        assert msg?.contains(expectedText), "Message [$msg] does not contain [$expectedText]"
        return msg
    }

    /**
     * Assert that the specified closure should throw an exception whose message contains text
     * @param text - the text expected within the message; may be a single String or a List of Strings
     * @param closure - the Closure to execute
     */
    static void shouldFailWithMessageContaining(text, Closure closure) {
        def message = shouldFail(closure)
        def strings = text instanceof List ? text : [text]
        strings.each { string ->
            assert message.contains(string), "[$message] does not contain [$string]"
        }
    }

    /**
     * Return true if the text contains each of the specified strings
     * @param text - the text to search
     * @param strings - the Strings to check for; toString() is invoked on each element
     */
    static boolean containsAll(String text, strings) {
        strings.every { text.contains(it.toString()) }
    }

    /**
     * Assert that the text contains each of the specified strings
     * @param text - the text to search
     * @param strings - the Strings that must be present within text; toString() is invoked on each element
     */
    static void assertContainsAll(String text, strings) {
        strings.each { assert text.contains(it.toString()), "text does not contain [$it]" }
    }

    /**
     * Assert that the text contains each of the specified strings, in order
     * @param text - the text to search
     * @param strings - the Strings that must be present within text, and appear
     *      in the order specified; toString() is applied to each.
     */
    static void assertContainsAllInOrder(String text, strings) {
        def startIndex = 0
        strings.each { string ->
            def index = text.indexOf(string.toString(), startIndex)
            assert index > -1, "text does not contain [$string]"
            startIndex = index + 1
        }
    }

    /**
     * Assert that the two collections have equal Sets of elements. In other words, assert that
     * the two collections are the same, ignoring ordering and duplicates.
     */
    static void assertEqualSets(Collection collection1, Collection collection2) {
        assert collection1 as Set == collection2 as Set
    }

    static String captureSystemOut(Closure closure) {
        def originalSystemOut = System.out
        def outputStream = new ByteArrayOutputStream()
        try {
            System.out = new PrintStream(outputStream)
            closure()
        }
        finally {
            System.out = originalSystemOut
        }
        outputStream.toString()
    }

    static List<LoggingEvent> captureLog4JMessages(Closure closure) {
        def inMemoryAppender = new InMemoryAppender()
        def logger = Logger.rootLogger
        logger.addAppender(inMemoryAppender)
        try {
            closure()
        }
        finally {
            logger.removeAppender(inMemoryAppender)
        }
        return inMemoryAppender.getLoggingEvents()
    }

}
