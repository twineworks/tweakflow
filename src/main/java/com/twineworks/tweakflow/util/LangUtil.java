/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Twineworks GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.twineworks.tweakflow.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

public class LangUtil {

  private final static Pattern safeKeyName = Pattern.compile("[-+/a-zA-Z_0-9?]+");
  private final static Pattern safeIdentifier = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9?]*");
  private final static Pattern safeTimeZoneIdentifier = Pattern.compile("[a-zA-Z_]+(/[a-zA-Z_0-9?]+)+");
  private final static HashSet<String> keywords = new HashSet<>(Arrays.asList(

      "interactive",
      "in_scope",

      "global",
      "module",
      "import",
      "export",
      "as",
      "from",
      "alias",

      "meta",
      "doc",
      "via",

      "nil",
      "true",
      "false",
      "not",
      "is",
      "if",
      "then",
      "else",
      "for",
      "try",
      "catch",
      "throw",
      "let",
      "debug",
      "typeof",
      "default",
      "match",

      "provided",

      // data types
      "function",
      "string",
      "boolean",
      "long",
      "dict",
      "list",
      "double",
      "datetime",
      "any",
      "void",

      "library",

      "Infinity"
  ));

  public static String escapeString(String s){
    String escaped = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("#{", "\\#{");

    return escaped;
  }

  public static String getStringLiteral(String s){
    return "\""+escapeString(s)+"\"";
  }

  private final static char[] hexArray = "0123456789abcdef".toCharArray();

  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static String escapeIdentifier(String id){
    // known keywords
    if (keywords.contains(id)){
      return "`"+id+"`";
    }
    // otherwise safe as is?
    if (safeIdentifier.matcher(id).matches()){
      return id;
    }

    if (id.contains("`")) throw new IllegalArgumentException("identifier cannot contain ` character");
    return "`"+id+"`";
  }

  public static String escapeTimeZoneIdentifier(String id){

    // safe as regular identifier?
    if (safeIdentifier.matcher(id).matches()){
      return id;
    }

    // safe as time zone identifier foo/bar/baz?
    if (safeTimeZoneIdentifier.matcher(id).matches()){
      return id;
    }

    if (id.contains("`")) throw new IllegalArgumentException("time zone identifier cannot contain ` character");

    return "`"+id+"`";
  }

  public static String getKeyLiteral(String id){

    // safe as is?
    if (safeKeyName.matcher(id).matches()){
      return ":"+id;
    }

    if (id.contains("`")){
      return getStringLiteral(id);
    }

    return ":`"+id+"`";
  }

}
