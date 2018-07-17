/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Twineworks GmbH
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

import java.util.regex.Pattern;

public class LangUtil {

  private static Pattern safeIdentifier = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9?]*");

  public static String escapeString(String s){
    String escaped = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("#", "\\#");

    return escaped;
  }

  public static String getStringLiteral(String s){
    return "\""+escapeString(s)+"\"";
  }

  public static String escapeIdentifier(String id){
    if (safeIdentifier.matcher(id).matches()){
      return id;
    }
    if (id.contains("`")) throw new IllegalArgumentException("identifier cannot contain ` character");
    return "`"+id+"`";
  }

}
