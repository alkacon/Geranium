/*
 * This library is part of Geranium -
 * an open source UI library for GWT.
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)-
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.geranium.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Additional string related helper methods.<p>
 * 
 * @since 8.0.0
 * 
 */
public final class ClientStringUtil {

    /**
     * Prevent instantiation.<p> 
     */
    private ClientStringUtil() {

        // empty
    }

    /**
     * Returns the exception message.<p>
     * 
     * @param t the exception to get the message for
     * 
     * @return the exception message
     */
    public static String getMessage(Throwable t) {

        String message = t.getLocalizedMessage();
        if (message == null) {
            message = t.getMessage();
        }
        if (message == null) {
            message = t.getClass().getName();
        }
        return message;
    }

    /**
     * Returns the stack trace of the Throwable as a string.<p>
     * 
     * @param t the Throwable for which the stack trace should be returned
     * @param separator the separator between the lines of the stack trace 
     * 
     * @return a string representing a stack trace 
     */
    public static String getStackTrace(Throwable t, String separator) {

        return getStackTraceAsString(t.getStackTrace(), separator);
    }

    /**
     * Returns the stack trace as a string.<p>
     * 
     * @param trace the stack trace
     * @param separator the separator between the lines of the stack trace 
     * 
     * @return a string representing a stack trace 
     */
    public static String getStackTraceAsString(StackTraceElement[] trace, String separator) {

        String result = "";
        for (StackTraceElement elem : trace) {
            result += elem.toString();
            result += separator;
        }
        return result;
    }

    /**
     * The parseFloat() function parses a string and returns a float.<p>
     * 
     * Only the first number in the string is returned. Leading and trailing spaces are allowed.
     * 
     * @param str the string to be parsed
     * 
     * @return the parsed number
     */
    public static native double parseFloat(String str) /*-{
                                                       var ret = parseFloat(str, 10);
                                                       if (isNaN(ret)) {
                                                       return 0;
                                                       }
                                                       return ret;
                                                       }-*/;

    /**
     * The parseInt() function parses a string and returns an integer.<p>
     * 
     * Only the first number in the string is returned. Leading and trailing spaces are allowed.
     * If the first character cannot be converted to a number, parseInt() returns zero.<p>
     * 
     * @param str the string to be parsed
     * 
     * @return the parsed number
     */
    public static native int parseInt(String str) /*-{
                                                  var ret = parseInt(str, 10);
                                                  if (isNaN(ret)) {
                                                  return 0;
                                                  }
                                                  return ret;
                                                  }-*/;

    /**
     * Pushes a String into a javascript array.<p>
     * 
     * @param array the array to push the String into
     * @param s the String to push into the array
     */
    public static native void pushArray(JavaScriptObject array, String s) /*-{
                                                                          array.push(s);
                                                                          }-*/;

    /**
     * Shortens the string to the given maximum length.<p>
     * 
     * Will include HTML entity ellipses replacing the cut off text.<p>
     * 
     * @param text the string to shorten
     * @param maxLength the maximum length
     * 
     * @return the shortened string
     */
    public static String shortenString(String text, int maxLength) {

        if (text.length() <= maxLength) {
            return text;
        }
        String newText = text.substring(0, maxLength - 1);
        if (text.startsWith("/")) {
            // file name?
            newText = formatResourceName(text, maxLength);
        } else if (maxLength > 2) {
            // enough space for ellipsis?
            newText += DomUtil.Entity.hellip.html();
        }
        if (isEmptyOrWhitespaceOnly(newText)) {
            // if empty, it could break the layout
            newText = DomUtil.Entity.nbsp.html();
        }
        return newText;
    }

    /**
     * Replaces occurrences of special control characters in the given input with 
     * a HTML representation.<p>
     * 
     * This method currently replaces line breaks to <code>&lt;br/&gt;</code> and special HTML chars 
     * like <code>&lt; &gt; &amp; &quot;</code> with their HTML entity representation.<p>
     * 
     * @param source the String to escape
     * 
     * @return the escaped String
     */
    public static String escapeHtml(String source) {

        if (source == null) {
            return null;
        }
        source = escapeXml(source);
        source = substitute(source, "\r", "");
        source = substitute(source, "\n", "<br/>\n");
        return source;
    }

