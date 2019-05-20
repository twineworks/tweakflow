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

import com.twineworks.tweakflow.lang.values.DateTimeValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

class DatetimeSerializer implements ValueSerializer, ValueDeserializer {

  private Value subject;

  private boolean writtenInstant = false;
  private boolean writtenTzSize = false;

  private boolean readInstant = false;
  private boolean readTzSize = false;

  private long epochSeconds;
  private int nanos;

  private ByteBuffer tzBytes;
  private ZoneId lastZoneId;

  @Override
  public void setSubject(Value subject) {
    this.subject = subject;
    ZoneId zoneId = subject.dateTime().getZoned().getZone();

    if (!zoneId.equals(lastZoneId)){
      lastZoneId = zoneId;
      tzBytes = StandardCharsets.UTF_8.encode(zoneId.getId());
    }
    else{
      tzBytes.position(0);
    }

    writtenInstant = false;
    writtenTzSize = false;
  }

  @Override
  public boolean put(Out out, ByteBuffer buffer) {

    if (!writtenInstant){
      if (buffer.remaining() >= 13){
        buffer.put(MagicNumbers.Format.DATETIME);
        Instant instant = subject.dateTime().getInstant();
        long epochSecond = instant.getEpochSecond();
        int nanos = instant.getNano();
        buffer.putLong(epochSecond);
        buffer.putInt(nanos);
        writtenInstant = true;
      }
      else{
        return false;
      }
    }

    if (!writtenTzSize){
      if (buffer.remaining() >= 4){
        buffer.putInt(tzBytes.limit());
        writtenTzSize = true;
      }
      else{
        return false;
      }
    }

    // need to write the tz?
    while (tzBytes.hasRemaining() && buffer.hasRemaining()){
      buffer.put(tzBytes.get());
    }

    return !tzBytes.hasRemaining();

  }

  @Override
  public Value getSubject() {
    return subject;
  }

  private boolean getFromDatetimeFormat(ByteBuffer buffer){

    if (!readInstant){
      if (buffer.remaining() >= 12){
        epochSeconds = buffer.getLong();
        nanos = buffer.getInt();
        readInstant = true;
      }
      else{
        return false;
      }
    }

    if (!readTzSize){
      if (buffer.remaining() >= 4){
        int tzSize = buffer.getInt();
        if (tzBytes == null || tzBytes.capacity() < tzSize){
          tzBytes = ByteBuffer.allocate(tzSize);
        }
        else{
          tzBytes.position(0);
          tzBytes.limit(tzSize);
        }
        readTzSize = true;
      }
      else{
        return false;
      }
    }

    while(buffer.hasRemaining() && tzBytes.hasRemaining()){
      tzBytes.put(buffer.get());
    }

    if (tzBytes.hasRemaining()) return false;

    tzBytes.position(0);
    CharBuffer decoded = StandardCharsets.UTF_8.decode(tzBytes);
    ZoneId zoneId = ZoneId.of(decoded.toString());

    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds, nanos), zoneId);
    subject = Values.make(new DateTimeValue(zonedDateTime));

    readInstant = false;
    readTzSize = false;

    return true;

  }

  @Override
  public boolean get(In in, byte format, ByteBuffer buffer) {
    switch (format){
      case MagicNumbers.Format.DATETIME: {
        return getFromDatetimeFormat(buffer);
      }
      default: {
        throw new AssertionError("Unknown format: "+format);
      }
    }
  }
}
