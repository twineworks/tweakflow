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

package com.twineworks.tweakflow.io;

public class MagicNumbers {

  public static class Format {

    public final static byte VOID = 0;

    public final static byte BOOLEAN = 1;

    public final static byte BINARY = 5;

    public final static byte LONG = 10;

    public final static byte DECIMAL = 20;

    public final static byte DOUBLE = 30;

    public final static byte STRING = 40;

    public final static byte DATETIME = 50;

    public final static byte LIST = 60;

    public final static byte DICT = 70;

    public final static byte FUNCTION = 100;

    public final static byte NOTHING = -1;

    public static final byte STRING_PART = 42;

    public final static byte LIST_HEAD = 62;

    public final static byte DICT_HEAD = 72;

    public final static byte KEY = 80;
    public final static byte KEY_PART = 82;

    public static final byte BINARY_PART = 7;
    public static final byte DECIMAL_PART = 22;

    public static final byte DATETIME_PART = 52;


  }

}
