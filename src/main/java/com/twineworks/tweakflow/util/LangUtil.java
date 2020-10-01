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

import com.twineworks.tweakflow.lang.ast.expressions.DateTimeNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.values.DateTimeValue;

import java.time.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.identifier;
import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class LangUtil {

  private final static Pattern safeKeyName = Pattern.compile("([.]?[-+/a-zA-Z_0-9?]+)+");
  private final static Pattern safeIdentifier = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9?]*");
  private final static Pattern safeTimeZoneIdentifier = Pattern.compile("[a-zA-Z_]+(/[a-zA-Z_0-9?]+)+");

  private final static String REGEX_DATE = "(?:\\+|-)?\\d{1,10}-\\d+-\\d+";
  private final static String REGEX_TIME = "(?:\\d+:\\d+:\\d+(?:\\.\\d+)?)";
  private final static String REGEX_OFFSET = "(?:(?:(?:\\+|-)\\d+:\\d+)|Z)";
  private final static String REGEX_TZ = "@(?:.+)";
  private final static Pattern literalDatetimePattern = Pattern.compile("^"+REGEX_DATE+"T(?:"+REGEX_TIME+"(?:"+REGEX_OFFSET+"|(?:"+REGEX_OFFSET+REGEX_TZ+")|"+REGEX_TZ+")?)?$");

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
      "binary",
      "long",
      "dict",
      "list",
      "double",
      "decimal",
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

  public static String escapeSingleQuotedString(String s){
    String escaped = s.replace("'", "''");
    return escaped;
  }


  public static String getSingleQuotedStringLiteral(String s){
    return "'"+escapeSingleQuotedString(s)+"'";
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

    // prefer '$foo' over :`$foo`
    if (!id.contains("'")){
      return getSingleQuotedStringLiteral(id);
    }

    // prefer "foo's" over :`foo's`
    if (!id.contains("\"")){
      return getStringLiteral(id);
    }

    if (id.contains("`")){
      return getStringLiteral(id);
    }

    return ":`"+id+"`";
  }

  /**
   * Returns whether the given string has a chance to parse correctly
   * using toDateTime()
   * @param str
   * @return
   */
  public static boolean isPossiblyDateTime(String str){
    return literalDatetimePattern.matcher(str).matches();
  }

  /**
   * Converts a string conforming to language rules to a datetime value.
   * Suitable to parse a string as provided by the lexer.
   *
   * @param str the string to convert to a datetime
   * @param sourceInfo location of source in case we're parsing sourcecode
   * @return
   */
  public static DateTimeValue toDateTime(String str, SourceInfo sourceInfo){

    // 2017-03-17T16:04:02.123456789+01:00@`Europe/Berlin`

    // lexer provides a token that may not be valid
    // the parser can provide more helpful error messages in case of an 'almost right' datetime notation
    // here's the guarantees:

    // some digits for year -> year must have at least 4 digits to prevent usage of things like 19-02-23T
    //                         as this would not be 2019, it would be 19 CE, need to write 0019-02-23T to mean 19 CE
    //                         year can have a maximum of 9 digits
    // some digits for month          -> one or two digits are allowed
    // some digits for day_of month   -> one or two digits are allowed

    // T separator is guaranteed to be present

    // time is optional
    // some digits for hour           -> one or two digits are allowed
    // some digits for minute         -> one or two digits are allowed
    // some digits for second         -> one or two digits are allowed

    // . fraction is optional
    // some digits for nano_of_second -> one to nine digits are allowed

    // offset is optional, if it is present, it can be
    // Z
    //
    // +|- some digits for offset hour    -> 1 or 2 digits are allowed
    // +|- some digits for offset minute  -> 1 or 2 digits are allowed


    int idxT = str.indexOf('T');

    // default time
    int hour = 0;
    int minute = 0;
    int second = 0;
    int nanoOfSecond = 0;

    // -- date part --

    // date part are first digits up to T
    String date = str.substring(0, idxT);

    boolean isNeg = date.startsWith("-");

    // temporarily strip off the leading '-' for processing convenience
    if (isNeg) {
      date = date.substring(1);
    }
    // strip off unnecessary + at year beginning, if present
    else if (date.startsWith("+")) {
      date = date.substring(1);
    }

    String[] parts = date.split("-");
    String yearPart = parts[0];
    String monthPart = parts[1];
    String dayOfMonthPart = parts[2];

    if (yearPart.length() < 4) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid year: year must consist of at least four digits, provide the century or leading zeros", sourceInfo);
    } else if (yearPart.length() > 9) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid year: year must not exceed nine digits", sourceInfo);
    }

    int year = Integer.parseInt(yearPart, 10);

    if (monthPart.length() > 2) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid month: month must consist of one or two digits", sourceInfo);
    }

    int month = Integer.parseInt(monthPart, 10);

    if (dayOfMonthPart.length() > 2) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid day of month: day of month must consist of one or two digits", sourceInfo);
    }
    int dayOfMonth = Integer.parseInt(dayOfMonthPart, 10);

    if (isNeg) {
      year = -year;
    }

    // -- time part --

    // defaults
    String offset = null;
    String tz = "UTC";

    int len = str.length();

    // optional parts
    if (len > idxT + 1) {
      // scan until +-h+:m+ offset, Z offset, or @tzName
      String timeSection = str.substring(idxT + 1);
      int timeLen = timeSection.length();

      // shortest time sig: 0:0:0
      int clockTimeLen = 5;
      for (int i=5;i<timeLen;i++){
        char c = timeSection.charAt(i);
        // still in clockTime?
        if (c >= '0' && c <= '9' || c == ':' || c == '.'){
          clockTimeLen = i+1;
        }
        else{
          // out of clockTime
          break;
        }
      }
      // got h+:m+:s+(.f+) in clockTime
      String clockTime = timeSection.substring(0, clockTimeLen);
      String[] clockTimeParts = clockTime.split(":");
      String hourPart = clockTimeParts[0];
      String minutePart = clockTimeParts[1];
      String secondPart = clockTimeParts[2];
      String fractionPart = null;

      int dotPos = secondPart.indexOf(".");
      if (dotPos >= 0){
        fractionPart = secondPart.substring(dotPos+1);
        secondPart = secondPart.substring(0, dotPos);
      }

      if (hourPart.length() > 2){
        throw new LangException(LangError.INVALID_DATETIME, "invalid hour: hour must consist of one or two digits", sourceInfo);
      }

      if (minutePart.length() > 2){
        throw new LangException(LangError.INVALID_DATETIME, "invalid minute: minute must consist of one or two digits", sourceInfo);
      }

      if (secondPart.length() > 2){
        throw new LangException(LangError.INVALID_DATETIME, "invalid second: second must consist of one or two digits", sourceInfo);
      }

      if (fractionPart != null && fractionPart.length() > 9){
        throw new LangException(LangError.INVALID_DATETIME, "invalid fraction of second: fraction of second must not exceed nine digits", sourceInfo);
      }

      hour = Integer.parseInt(hourPart, 10);
      minute = Integer.parseInt(minutePart, 10);
      second = Integer.parseInt(secondPart, 10);

      // need to convert fraction digits to nano digits
      // i.e. given fraction 999 -> 999_000_000ns
      if (fractionPart != null){
        int additionalDigits = 9 - fractionPart.length();
        if (additionalDigits > 0){
          // quick and dirty addition of additionalDigits 0s
          char[] zeros = new char[additionalDigits];
          Arrays.fill(zeros, '0');
          fractionPart = fractionPart+new String(zeros);
        }
      }

      nanoOfSecond = fractionPart == null ?  0 : Integer.parseInt(fractionPart, 10);


      String offsetTime = timeSection.substring(clockTimeLen);

      if (offsetTime.length() > 0) {

        // -- offset part --
        // possibilities are +-h+:m+, Z, or empty implying Z
        char offsetStartChar = offsetTime.charAt(0);
        int offsetLen = 0;
        if (offsetStartChar == '+' || offsetStartChar=='-'){

          // offset given by +-digits:digits
          offsetLen = 4;
          for (int i=4;i<offsetTime.length();i++){
            char c = offsetTime.charAt(i);
            // still in offsetTime?
            if (c >= '0' && c <= '9' || c == ':'){
              offsetLen = i+1;
            }
            else{
              // out of offsetTime
              break;
            }
          }

          // contains (+|-)h+:m+
          String offsetClock = offsetTime.substring(0, offsetLen);
          // require length of 2 for both hours and minutes
          int splitPos = offsetClock.indexOf(':');
          String offsetHour = offsetClock.substring(1, splitPos);
          String offsetMinute = offsetClock.substring(splitPos+1);

          if (offsetHour.length() > 2){
            throw new LangException(LangError.INVALID_DATETIME, "invalid offset hour: offset hour must consist of one or two digits", sourceInfo);
          }

          if (offsetMinute.length() > 2){
            throw new LangException(LangError.INVALID_DATETIME, "invalid offset minute: offset minute must consist of one or two digits", sourceInfo);
          }

          // normalize offset hour and minute to length 2
          if (offsetHour.length() < 2){
            offsetHour = "0"+offsetHour;
          }

          if (offsetMinute.length() < 2){
            offsetMinute = "0"+offsetMinute;
          }

          // construct offset
          offset = offsetClock.charAt(0)+offsetHour+":"+offsetMinute;

        }
        else if (offsetStartChar == 'Z'){
          // offset is 00:00
          offset = "Z";
          offsetLen = 1;
        }
        else if (offsetStartChar == '@'){
          // offset is empty, implying default zone offset
          offset = null;
          offsetLen = 0;
        }

        String tzNameTime = offsetTime.substring(offsetLen);

        // offset & timezone
        if (tzNameTime.length() > 0) {
          // time zone given?
          tz = identifier(tzNameTime.substring(1));
        }
        // no timezone given, but an offset is there
        else{
          if (offset == null || offset.equals("Z")) {
            tz = "UTC";
          } else {
            tz = "UTC" + offset;
          }
        }

      }

    }


    try {
      // create a zoned time, using strict rules for all parts
      LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
      LocalTime localTime = LocalTime.of(hour, minute, second, nanoOfSecond);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      ZonedDateTime zonedDateTime;
      if (offset == null){
        zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(tz));
      }
      else{
        zonedDateTime = ZonedDateTime.ofStrict(localDateTime, ZoneOffset.of(offset), ZoneId.of(tz));
      }

      DateTimeValue dateTime = new DateTimeValue(zonedDateTime);

      return dateTime;

    } catch (DateTimeException e) {
      throw new LangException(LangError.INVALID_DATETIME, e.getMessage(), sourceInfo);
    }
  }

}
