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

package com.twineworks.tweakflow.lang.errors;

public enum LangError implements ErrorCode {

  OK,
  UNKNOWN_ERROR,
  PARSE_ERROR,
  IO_ERROR,
  CANNOT_FIND_MODULE,
  CANNOT_FIND_EXPORT,
  INVALID_IMPORT_PATH,
  INVALID_REFERENCE_TARGET,
  UNRESOLVED_REFERENCE,
  ALREADY_DEFINED,
  CYCLIC_REFERENCE,
  INCOMPATIBLE_TYPES,
  NUMBER_OUT_OF_BOUNDS,
  LITERAL_VALUE_REQUIRED,
  CAST_ERROR,
  CANNOT_CALL,
  USER_CLASS_SPEC_MISSING,
  USER_CLASS_INCOMPATIBLE,
  UNEXPECTED_ARGUMENT,
  CUSTOM_ERROR,
  INDEX_OUT_OF_BOUNDS,
  NIL_ERROR,
  DIVISION_BY_ZERO,
  DEFAULT_PATTERN_NOT_LAST,
  ILLEGAL_ARGUMENT,
  INVALID_DATETIME,
  NATIVE_CODE_RESTRICTED;

  @Override
  public String getName() {
    return this.name();
  }

}
