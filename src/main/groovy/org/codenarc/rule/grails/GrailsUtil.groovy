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
package org.codenarc.rule.grails

/**
 * Utility methods for Grails rule classes. This class is not intended for general use.
 *
 * @author Chris Mair
 * @version $Revision: 109 $ - $Date: 2009-03-30 21:33:32 -0400 (Mon, 30 Mar 2009) $
 */
class GrailsUtil {
    protected static final CONTROLLERS_AND_TAGLIB_FILES = /.*grails-app\/(controllers|taglib)\/.*/

    /**
     * Private constructor. All members are static.
     */
    private GrailsUtil() { }
}