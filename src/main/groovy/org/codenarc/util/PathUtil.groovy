/*
 * Copyright 2010 the original author or authors.
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
 * Path-related utility methods.
 *
 * @author Chris Mair
  */
class PathUtil {

    private static final SEP = '/'

    static String getName(String path) {
        if (!path) {
            return null
        }
        int separatorIndex1 = path.lastIndexOf('/')
        int separatorIndex2 = path.lastIndexOf('\\')
        int separatorIndex = [separatorIndex1, separatorIndex2].max()
        (separatorIndex == -1) ? path : path[separatorIndex + 1 .. -1]
    }

     static String getParentPath(String filePath) {
         def normalizedPath = normalizePath(filePath)
         def partList = normalizedPath ? normalizedPath.tokenize(SEP) : []
         if (partList.size() < 2) {
             return null
         }
         def parentList = partList[0..-2]
         parentList.join(SEP)
     }

     static String normalizePath(String path) {
         path ? path.replaceAll('\\\\', SEP) : path
     }

     static String removePathPrefix(String prefix, String path) {
         def resultPath = path
         if (prefix && resultPath.startsWith(prefix)) {
             resultPath = resultPath - prefix
             return removeLeadingSlash(resultPath)
         }
         resultPath
     }

     private static String removeLeadingSlash(path) {
         (path.startsWith('\\') || path.startsWith(SEP)) ? path[1 .. -1] : path
     }

    // Private constructor to prevent instantiation. All members are static.
    private PathUtil() { }
}
