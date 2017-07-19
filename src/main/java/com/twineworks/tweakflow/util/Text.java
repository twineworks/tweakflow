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

import java.util.Collection;
import java.util.Iterator;

public class Text {
  public static String join(Collection<String> strings, String separator){
    StringBuilder builder = new StringBuilder(strings.size()*16+16);

    for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
      builder.append(iterator.next());
      if (iterator.hasNext()){
        builder.append(separator);
      }

    }

    return builder.toString();
  }

  public static String join(Object[] items, String separator){
    StringBuilder builder = new StringBuilder(items.length*16+16);

    for (int i = 0; i < items.length; i++) {
      Object string = items[i];
      builder.append(string);
      if (i < items.length-1){
        builder.append(separator);
      }

    }

    return builder.toString();
  }

  public static String repeat(String s, int n) {
    if(s == null) {
      return null;
    }
    final StringBuilder sb = new StringBuilder(s.length() * n);
    for(int i = 0; i < n; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

}
