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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.tweakflow.util.LangUtil;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;

public class DateTimeValue {

  private final Instant instant;
  private final LocalDateTime local;
  private final OffsetDateTime offset;
  private final ZonedDateTime zoned;

  private static final DateTimeFormatter leadFormatter = new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .toFormatter();

  private static final DateTimeFormatter regionIdFormatter = new DateTimeFormatterBuilder()
      .parseCaseSensitive()
      .appendZoneId()
      .toFormatter();

  public DateTimeValue(Instant instant) {
    this.instant = instant;
    zoned = instant.atZone(ZoneId.of("UTC"));
    offset = zoned.toOffsetDateTime();
    local = zoned.toLocalDateTime();
  }

  public DateTimeValue(LocalDateTime local) {
    this.local = local;
    this.zoned = local.atZone(ZoneId.of("UTC"));
    this.instant = zoned.toInstant();
    this.offset = zoned.toOffsetDateTime();
  }

  public DateTimeValue(OffsetDateTime offset) {
    this.offset = offset;
    this.local = offset.toLocalDateTime();
    this.instant = offset.toInstant();
    this.zoned = offset.toZonedDateTime();
  }

  public DateTimeValue(ZonedDateTime zoned) {
    this.zoned = zoned;
    this.offset = zoned.toOffsetDateTime();
    this.local = zoned.toLocalDateTime();
    this.instant = zoned.toInstant();
  }

  public Instant getInstant() {
    return instant;
  }

  public LocalDateTime getLocal() {
    return local;
  }

  public OffsetDateTime getOffset() {
    return offset;
  }

  public ZonedDateTime getZoned() {
    return zoned;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DateTimeValue that = (DateTimeValue) o;
    return Objects.equals(zoned, that.zoned);
  }

  @Override
  public int hashCode() {
    return Objects.hash(zoned);
  }

  @Override
  public String toString() {
    String lead = leadFormatter.format(zoned);
    String zone = regionIdFormatter.format(zoned);
    return lead+"@"+LangUtil.escapeIdentifier(zone);
  }
}
