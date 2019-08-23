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
import com.twineworks.tweakflow.util.LangUtil;

import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;

public final class Bin {

  private static int hexToBin(char ch) {
    if ('0' <= ch && ch <= '9') return ch - '0';
    if ('A' <= ch && ch <= 'F') return ch - 'A' + 10;
    if ('a' <= ch && ch <= 'f') return ch - 'a' + 10;
    return -1;
  }

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
        if (!value.isBinary())
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot concat non-binary type " + value.type().name());
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
        filled += b.length;
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

      return Values.make(Byte.toUnsignedInt(bytes[(int) idx]));
    }
  }

  // (long x, boolean signed=false) -> binary
  public static final class of_byte implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value signed) {
      if (x == Values.NIL) return Values.NIL;
      if (signed == Values.NIL) throw new LangException(LangError.NIL_ERROR, "signed cannot be nil");

      long num = x.longNum();
      boolean isSigned = signed.bool();

      byte[] ret = new byte[1];
      if (isSigned){
        if (num < Byte.MIN_VALUE || num > Byte.MAX_VALUE) throw new LangException(LangError.ILLEGAL_ARGUMENT, "signed byte value out of range: "+num);
        ret[0] = (byte) num;
      }
      else {
        if (num < 0 || num > 255) throw new LangException(LangError.ILLEGAL_ARGUMENT, "unsigned byte value out of range: "+num);
        if (num < 128){
          ret[0] = (byte) num;
        }
        else {
          ret[0] = (byte) (num-256);
        }
      }

      return Values.make(ret);
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
      if (idx + 1 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      int a = Byte.toUnsignedInt(bytes[intIdx]);
      int b = Byte.toUnsignedInt(bytes[intIdx + 1]);

      int out;
      if (bigEndian.bool()) {
        // big endian, most significant first
        out = (a << 8) | b;
      } else {
        // little endian, most significant last
        out = a | (b << 8);
      }

      return Values.make(out);
    }
  }

  // (long x, boolean signed=false, boolean big_endian=false) -> binary
  public static final class of_word implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value signed, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (signed == Values.NIL) throw new LangException(LangError.NIL_ERROR, "signed cannot be nil");
      if (bigEndian == Values.NIL) throw new LangException(LangError.NIL_ERROR, "big_endian cannot be nil");

      long num = x.longNum();
      boolean isSigned = signed.bool();
      boolean isBigEndian = bigEndian.bool();
      short word;
      byte[] ret = new byte[2];

      if (isSigned){
        if (num < Short.MIN_VALUE || num > Short.MAX_VALUE) throw new LangException(LangError.ILLEGAL_ARGUMENT, "signed word value out of range: "+num);
        word = (short) num;
      }
      else {
        long MAX_UNSIGNED = (long)Short.MAX_VALUE*2+1;
        if (num < 0 || num > MAX_UNSIGNED) throw new LangException(LangError.ILLEGAL_ARGUMENT, "unsigned word value out of range: "+num);
        if (num <= Short.MAX_VALUE){
          word = (short) num;
        }
        else {
          word = (short) (num-MAX_UNSIGNED-1);
        }
      }
      if (isBigEndian){
        ret[0] = (byte) (word >>> 8);
        ret[1] = (byte) (word);
      }
      else{
        ret[0] = (byte) (word);
        ret[1] = (byte) (word >>> 8);
      }

      return Values.make(ret);
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
      if (idx + 3 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      int a = Byte.toUnsignedInt(bytes[intIdx]);
      int b = Byte.toUnsignedInt(bytes[intIdx + 1]);
      int c = Byte.toUnsignedInt(bytes[intIdx + 2]);
      int d = Byte.toUnsignedInt(bytes[intIdx + 3]);

      long out;
      if (bigEndian.bool()) {
        // big endian, most significant first
        out = (((long) a) << 24) | (b << 16) | (c << 8) | d;
      } else {
        // little endian, most significant last
        out = (((long) d) << 24) | (c << 16) | (b << 8) | a;
      }

      return Values.make(out);
    }
  }

  // (long x, boolean signed=false, boolean big_endian=false) -> binary
  public static final class of_dword implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value signed, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (signed == Values.NIL) throw new LangException(LangError.NIL_ERROR, "signed cannot be nil");
      if (bigEndian == Values.NIL) throw new LangException(LangError.NIL_ERROR, "big_endian cannot be nil");

      long num = x.longNum();
      boolean isSigned = signed.bool();
      boolean isBigEndian = bigEndian.bool();
      int dword;
      byte[] ret = new byte[4];

      if (isSigned){
        if (num < Integer.MIN_VALUE || num > Integer.MAX_VALUE) throw new LangException(LangError.ILLEGAL_ARGUMENT, "signed dword value out of range: "+num);
        dword = (int) num;
      }
      else {
        long MAX_UNSIGNED = (long)Integer.MAX_VALUE*2+1;
        if (num < 0 || num > MAX_UNSIGNED) throw new LangException(LangError.ILLEGAL_ARGUMENT, "unsigned dword value out of range: "+num);
        if (num <= Integer.MAX_VALUE){
          dword = (int) num;
        }
        else {
          dword = (int) (num-MAX_UNSIGNED-1);
        }
      }
      if (isBigEndian){
        ret[0] = (byte) (dword >>> 24);
        ret[1] = (byte) (dword >>> 16);
        ret[2] = (byte) (dword >>> 8);
        ret[3] = (byte) (dword);
      }
      else{
        ret[3] = (byte) (dword >>> 24);
        ret[2] = (byte) (dword >>> 16);
        ret[1] = (byte) (dword >>> 8);
        ret[0] = (byte) (dword);
      }

      return Values.make(ret);
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
      if (idx + 7 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      long a = Byte.toUnsignedLong(bytes[intIdx]);
      long b = Byte.toUnsignedLong(bytes[intIdx + 1]);
      long c = Byte.toUnsignedLong(bytes[intIdx + 2]);
      long d = Byte.toUnsignedLong(bytes[intIdx + 3]);
      long e = Byte.toUnsignedLong(bytes[intIdx + 4]);
      long f = Byte.toUnsignedLong(bytes[intIdx + 5]);
      long g = Byte.toUnsignedLong(bytes[intIdx + 6]);
      long h = Byte.toUnsignedLong(bytes[intIdx + 7]);

      long out;
      if (bigEndian.bool()) {
        // big endian, most significant first
        out = (a << 56) | (b << 48) | (c << 40) | (d << 32) | (e << 24) | (f << 16) | (g << 8) | h;
      } else {
        // little endian, most significant last
        out = (h << 56) | (g << 48) | (f << 40) | (e << 32) | (d << 24) | (c << 16) | (b << 8) | a;
      }

      return Values.make(out);
    }
  }

  // (long x, boolean big_endian=false) -> binary
  public static final class of_long implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) throw new LangException(LangError.NIL_ERROR, "big_endian cannot be nil");

      long num = x.longNum();
      boolean isBigEndian = bigEndian.bool();

      byte[] ret = new byte[8];

      if (isBigEndian){
        ret[0] = (byte) (num >>> 56);
        ret[1] = (byte) (num >>> 48);
        ret[2] = (byte) (num >>> 40);
        ret[3] = (byte) (num >>> 32);
        ret[4] = (byte) (num >>> 24);
        ret[5] = (byte) (num >>> 16);
        ret[6] = (byte) (num >>> 8);
        ret[7] = (byte) (num);
      }
      else{
        ret[7] = (byte) (num >>> 56);
        ret[6] = (byte) (num >>> 48);
        ret[5] = (byte) (num >>> 40);
        ret[4] = (byte) (num >>> 32);
        ret[3] = (byte) (num >>> 24);
        ret[2] = (byte) (num >>> 16);
        ret[1] = (byte) (num >>> 8);
        ret[0] = (byte) (num);
      }

      return Values.make(ret);
    }
  }

  // (binary x, long i, boolean big_endian=false) -> double
  public static final class float_at implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) return Values.NIL;

      long idx = i.longNum();
      if (idx < 0) return Values.NIL;
      byte[] bytes = x.bytes();
      if (idx + 3 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      int a = Byte.toUnsignedInt(bytes[intIdx]);
      int b = Byte.toUnsignedInt(bytes[intIdx + 1]);
      int c = Byte.toUnsignedInt(bytes[intIdx + 2]);
      int d = Byte.toUnsignedInt(bytes[intIdx + 3]);

      int out;
      if (bigEndian.bool()) {
        // big endian, most significant first
        out = (a << 24) | (b << 16) | (c << 8) | d;
      } else {
        // little endian, most significant last
        out = (d << 24) | (c << 16) | (b << 8) | a;
      }
      float v = Float.intBitsToFloat(out);
      return Values.make((double) v);
    }
  }

  // (double x, boolean big_endian=false) -> binary
  public static final class of_float implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) throw new LangException(LangError.NIL_ERROR, "big_endian cannot be nil");

      float num = x.doubleNum().floatValue();
      boolean isBigEndian = bigEndian.bool();
      int dword = Float.floatToIntBits(num);
      byte[] ret = new byte[4];

      if (isBigEndian){
        ret[0] = (byte) (dword >>> 24);
        ret[1] = (byte) (dword >>> 16);
        ret[2] = (byte) (dword >>> 8);
        ret[3] = (byte) (dword);
      }
      else{
        ret[3] = (byte) (dword >>> 24);
        ret[2] = (byte) (dword >>> 16);
        ret[1] = (byte) (dword >>> 8);
        ret[0] = (byte) (dword);
      }

      return Values.make(ret);
    }
  }

  // (binary x, long i, boolean big_endian=false) -> double
  public static final class double_at implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) return Values.NIL;

      long idx = i.longNum();
      if (idx < 0) return Values.NIL;
      byte[] bytes = x.bytes();
      if (idx + 7 >= bytes.length) return Values.NIL;

      int intIdx = (int) idx;
      long a = Byte.toUnsignedLong(bytes[intIdx]);
      long b = Byte.toUnsignedLong(bytes[intIdx + 1]);
      long c = Byte.toUnsignedLong(bytes[intIdx + 2]);
      long d = Byte.toUnsignedLong(bytes[intIdx + 3]);
      long e = Byte.toUnsignedLong(bytes[intIdx + 4]);
      long f = Byte.toUnsignedLong(bytes[intIdx + 5]);
      long g = Byte.toUnsignedLong(bytes[intIdx + 6]);
      long h = Byte.toUnsignedLong(bytes[intIdx + 7]);

      long out;
      if (bigEndian.bool()) {
        // big endian, most significant first
        out = (a << 56) | (b << 48) | (c << 40) | (d << 32) | (e << 24) | (f << 16) | (g << 8) | h;
      } else {
        // little endian, most significant last
        out = (h << 56) | (g << 48) | (f << 40) | (e << 32) | (d << 24) | (c << 16) | (b << 8) | a;
      }

      return Values.make(Double.longBitsToDouble(out));
    }
  }

  // (double x, boolean big_endian=false) -> binary
  public static final class of_double implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value bigEndian) {
      if (x == Values.NIL) return Values.NIL;
      if (bigEndian == Values.NIL) throw new LangException(LangError.NIL_ERROR, "big_endian cannot be nil");

      double num = x.doubleNum();
      boolean isBigEndian = bigEndian.bool();
      long bits = Double.doubleToLongBits(num);
      byte[] ret = new byte[8];

      if (isBigEndian){
        ret[0] = (byte) (bits >>> 56);
        ret[1] = (byte) (bits >>> 48);
        ret[2] = (byte) (bits >>> 40);
        ret[3] = (byte) (bits >>> 32);
        ret[4] = (byte) (bits >>> 24);
        ret[5] = (byte) (bits >>> 16);
        ret[6] = (byte) (bits >>> 8);
        ret[7] = (byte) (bits);
      }
      else{
        ret[7] = (byte) (bits >>> 56);
        ret[6] = (byte) (bits >>> 48);
        ret[5] = (byte) (bits >>> 40);
        ret[4] = (byte) (bits >>> 32);
        ret[3] = (byte) (bits >>> 24);
        ret[2] = (byte) (bits >>> 16);
        ret[1] = (byte) (bits >>> 8);
        ret[0] = (byte) (bits);
      }

      return Values.make(ret);
    }
  }

  // (binary x, long start=0, long end) -> list
  public static final class slice implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value start, Value end) {

      if (x.isNil()) return Values.NIL;
      if (start.isNil()) throw new LangException(LangError.NIL_ERROR, "Cannot slice starting at nil");
      if (start.longNum() < 0)
        throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "Cannot slice starting at: " + start.longNum());

      byte[] bytes = x.bytes();
      int size = bytes.length;
      long startIndex = start.longNum();
      long endIndex = end.isNil() ? size : end.longNum();

      if (endIndex <= startIndex) return Values.EMPTY_BINARY;
      if (startIndex >= size) return Values.EMPTY_BINARY;

      if (endIndex >= size) endIndex = size;
      if (startIndex == 0L && endIndex >= size) return x;

      byte[] slice = Arrays.copyOfRange(bytes, (int) startIndex, (int) endIndex);
      return Values.make(slice);

    }
  }

  // (binary x) -> string
  public static final class to_hex implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;
      byte[] bytes = x.bytes();
      if (bytes.length == 0) return Values.EMPTY_STRING;

      return Values.make(LangUtil.bytesToHex(bytes));

    }
  }

  // (string x) -> binary
  public static final class from_hex implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;
      String s = x.string();

      int len = s.length();
      if (len == 0) return Values.EMPTY_BINARY;

      if (len % 2 != 0) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "hex string needs to be even-length, but has " + len + " characters: " + s);
      }

      byte[] out = new byte[len / 2];

      for (int i = 0; i < len; i += 2) {

        int h = hexToBin(s.charAt(i));
        if (h == -1) {
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "hex string contains illegal character: " + s.charAt(i));
        }

        int l = hexToBin(s.charAt(i + 1));
        if (l == -1) {
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "hex string contains illegal character: " + s.charAt(i + 1));
        }

        out[i / 2] = (byte) ((h << 4) | l);
      }

      return Values.make(out);
    }
  }

  // (binary x, variant='basic') -> string
  public static final class base64_encode implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value variant) {

      if (x.isNil()) return Values.NIL;
      if (variant.isNil()) return Values.NIL;

      String variantStr = variant.string().toLowerCase();
      switch (variantStr){
        case "basic":
          return Values.make(Base64.getEncoder().encodeToString(x.bytes()));
        case "url":
          return Values.make(Base64.getUrlEncoder().encodeToString(x.bytes()));
        case "mime":
          return Values.make(Base64.getMimeEncoder().encodeToString(x.bytes()));
        default:
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "Unsupported base64 variant requested: "+variantStr);
      }
    }
  }

  // (string x, variant='basic') -> binary
  public static final class base64_decode implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value variant) {

      if (x.isNil()) return Values.NIL;
      if (variant.isNil()) return Values.NIL;

      String variantStr = variant.string().toLowerCase();
      switch (variantStr){
        case "basic":
          return Values.make(Base64.getDecoder().decode(x.string()));
        case "url":
          return Values.make(Base64.getUrlDecoder().decode(x.string()));
        case "mime":
          return Values.make(Base64.getMimeDecoder().decode(x.string()));
        default:
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "Unsupported base64 variant requested: "+variantStr);
      }
    }
  }

}
