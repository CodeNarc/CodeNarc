/*
 * Copyright 2008 the original author or authors.
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

/**
 * Contains property-related static utility methods
 *
 * @author Chris Mair
  */
class PropertyUtil {

    /**
     * Set the value of the named property on the specified Object from a String value.
     * If the name specifies an int, long or boolean value then trim and parse the provided String value
     * and convert to the appropriate type.
     * @param object - the Object whose field should be set
     * @param name - the property name to set
     * @param value - the property value as a String
     * @throws NoSuchFieldException - if the object does not contain the named field
     */
    static void setPropertyFromString(Object object, String propertyName, String propertyValue) {
        def property = object.metaClass.getMetaProperty(propertyName)
        if (property == null) {
            throw new NoSuchFieldException(propertyName)
        }

        Object newPropertyValue = propertyValue

        if (property.type == int) {
            newPropertyValue = Integer.parseInt(propertyValue.trim())
        }

        if (property.type == long) {
            newPropertyValue = Long.parseLong(propertyValue.trim())
        }

        if (property.type == boolean) {
            newPropertyValue = Boolean.parseBoolean(propertyValue.trim())
        }

        if (property.type == BigDecimal) {
            newPropertyValue = new BigDecimal(propertyValue.trim())
        }

        object[propertyName] = newPropertyValue
    }

    /**
     * Private constructor. All methods are static.
     */
    private PropertyUtil() { }
}