    /**
     * Substitutes <code>searchString</code> in the given source String with <code>replaceString</code>.<p>
     * 
     * This is a high-performance implementation which should be used as a replacement for 
     * <code>{@link String#replaceAll(java.lang.String, java.lang.String)}</code> in case no
     * regular expression evaluation is required.<p>
     * 
     * @param source the content which is scanned
     * @param searchString the String which is searched in content
     * @param replaceString the String which replaces <code>searchString</code>
     * 
     * @return the substituted String
     */
    public static String substitute(String source, String searchString, String replaceString) {

        if (source == null) {
            return null;
        }

        if (isEmpty(searchString)) {
            return source;
        }

        if (replaceString == null) {
            replaceString = "";
        }
        int len = source.length();
        int sl = searchString.length();
        int rl = replaceString.length();
        int length;
        if (sl == rl) {
            length = len;
        } else {
            int c = 0;
            int s = 0;
            int e;
            while ((e = source.indexOf(searchString, s)) != -1) {
                c++;
                s = e + sl;
            }
            if (c == 0) {
                return source;
            }
            length = len - (c * (sl - rl));
        }

        int s = 0;
        int e = source.indexOf(searchString, s);
        if (e == -1) {
            return source;
        }
        StringBuffer sb = new StringBuffer(length);
        while (e != -1) {
            sb.append(source.substring(s, e));
            sb.append(replaceString);
            s = e + sl;
            e = source.indexOf(searchString, s);
        }
        e = len;
        sb.append(source.substring(s, e));
        return sb.toString();
    }

    /**
     * Formats a resource name that it is displayed with the maximum length and path information is adjusted.<p>
     * In order to reduce the length of the displayed names, single folder names are removed/replaced with ... successively, 
     * starting with the second! folder. The first folder is removed as last.<p>
     * 
     * Example: formatResourceName("/myfolder/subfolder/index.html", 21) returns <code>/myfolder/.../index.html</code>.<p>
     * 
     * @param name the resource name to format
     * @param maxLength the maximum length of the resource name (without leading <code>/...</code>)
     * 
     * @return the formatted resource name
     */
    public static String formatResourceName(String name, int maxLength) {

        if (name == null) {
            return null;
        }

        if (name.length() <= maxLength) {
            return name;
        }

        int total = name.length();
        String[] names = splitAsArray(name, "/");
        if (name.endsWith("/")) {
            names[names.length - 1] = names[names.length - 1] + "/";
        }
        for (int i = 1; (total > maxLength) && (i < (names.length - 1)); i++) {
            if (i > 1) {
                names[i - 1] = "";
            }
            names[i] = "...";
            total = 0;
            for (int j = 0; j < names.length; j++) {
                int l = names[j].length();
                total += l + ((l > 0) ? 1 : 0);
            }
        }
        if (total > maxLength) {
            names[0] = (names.length > 2) ? "" : (names.length > 1) ? "..." : names[0];
        }

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < names.length; i++) {
            if (names[i].length() > 0) {
                result.append("/");
                result.append(names[i]);
            }
        }

