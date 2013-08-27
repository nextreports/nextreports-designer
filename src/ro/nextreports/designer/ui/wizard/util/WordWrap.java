/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.designer.ui.wizard.util;

/**
 * This class provides the static method for word-wrapping text strings.
 *
 * @author Decebal Suiu
 */
class WordWrap {

    /**
     * This method takes a string and wraps it to a line length of no more than
     * wrapLength. If prepend is not null, each resulting line will be prefixed
     * with the prepend string. In that case, resultant line length will be no
     * more than wrapLength + prepend.length()
     */
    public static String wrap(String s, int wrapLength, String prepend) {
        if (s == null) {
            return null;
        }

        if (wrapLength < 0) {
            throw new IllegalArgumentException("bad params");
        }

        int p;
        int p2;
        int offset = 0;
        int marker;

        StringBuffer result = new StringBuffer();

        if (prepend != null) {
            result.append(prepend);
        }

        char[] charAry = s.toCharArray();

        p = marker = 0;

        // each time through the loop, p starts out pointing to the same char as
        // marker
        while (marker < charAry.length) {
            while (p < charAry.length && (charAry[p] != '\n')
                    && ((p - marker) < wrapLength)) {
                p++;
            }

            if (p == charAry.length) {
                result.append(s.substring(marker, p));
                return result.toString();
            }

            if (charAry[p] == '\n') {
                /*
                 * We've got a newline. This newline is bound to have terminated
                 * the while loop above. Step p back one character so that the
                 * isspace(*p) check below will detect that it hit the \n, and
                 * will do the right thing.
                 */
                result.append(s.substring(marker, p + 1));

                if (prepend != null) {
                    result.append(prepend);
                }

                p = marker = p + 1;

                continue;
            }

            p2 = p - 1;

            /*
             * We've either hit the end of the string, or we've gotten past the
             * wrap_length. Back p2 up to the last space before the wrapLength,
             * if there is such a space.
             *
             * Note that if the next character in the string (the character
             * immediately after the break point) is a space, we don't need to
             * back up at all. We'll just print up to our current location, do
             * the newline, and skip to the next line.
             */
            if (p < charAry.length) {
                if (isSpace(charAry[p])) {
                    // the next character is white space. We'll want to skip that.
                    offset = 1;
                } else {
                    // back p2 up to the last white space before the break point
                    while ((p2 > marker) && !isSpace(charAry[p2])) {
                        p2--;
                    }

                    offset = 0;
                }
            }

            /*
             * If the line was completely filled (no place to break), we'll just
             * copy the whole line out and force a break.
             */
            if (p2 == marker) {
                p2 = p - 1;
            }

            if (!isSpace(charAry[p2])) {
                /*
                 * If weren't were able to back up to a space, copy out the
                 * whole line, including the break character (in this case,
                 * we'll be making the string one character longer by inserting
                 * a newline).
                 */
                result.append(s.substring(marker, p2 + 1));
            } else {
                /*
                 * The break character is whitespace. We'll copy out the
                 * characters up to but not including the break character, which
                 * we will effectively replace with a newline.
                 */
                result.append(s.substring(marker, p2));
            }

            // If we have not reached the end of the string, newline
            if (p < charAry.length) {
                result.append("\n");

                if (prepend != null) {
                    result.append(prepend);
                }
            }

            p = marker = p2 + 1 + offset;
        }

        return result.toString();
    }

    public static String wrap(String inString, int wrapLength) {
        return wrap(inString, wrapLength, null);
    }

    private static boolean isSpace(char c) {
        return ((c == '\n') || (c == ' ') || (c == '\t'));
    }

}
