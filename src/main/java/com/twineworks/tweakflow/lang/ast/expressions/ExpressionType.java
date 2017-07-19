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

package com.twineworks.tweakflow.lang.ast.expressions;

public enum ExpressionType {
  PRIMITIVE,
  REFERENCE,
  IF,
  IS,
  CAST,
  LIST,
  DICT,
  FUNCTION,
  CALL,
  LET,
  CONTAINER_ACCESS,
  PLUS,
  MINUS,
  EQUAL,
  NOT,
  MULT,
  INT_DIV,
  DIV,
  MOD,
  POW,
  NEGATE,
  AND,
  OR,
  LESS_THAN,
  LESS_THAN_OR_EQUAL,
  GREATER_THAN,
  GREATER_THAN_OR_EQUAL,
  STRING_CONCAT,
  LIST_CONCAT,
  DICT_MERGE,
  DEBUG,
  BITWISE_NOT,
  BITWISE_AND,
  BITWISE_OR,
  BITWISE_XOR,
  BITWISE_SHIFT_LEFT,
  BITWISE_PRESERVING_SHIFT_RIGHT,
  BITWISE_ZERO_SHIFT_RIGHT,
  THROW,
  TYPEOF,
  DEFAULT,
  FOR,
  MATCH,
  IDENTICAL, NOT_IDENTICAL, NOT_EQUAL, TRY_CATCH
}
