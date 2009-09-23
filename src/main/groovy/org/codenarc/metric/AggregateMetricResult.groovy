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
package org.codenarc.metric

/**
 * Represents the aggregate results from applying a metric to multiple source units (e.g. methods, classes).
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
interface AggregateMetricResults {

    Object getTotalValue()
    Object getAverageValue()
    Map getChildren()

}