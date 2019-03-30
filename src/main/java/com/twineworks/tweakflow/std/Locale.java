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
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

import java.text.DecimalFormatSymbols;

public final class Locale {

  // function locales () -> dict via {:class "com.twineworks.tweakflow.std.Locales$languages"}
  public static final class languages implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value localeForDisplayNames) {

      TransientDictValue dict = new TransientDictValue();

      java.util.Locale locale;
      if (localeForDisplayNames == Values.NIL){
        locale = java.util.Locale.US;
      }
      else{
        locale = java.util.Locale.forLanguageTag(localeForDisplayNames.string());
      }

      java.util.Locale[] availableLocales = java.util.Locale.getAvailableLocales();
      for (java.util.Locale loc : availableLocales) {
        String tag = loc.toLanguageTag();
        String displayName = loc.getDisplayName(locale);
        dict.put(tag, Values.make(displayName));
      }

      return Values.make(dict.persistent());
    }
  }

  // function decimal_symbols(lang='en-US') -> dict
  public static final class decimalSymbols implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value lang) {

      TransientDictValue dict = new TransientDictValue();

      java.util.Locale locale;
      if (lang == Values.NIL){
        locale = java.util.Locale.US;
      }
      else{
        locale = java.util.Locale.forLanguageTag(lang.string());
      }

      DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);

      dict.put("exponent_separator", Values.make(symbols.getExponentSeparator()));
      dict.put("grouping_separator", Values.make(symbols.getGroupingSeparator()));
      dict.put("decimal_separator", Values.make(symbols.getDecimalSeparator()));
      dict.put("nan", Values.make(symbols.getNaN()));
      dict.put("infinity", Values.make(symbols.getInfinity()));
      dict.put("minus_sign", Values.make(symbols.getMinusSign()));
      dict.put("zero_digit", Values.make(symbols.getZeroDigit()));
      return Values.make(dict.persistent());
    }
  }

  public static DecimalFormatSymbols decimalSymbolsFromDict(DictValue dict){

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(java.util.Locale.US);

    // exponent separator
    Value vExponentSeparator = dict.get("exponent_separator");
    if (vExponentSeparator == Values.NIL) throw new LangException(LangError.NIL_ERROR, "exponent_separator must not be nil");
    String exponentSeparator = vExponentSeparator.castTo(Types.STRING).string();
    if (exponentSeparator.length() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "exponent_separator must not be empty");

    symbols.setExponentSeparator(exponentSeparator);

    // grouping separator
    Value vGroupingSeparator = dict.get("grouping_separator");
    if (vGroupingSeparator == Values.NIL) throw new LangException(LangError.NIL_ERROR, "grouping_separator must not be nil");
    String groupingSeparator = vGroupingSeparator.castTo(Types.STRING).string();
    if (groupingSeparator.length() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "grouping_separator must not be empty");
    if (groupingSeparator.length() > 1) throw new LangException(LangError.ILLEGAL_ARGUMENT, "grouping_separator must consist of exactly one character from the BMP (Basic Multilingual Plane)");

    symbols.setGroupingSeparator(groupingSeparator.charAt(0));

    // grouping separator
    Value vDecimalSeparator = dict.get("decimal_separator");
    if (vDecimalSeparator == Values.NIL) throw new LangException(LangError.NIL_ERROR, "decimal_separator must not be nil");
    String decimalSeparator = vDecimalSeparator.castTo(Types.STRING).string();
    if (decimalSeparator.length() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "decimal_separator must not be empty");
    if (decimalSeparator.length() > 1) throw new LangException(LangError.ILLEGAL_ARGUMENT, "decimal_separator must consist of exactly one character from the BMP (Basic Multilingual Plane)");

    symbols.setDecimalSeparator(decimalSeparator.charAt(0));

    // nan
    Value vNan = dict.get("nan");
    if (vNan == Values.NIL) throw new LangException(LangError.NIL_ERROR, "nan must not be nil");
    String nan = vNan.castTo(Types.STRING).string();
    if (nan.length() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "nan must not be empty");
    symbols.setNaN(nan);

    // infinity
    Value vInfinity = dict.get("infinity");
    if (vInfinity == Values.NIL) throw new LangException(LangError.NIL_ERROR, "infinity must not be nil");
    String infinity = vInfinity.castTo(Types.STRING).string();
    if (infinity.length() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "infinity must not be empty");
    symbols.setInfinity(infinity);

    // minus sign
    Value vMinusSign = dict.get("minus_sign");
    if (vMinusSign == Values.NIL) throw new LangException(LangError.NIL_ERROR, "minus_sign must not be nil");
    String minusSign = vMinusSign.castTo(Types.STRING).string();
    if (minusSign.length() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "minus_sign must not be empty");
    if (minusSign.length() > 1) throw new LangException(LangError.ILLEGAL_ARGUMENT, "minus_sign must consist of exactly one character from the BMP (Basic Multilingual Plane)");

    symbols.setMinusSign(minusSign.charAt(0));

    // zero digit
    Value vZeroDigit = dict.get("zero_digit");
    if (vZeroDigit == Values.NIL) throw new LangException(LangError.NIL_ERROR, "zero_digit must not be nil");
    String zeroDigit = vZeroDigit.castTo(Types.STRING).string();
    if (zeroDigit.length() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "zero_digit must not be empty");
    if (zeroDigit.length() > 1) throw new LangException(LangError.ILLEGAL_ARGUMENT, "zero_digit must consist of exactly one character from the BMP (Basic Multilingual Plane)");

    symbols.setZeroDigit(zeroDigit.charAt(0));

    return symbols;
  }

}