        return result.toString();
    }

    /**
     * Returns <code>true</code> if the provided String is either <code>null</code>
     * or the empty String <code>""</code>.<p> 
     * 
     * @param value the value to check
     * 
     * @return true, if the provided value is null or the empty String, false otherwise
     */
    public static boolean isEmpty(String value) {

        return (value == null) || (value.length() == 0);
    }

    /**
     * Returns <code>true</code> if the provided String is either <code>null</code>
     * or contains only white spaces.<p> 
     * 
     * @param value the value to check
     * 
     * @return true, if the provided value is null or contains only white spaces, false otherwise
     */
    public static boolean isEmptyOrWhitespaceOnly(String value) {

        return isEmpty(value) || (value.trim().length() == 0);
    }

    /**
     * Returns <code>true</code> if the provided String is neither <code>null</code>
     * nor the empty String <code>""</code>.<p> 
     * 
     * @param value the value to check
     * 
     * @return true, if the provided value is not null and not the empty String, false otherwise
     */
    public static boolean isNotEmpty(String value) {

        return (value != null) && (value.length() != 0);
    }

    /**
     * Returns <code>true</code> if the provided String is neither <code>null</code>
     * nor contains only white spaces.<p> 
     * 
     * @param value the value to check
     * 
     * @return <code>true</code>, if the provided value is <code>null</code> 
     *          or contains only white spaces, <code>false</code> otherwise
     */
    public static boolean isNotEmptyOrWhitespaceOnly(String value) {

        return (value != null) && (value.trim().length() > 0);
    }

    /**
     * Concatenates multiple paths and separates them with '/'.<p>
     * 
     * Consecutive slashes will be reduced to a single slash in the resulting string.
     * For example, joinPaths("/foo/", "/bar", "baz") will return "/foo/bar/baz".
     * 
     * @param paths the array of paths
     *  
     * @return the joined path 
     */
    public static String joinPaths(String... paths) {

        String result = listAsString(Arrays.asList(paths), "/");
        // result may now contain multiple consecutive slashes, so reduce them to single slashes
        result = result.replaceAll("/+", "/");
        return result;
    }

    /**
     * Returns a string representation for the given list using the given separator.<p>
     * 
     * @param <E> type of list entries
     * @param list the list to write
     * @param separator the item separator string
     * 
     * @return the string representation for the given map
     */
    public static <E> String listAsString(List<E> list, String separator) {

        StringBuffer string = new StringBuffer(128);
        Iterator<E> it = list.iterator();
        while (it.hasNext()) {
            string.append(it.next());
            if (it.hasNext()) {
                string.append(separator);
            }
        }
        return string.toString();
    }

    /**
     * Splits a String into substrings along the provided char delimiter and returns
     * the result as an Array of Substrings.<p>
     *
     * @param str the String to split
     * @param splitter the delimiter to split at
     *
     * @return the Array of substrings
     */
    public static String[] splitAsArray(String str, String splitter) {

        return str.split(splitter);
    }

    /**
     * Splits a String into substrings along the provided char delimiter and returns
     * the result as a List of Substrings.<p>
     *
     * @param source the String to split
     * @param delimiter the delimiter to split at
     * @param trim flag to indicate if leading and trailing white spaces should be omitted
     *
     * @return the List of splitted Substrings
     */
    public static List<String> splitAsList(String source, char delimiter, boolean trim) {

        List<String> result = new ArrayList<String>();
        int i = 0;
        int l = source.length();
        int n = source.indexOf(delimiter);
        while (n != -1) {
            // zero - length items are not seen as tokens at start or end
            if ((i < n) || ((i > 0) && (i < l))) {
                result.add(trim ? source.substring(i, n).trim() : source.substring(i, n));
            }
            i = n + 1;
            n = source.indexOf(delimiter, i);
        }
        // is there a non - empty String to cut from the tail? 
        if (n < 0) {
            n = source.length();
        }
        if (i < n) {
            result.add(trim ? source.substring(i).trim() : source.substring(i));
        }
        return result;
    }

    /**
     * Splits a String into substrings along the provided String delimiter and returns
     * the result as List of Substrings.<p>
     *
     * @param source the String to split
     * @param delimiter the delimiter to split at
     *
     * @return the Array of splitted Substrings
     */
    public static List<String> splitAsList(String source, String delimiter) {

        return splitAsList(source, delimiter, false);
    }

    /**
     * Splits a String into substrings along the provided String delimiter and returns
     * the result as List of Substrings.<p>
     * 
     * @param source the String to split
     * @param delimiter the delimiter to split at
     * @param trim flag to indicate if leading and trailing white spaces should be omitted
     * 
     * @return the Array of splitted Substrings
     */
    public static List<String> splitAsList(String source, String delimiter, boolean trim) {

        int dl = delimiter.length();
        if (dl == 1) {
            // optimize for short strings
            return splitAsList(source, delimiter.charAt(0), trim);
        }

        List<String> result = new ArrayList<String>();
        int i = 0;
        int l = source.length();
        int n = source.indexOf(delimiter);
        while (n != -1) {
            // zero - length items are not seen as tokens at start or end:  ",," is one empty token but not three
            if ((i < n) || ((i > 0) && (i < l))) {
                result.add(trim ? source.substring(i, n).trim() : source.substring(i, n));
            }
            i = n + dl;
            n = source.indexOf(delimiter, i);
        }
        // is there a non - empty String to cut from the tail? 
        if (n < 0) {
            n = source.length();
        }
        if (i < n) {
            result.add(trim ? source.substring(i).trim() : source.substring(i));
        }
        return result;
    }

    /**
     * Checks whether one path is a prefix path of another, i.e. its path components are 
     * the initial path components of the second path.<p>
     * 
     * It is not enough to just use {@link String#startsWith}, because we want /foo/bar to 
     * be a prefix path of  /foo/bar/baz, but not of /foo/bar42.<p>
     *  
     * @param firstPath the first path 
     * @param secondPath the second path 
     * 
     * @return true if the first path is a prefix path of the second path 
     */
    public static boolean isPrefixPath(String firstPath, String secondPath) {

        firstPath = joinPaths(firstPath, "/");
        secondPath = joinPaths(secondPath, "/");
        return secondPath.startsWith(firstPath);
    }

    /**
     * Splits a String into substrings along the provided <code>paramDelim</code> delimiter,
     * then each substring is treat as a key-value pair delimited by <code>keyValDelim</code>.<p>
     * 
     * @param source the string to split
     * @param paramDelim the string to delimit each key-value pair
     * @param keyValDelim the string to delimit key and value
     * 
     * @return a map of splitted key-value pairs
     */
    public static Map<String, String> splitAsMap(String source, String paramDelim, String keyValDelim) {

        int keyValLen = keyValDelim.length();
        // use LinkedHashMap to preserve the order of items 
        Map<String, String> params = new LinkedHashMap<String, String>();
        Iterator<String> itParams = splitAsList(source, paramDelim, true).iterator();
        while (itParams.hasNext()) {
            String param = itParams.next();
            int pos = param.indexOf(keyValDelim);
            String key = param;
            String value = "";
            if (pos > 0) {
                key = param.substring(0, pos);
                if ((pos + keyValLen) < param.length()) {
                    value = param.substring(pos + keyValLen);
                }
            }
            params.put(key, value);
        }
        return params;
    }

    /**
     * Escapes a String so it may be printed as text content or attribute
     * value in a HTML page or an XML file.<p>
     * 
     * This method replaces the following characters in a String:
     * <ul>
     * <li><b>&lt;</b> with &amp;lt;
     * <li><b>&gt;</b> with &amp;gt;
     * <li><b>&amp;</b> with &amp;amp;
     * <li><b>&quot;</b> with &amp;quot;
     * </ul><p>
     * 
     * @param source the string to escape
     * @param doubleEscape if <code>false</code>, all entities that already are escaped are left untouched
     * 
     * @return the escaped string
     */
    private static String escapeXml(String source) {

        if (source == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(source.length() * 2);

        for (int i = 0; i < source.length(); ++i) {
            char ch = source.charAt(i);
            switch (ch) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    // don't escape already escaped international and special characters
                    int terminatorIndex = source.indexOf(";", i);
                    if (terminatorIndex > 0) {
                        if (source.substring(i + 1, terminatorIndex).matches("#[0-9]+")) {
                            result.append(ch);
                            break;
                        }
                    }

                    // note that to other "break" in the above "if" block
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(ch);
            }
        }
        return new String(result);
    }
}