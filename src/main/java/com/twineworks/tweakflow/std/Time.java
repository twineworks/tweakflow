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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.TimeZone;

public final class Time {

  private static Value periodToDict(Period period) {
    return Values.makeDict(
        "years", Values.make(period.getYears()),
        "months", Values.make(period.getMonths()),
        "days", Values.make(period.getDays())
    );
  }

  private static Value durationToDict(Duration duration) {
    return Values.makeDict(
        "seconds", Values.make(duration.getSeconds()),
        "nano_seconds", Values.make(duration.getNano())
    );
  }

  public static boolean isValidTimeZone(final String timeZone) {
    final String DEFAULT_GMT_TIMEZONE = "GMT";
    if (timeZone.equals(DEFAULT_GMT_TIMEZONE)) {
      return true;
    } else {
      // if custom time zone is invalid,
      // time zone id returned is always "GMT" by default
      String id = TimeZone.getTimeZone(timeZone).getID();
      return !id.equals(DEFAULT_GMT_TIMEZONE);
    }
  }

  // function years_between: (datetime start_inclusive, datetime end_exclusive) -> long
  public static final class yearsBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();

      return Values.make(ChronoUnit.YEARS.between(start.getZoned(), end.getZoned()));
    }
  }

  // function months_between: (datetime start_inclusive, datetime end_exclusive) -> long
  public static final class monthsBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();

      return Values.make(ChronoUnit.MONTHS.between(start.getZoned(), end.getZoned()));
    }
  }

  // function days_between: (datetime start_inclusive, datetime end_exclusive) -> long
  public static final class daysBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();


      return Values.make(ChronoUnit.DAYS.between(start.getZoned(), end.getZoned()));
    }
  }

  // function hours_between: (datetime start_inclusive, datetime end_exclusive) -> long
  public static final class hoursBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();

      return Values.make(ChronoUnit.HOURS.between(start.getZoned(), end.getZoned()));
    }
  }

  // function minutes_between: (datetime start_inclusive, datetime end_exclusive) -> long
  public static final class minutesBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();

      return Values.make(ChronoUnit.MINUTES.between(start.getZoned(), end.getZoned()));
    }
  }

  // function seconds_between: (datetime start_inclusive, datetime end_exclusive) -> long
  public static final class secondsBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();

      return Values.make(ChronoUnit.SECONDS.between(start.getZoned(), end.getZoned()));
    }
  }

  // function periods_between: function(datetime start_inclusive, datetime end_exclusive) -> dict
  public static final class periodBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();

      return periodToDict(
          Period.between(start.getLocal().toLocalDate(), end.getLocal().toLocalDate())
      );
    }
  }

  // function duration_between: (datetime start_inclusive, datetime end_exclusive) -> dict
  public static final class durationBetween implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value startInclusive, Value endExclusive) {

      if (startInclusive == Values.NIL) return Values.NIL;
      if (endExclusive == Values.NIL) return Values.NIL;

      DateTimeValue start = startInclusive.dateTime();
      DateTimeValue end = endExclusive.dateTime();

      return durationToDict(
          Duration.between(start.getZoned(), end.getZoned())
      );
    }
  }

  // function add_period(datetime start, long years, long months, long days) -> datetime
  public static final class addPeriod implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value start, Value years, Value months, Value days) {

      if (start == Values.NIL) return Values.NIL;
      if (years == Values.NIL) return Values.NIL;
      if (months == Values.NIL) return Values.NIL;
      if (days == Values.NIL) return Values.NIL;

      Long yearsLong = years.longNum();
      Long monthsLong = months.longNum();
      Long daysLong = days.longNum();

      DateTimeValue startTime = start.dateTime();

      try {
        return Values.make(new DateTimeValue(
            startTime.getZoned()
                .plusYears(yearsLong)
                .plusMonths(monthsLong)
                .plusDays(daysLong)));

      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }
    }
  }

  // function add_duration: (datetime start, long seconds=0, long nano_of_second=0) -> datetime
  public static final class addDuration implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value start, Value seconds, Value nanoOfSecond) {

      if (start == Values.NIL) return Values.NIL;
      if (seconds == Values.NIL) return Values.NIL;
      if (nanoOfSecond == Values.NIL) return Values.NIL;

      Long secondsLong = seconds.longNum();
      Long nanosLong = nanoOfSecond.longNum();

      DateTimeValue startTime = start.dateTime();

      try {
        return Values.make(new DateTimeValue(
            startTime.getZoned()
                .plusSeconds(secondsLong)
                .plusNanos(nanosLong)));

      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }
    }
  }

  // function year(datetime x) -> long
  public static final class year implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getYear());
    }
  }

  // function month(datetime x) -> long
  public static final class month implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getMonthValue());
    }
  }

  // function day_of_month(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfMonth"}
  public static final class dayOfMonth implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getDayOfMonth());
    }
  }

  // function day_of_year(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfYear"}
  public static final class dayOfYear implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getDayOfYear());
    }
  }

  // function day_of_week(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$dayOfWeek"}
  public static final class dayOfWeek implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getDayOfWeek().getValue());
    }
  }

  // function hour(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$hour"}
  public static final class hour implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getHour());
    }
  }

  // function minute(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$minute"}
  public static final class minute implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getMinute());
    }
  }

  // function second(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$second"}
  public static final class second implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getSecond());
    }
  }

  // function nano_of_second(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$nanoOfSecond"}
  public static final class nanoOfSecond implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getNano());
    }
  }

  // function week_of_year(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$weekOfYear"}
  public static final class weekOfYear implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getLong(WeekFields.ISO.weekOfWeekBasedYear()));
    }
  }

  // function offset_seconds(datetime x) -> long via {:class "com.twineworks.tweakflow.std.Time$offsetSeconds"}
  public static final class offsetSeconds implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getOffset().get(ChronoField.OFFSET_SECONDS));
    }
  }

  // function zone(datetime x) -> string via {:class "com.twineworks.tweakflow.std.Time$zone"}
  public static final class zone implements UserFunction, Arity1UserFunction {
    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(x.dateTime().getZoned().getZone().getId());
    }
  }

  public static final class formatter_impl implements UserFunction, Arity1UserFunction {

    private final DateTimeFormatter formatter;

    public formatter_impl(DateTimeFormatter formatter) {
      this.formatter = formatter;
    }

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;

      return Values.make(formatter.format(x.dateTime().getZoned()));
    }
  }

  // (string pattern, string lang) -> function
  public static final class formatter implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value pattern, Value lang) {

      if (pattern == Values.NIL) {
        throw new LangException(LangError.NIL_ERROR, "pattern cannot be nil");
      }

      if (lang == Values.NIL) {
        throw new LangException(LangError.NIL_ERROR, "language tag cannot be nil");
      }

      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.string(), java.util.Locale.forLanguageTag(lang.string()));

        return Values.make(
            new UserFunctionValue(
                new FunctionSignature(Collections.singletonList(
                    new FunctionParameter(0, "x", Types.DATETIME, Values.NIL)),
                    Types.STRING),
                new formatter_impl(formatter)));

      } catch (IllegalArgumentException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid datetime format pattern: " + e.getMessage());
      }

    }
  }

  public static final class parser_impl implements UserFunction, Arity1UserFunction {

    private final DateTimeFormatter formatter;

    public parser_impl(DateTimeFormatter formatter) {
      this.formatter = formatter;
    }

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;

      DateTimeValue dt = null;

      try {
        dt = new DateTimeValue(ZonedDateTime.parse(x.string(), formatter));
      } catch (DateTimeParseException e1) {
        try {
          dt = new DateTimeValue(LocalDateTime.parse(x.string(), formatter).atZone(formatter.getZone()));
        } catch (DateTimeParseException e2) {
          try {
            dt = new DateTimeValue(LocalDate.parse(x.string(), formatter).atStartOfDay().atZone(formatter.getZone()));
          } catch (DateTimeParseException e3) {
            throw new LangException(LangError.ILLEGAL_ARGUMENT, e3.getMessage());
          }
        }
      }

      return Values.make(dt);
    }
  }

  // (string pattern, string lang, string default_tz, boolean lenient) -> function
  public static final class parser implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value pattern, Value lang, Value default_tz, Value lenient) {

      if (pattern == Values.NIL) {
        throw new LangException(LangError.NIL_ERROR, "pattern cannot be nil");
      }

      if (lang == Values.NIL) {
        throw new LangException(LangError.NIL_ERROR, "language tag cannot be nil");
      }

      if (lenient == Values.NIL) {
        throw new LangException(LangError.NIL_ERROR, "lenient flag cannot be nil");
      }

      if (default_tz == Values.NIL) {
        throw new LangException(LangError.NIL_ERROR, "default_tz cannot be nil");
      }

      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.string(), java.util.Locale.forLanguageTag(lang.string()));

        if (lenient == Values.TRUE) {
          formatter = formatter.withResolverStyle(ResolverStyle.LENIENT);
        } else {
          formatter = formatter.withResolverStyle(ResolverStyle.STRICT);
        }

        try {
          formatter = formatter.withZone(ZoneId.of(default_tz.string()));
        } catch (DateTimeException e) {
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid time zone: " + e.getMessage());
        }

        return Values.make(
            new UserFunctionValue(
                new FunctionSignature(Collections.singletonList(
                    new FunctionParameter(0, "x", Types.STRING, Values.NIL)),
                    Types.DATETIME),
                new parser_impl(formatter)));

      } catch (IllegalArgumentException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid datetime format pattern: " + e.getMessage());
      }

    }
  }

  // () -> list
  public static final class zones implements UserFunction, Arity0UserFunction {

    @Override
    public Value call(UserCallContext context) {

      ListValue list = new ListValue();
      for (String tz : TimeZone.getAvailableIDs()) {
        list = list.append(Values.make(tz));
      }

      return Values.make(list);

    }
  }

  // function with_year(datetime x, long year) -> datetime
  public static final class withYear implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value year) {

      if (x == Values.NIL) return Values.NIL;
      if (year == Values.NIL) return Values.NIL;

      Long y = year.longNum();
      if ((long) y.intValue() != y) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "year value out of range: " + y);
      }
      DateTimeValue t = x.dateTime();

      try {
        return Values.make(new DateTimeValue(t.getZoned().withYear(y.intValue())));
      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }

    }
  }

  // function with_month(datetime x, long month) -> datetime
  public static final class withMonth implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value month) {

      if (x == Values.NIL) return Values.NIL;
      if (month == Values.NIL) return Values.NIL;

      Long m = month.longNum();
      if ((long) m.intValue() != m) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "month value out of range: " + m);
      }
      DateTimeValue t = x.dateTime();

      try {
        return Values.make(new DateTimeValue(t.getZoned().withMonth(m.intValue())));
      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }

    }
  }

  // function with_day_of_month(datetime x, long day_of_month) -> datetime
  public static final class withDayOfMonth implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value day_of_month) {

      if (x == Values.NIL) return Values.NIL;
      if (day_of_month == Values.NIL) return Values.NIL;

      Long d = day_of_month.longNum();
      if ((long) d.intValue() != d) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "day_of_month value out of range: " + d);
      }
      DateTimeValue t = x.dateTime();

      try {
        return Values.make(new DateTimeValue(t.getZoned().withDayOfMonth(d.intValue())));
      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }

    }
  }

  // function with_hour(datetime x, long hour) -> datetime
  public static final class withHour implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value hour) {

      if (x == Values.NIL) return Values.NIL;
      if (hour == Values.NIL) return Values.NIL;

      Long d = hour.longNum();
      if ((long) d.intValue() != d) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "hour value out of range: " + d);
      }
      DateTimeValue t = x.dateTime();

      try {
        return Values.make(new DateTimeValue(t.getZoned().withHour(d.intValue())));
      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }

    }
  }

  // function with_minute(datetime x, long minute) -> datetime
  public static final class withMinute implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value minute) {

      if (x == Values.NIL) return Values.NIL;
      if (minute == Values.NIL) return Values.NIL;

      Long m = minute.longNum();
      if ((long) m.intValue() != m) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "minute value out of range: " + m);
      }
      DateTimeValue t = x.dateTime();

      try {
        return Values.make(new DateTimeValue(t.getZoned().withMinute(m.intValue())));
      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }

    }
  }

  // function with_second(datetime x, long second) -> datetime
  public static final class withSecond implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value second) {

      if (x == Values.NIL) return Values.NIL;
      if (second == Values.NIL) return Values.NIL;

      Long m = second.longNum();
      if ((long) m.intValue() != m) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "second value out of range: " + m);
      }
      DateTimeValue t = x.dateTime();

      try {
        return Values.make(new DateTimeValue(t.getZoned().withSecond(m.intValue())));
      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }

    }
  }

  // function with_nano_of_second(datetime x, long second) -> datetime
  public static final class withNanoOfSecond implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value nano_of_second) {

      if (x == Values.NIL) return Values.NIL;
      if (nano_of_second == Values.NIL) return Values.NIL;

      Long n = nano_of_second.longNum();
      if ((long) n.intValue() != n) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "nano_of_second value out of range: " + n);
      }
      DateTimeValue t = x.dateTime();

      try {
        return Values.make(new DateTimeValue(t.getZoned().withNano(n.intValue())));
      } catch (DateTimeException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, e.getMessage());
      }

    }
  }

  // (datetime x, string tz) -> datetime
  public static final class withTz implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value tz) {

      if (x == Values.NIL) return Values.NIL;
      if (tz == Values.NIL) return Values.NIL;

      DateTimeValue t = x.dateTime();
      String tzId = tz.string();

      if (isValidTimeZone(tzId)) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        return Values.make(new DateTimeValue(t.getZoned().withZoneSameLocal(timeZone.toZoneId())));
      } else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "unknown time zone id: " + tzId);
      }

    }
  }

  // (datetime x, string tz) -> datetime
  public static final class sameInstantAtZone implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value tz) {

      if (x == Values.NIL) return Values.NIL;
      if (tz == Values.NIL) return Values.NIL;

      DateTimeValue t = x.dateTime();
      String tzId = tz.string();

      if (isValidTimeZone(tzId)) {
        TimeZone timeZone = TimeZone.getTimeZone(tzId);
        return Values.make(new DateTimeValue(t.getZoned().withZoneSameInstant(timeZone.toZoneId())));
      } else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "unknown time zone id: " + tzId);
      }

    }
  }
}
