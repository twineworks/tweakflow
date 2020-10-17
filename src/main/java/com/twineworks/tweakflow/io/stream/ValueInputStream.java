/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Twineworks GmbH
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

package com.twineworks.tweakflow.io.stream;

import com.twineworks.tweakflow.io.MagicNumbers;
import com.twineworks.tweakflow.lang.values.*;
import com.twineworks.tweakflow.util.LRUCache;

import java.io.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ValueInputStream implements AutoCloseable {

  private final DataInputStream ds;
  private final LRUCache<String, Value> stringCache;
  private final LRUCache<Long, Value> longCache;
  private final LRUCache<String, String> keyCache;

  public ValueInputStream(InputStream in) {
    this(in, 0);
  }

  public ValueInputStream(InputStream in, int cacheSize) {
    this.ds = new DataInputStream(in);
    if (cacheSize > 0){
      stringCache = new LRUCache<>(cacheSize);
      longCache = new LRUCache<>(cacheSize);
      keyCache = new LRUCache<>(cacheSize);
    }
    else{
      stringCache = null;
      longCache = null;
      keyCache = null;
    }
  }

  private Value makeString(String str){
    if (stringCache != null){
      Value v = stringCache.get(str);
      if (v == null){
        v = Values.make(str);
        stringCache.put(str, v);
      }
      return v;
    }
    else {
      return Values.make(str);
    }
  }

  private Value makeLong(Long num){
    if (longCache != null){
      Value v = longCache.get(num);
      if (v == null){
        v = Values.make(num);
        longCache.put(num, v);
      }
      return v;
    }
    else {
      return Values.make(num);
    }
  }

  private String getKey(String k){
    if (keyCache != null){
      String v = keyCache.get(k);
      if (v == null){
        v = k;
        keyCache.put(k, k);
      }
      return v;
    }
    else {
      return k;
    }
  }

  public Value read() throws IOException {
    byte magicByte = ds.readByte();

    switch (magicByte) {
      case MagicNumbers.Format.VOID:
        return Values.NIL;
      case MagicNumbers.Format.BOOLEAN:
        return Values.make(ds.readByte() != 0);
      case MagicNumbers.Format.BINARY:
        int len = ds.readInt();
        byte[] bytes = new byte[len];
        ds.readFully(bytes);
        return Values.make(bytes);
      case MagicNumbers.Format.LONG:
        return makeLong(ds.readLong());
      case MagicNumbers.Format.DOUBLE:
        return Values.make(ds.readDouble());
      case MagicNumbers.Format.DECIMAL:
        return Values.make(new BigDecimal(ds.readUTF()));
      case MagicNumbers.Format.STRING:
        return makeString(ds.readUTF());
      case MagicNumbers.Format.DATETIME:
        long epochSeconds = ds.readLong();
        int nanos = ds.readInt();
        ZoneId zoneId = ZoneId.of(getKey(ds.readUTF()));
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds, nanos), zoneId);
        return Values.make(new DateTimeValue(zonedDateTime));
      case MagicNumbers.Format.LIST:
        int size = ds.readInt();
        Value[] items = new Value[size];
        for (int i = 0; i < size; i++) {
          items[i] = read();
        }
        return Values.make(new ListValue(items));
      case MagicNumbers.Format.DICT:

        TransientDictValue t = new TransientDictValue();
        int count = ds.readInt();
        for (int i = 0; i < count; i++) {
          t.put(getKey(ds.readUTF()), read());
        }
        return Values.make(t.persistent());

      default:
        throw new IOException("Unknown value type: " + magicByte);
    }
  }

  @Override
  public void close() {
    try {
      if (stringCache != null){
        stringCache.clear();
      }
      if (longCache != null){
        longCache.clear();
      }
      if(keyCache != null){
        keyCache.clear();
      }
      ds.close();
    } catch (IOException ignored) {
    }

  }
}
