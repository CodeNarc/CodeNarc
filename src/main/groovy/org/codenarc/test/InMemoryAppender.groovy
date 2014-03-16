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

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent

/**
 * Log4J Appender that saves all logged loggingEvents in a List.
 *
 * @author Chris Mair
  */
class InMemoryAppender extends AppenderSkeleton implements Closeable {

    private final List loggingEvents = []

    /**
     * Return the List of LoggingEvents logged to this Appender
     * @return the List of logged LoggingEvents
     */
    List<String> getLoggingEvents() {
        return loggingEvents
    }

    void clearLoggedMessages() {
        loggingEvents.clear()
    }

    protected void append(LoggingEvent loggingEvent) {
        loggingEvents.add(loggingEvent)
    }

    @Override
    void close() {
        // Do nothing
    }

    boolean requiresLayout() {
        return false
    }

}
