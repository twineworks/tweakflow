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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;

public class ValueOutputStream implements AutoCloseable{

  private final DataOutputStream ds;

  public ValueOutputStream(OutputStream out){
    this.ds = new DataOutputStream(out);
  }

  public void write(Value v) throws IOException {
    byte magicByte = v.type().getId();
    switch(magicByte){
      case MagicNumbers.Format.VOID: ds.writeByte(magicByte); break;
      case MagicNumbers.Format.BOOLEAN: ds.writeByte(magicByte); ds.writeByte(v.bool() ? (byte)1 : (byte)0); break;
      case MagicNumbers.Format.BINARY: ds.writeByte(magicByte); ds.writeInt(v.bytes().length); ds.write(v.bytes());break;
      case MagicNumbers.Format.LONG: ds.writeByte(magicByte); ds.writeLong(v.longNum()); break;
      case MagicNumbers.Format.DOUBLE: ds.writeByte(magicByte); ds.writeDouble(v.doubleNum());break;
      case MagicNumbers.Format.DECIMAL: ds.writeByte(magicByte); ds.writeUTF(v.decimal().toString()); break;
      case MagicNumbers.Format.STRING:
        ds.writeByte(magicByte);
        ds.writeUTF(v.string());
        break;
      case MagicNumbers.Format.DATETIME:
        ds.writeByte(magicByte);
        ZonedDateTime dt = v.dateTime().getZoned();
        Instant di = v.dateTime().getInstant();
        ds.writeLong(di.getEpochSecond());
        ds.writeInt(di.getNano());
        ds.writeUTF(dt.getZone().getId());
        break;

      case MagicNumbers.Format.LIST:
        ds.writeByte(magicByte);
        ListValue list = v.list();
        ds.writeInt(list.size());
        for (Value value : list) {
          write(value);
        }
        break;

      case MagicNumbers.Format.DICT:
        ds.writeByte(magicByte);
        DictValue dict = v.dict();
        ds.writeInt(dict.size());
        Iterator<Map.Entry<String, Value>> iter = dict.entryIterator();
        while(iter.hasNext()){
          Map.Entry<String, Value> entry = iter.next();
          ds.writeUTF(entry.getKey());
          write(entry.getValue());
        }
        break;
      case MagicNumbers.Format.FUNCTION:
        throw new IOException("Cannot serialize function values, found: "+ ValueInspector.inspect(v, true));
      default:
        throw new IOException("Unknown value type: "+v.type().name());
    }
  }

  public void flush() throws IOException {
    ds.flush();
  }

  @Override
  public void close() {
    try {
      ds.close();
    } catch (IOException ignored){
    }
  }

}
