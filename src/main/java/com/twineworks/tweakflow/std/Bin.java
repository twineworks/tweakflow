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

package com.twineworks.tweakflow.std;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.*;

import java.util.Arrays;
import java.util.Iterator;

public final class Bin {

  // (list xs) -> binary
  public static final class concat implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {
      ListValue values = xs.list();
      if (values == null) return Values.NIL;

      // first pass to determine target size and type conformity
      int totalSize = 0;
      for (Value value : values) {
        if (value.isNil()) return Values.NIL;
        if (!value.isBinary()) throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot concat non-binary type "+value.type().name());
        totalSize += value.bytes().length;
      }

      if (totalSize == 0) return Values.EMPTY_BINARY;
      if (values.size() == 1) return values.get(0);

      byte[] b = values.get(0).bytes();
      byte[] target = Arrays.copyOf(b, totalSize);
      int filled = b.length;

      Iterator<Value> iterator = values.iterator();
      // skip the first item
      iterator.next();

      while (iterator.hasNext()) {
        b = iterator.next().bytes();
        System.arraycopy(b, 0, target, filled, b.length);
        filled+=b.length;
      }

      return Values.make(target);
    }
  }

  // (binary x) -> long
  public static final class size implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.bytes().length);
    }
  }

  // (binary x, long i) -> long
  public static final class byte_at implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i) {
      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;
      long idx = i.longNum();
      if (idx < 0) return Values.NIL;
      byte[] bytes = x.bytes();
      if (idx >= bytes.length) return Values.NIL;

      return Values.make(Byte.toUnsignedInt(bytes[(int)idx]));
    }
  }

  // (binary x, long i, boolean big_endian=false) -> long
  public static final class word_at implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) return Values.NIL;

      long idx = i.longNum();
      if (idx < 0) return Values.NIL;
      byte[] bytes = x.bytes();
      if (idx+1 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      int a = Byte.toUnsignedInt(bytes[intIdx]);
      int b = Byte.toUnsignedInt(bytes[intIdx+1]);

      int out;
      if (bigEndian.bool()){
        // big endian, most significant first
        out = (a << 8) | b;
      }
      else{
        // little endian, most significant last
        out = a | (b << 8);
      }

      return Values.make(out);
    }
  }

  // (binary x, long i, boolean big_endian=false) -> long
  public static final class dword_at implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) return Values.NIL;

      long idx = i.longNum();
      if (idx < 0) return Values.NIL;
      byte[] bytes = x.bytes();
      if (idx+3 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      int a = Byte.toUnsignedInt(bytes[intIdx]);
      int b = Byte.toUnsignedInt(bytes[intIdx+1]);
      int c = Byte.toUnsignedInt(bytes[intIdx+2]);
      int d = Byte.toUnsignedInt(bytes[intIdx+3]);

      long out;
      if (bigEndian.bool()){
        // big endian, most significant first
        out = (((long)a) << 24) | (b << 16) | (c << 8) | d;
      }
      else{
        // little endian, most significant last
        out = (((long)d) << 24) | (c << 16) | (b << 8) | a;
      }

      return Values.make(out);
    }
  }

  // (binary x, long i, boolean big_endian=false) -> long
  public static final class long_at implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) return Values.NIL;

      long idx = i.longNum();
      if (idx < 0) return Values.NIL;
      byte[] bytes = x.bytes();
      if (idx+7 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      long a = Byte.toUnsignedLong(bytes[intIdx]);
      long b = Byte.toUnsignedLong(bytes[intIdx+1]);
      long c = Byte.toUnsignedLong(bytes[intIdx+2]);
      long d = Byte.toUnsignedLong(bytes[intIdx+3]);
      long e = Byte.toUnsignedLong(bytes[intIdx+4]);
      long f = Byte.toUnsignedLong(bytes[intIdx+5]);
      long g = Byte.toUnsignedLong(bytes[intIdx+6]);
      long h = Byte.toUnsignedLong(bytes[intIdx+7]);

      long out;
      if (bigEndian.bool()){
        // big endian, most significant first
        out = (a << 56) | (b << 48) | (c << 40) | (d << 32) | (e << 24) | (f << 16) | (g << 8) | h;
      }
      else{
        // little endian, most significant last
        out = (h << 56) | (g << 48) | (f << 40) | (e << 32) | (d << 24) | (c << 16) | (b << 8) | a;
      }

      return Values.make(out);
    }
  }

  // those need an endianness parameter
  // function floatAt: (binary x, long i) -> string
  // function doubleAt: (binary x, long i) -> string

  // function toString: (binary x, string charset='utf-8') -> string
  // function fromString: (string x, string charset='utf-8') -> binary

  // function slice: (binary xs, long start=0, long end) -> list

  // function base64encoder: (string type='MIME' or 'basic') -> function
  // function base64decoder: (string type='MIME' or 'basic') -> function

}
